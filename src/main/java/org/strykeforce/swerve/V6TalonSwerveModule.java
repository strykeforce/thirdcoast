package org.strykeforce.swerve;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.VelocityDutyCycle;
import com.ctre.phoenix6.controls.VelocityTorqueCurrentFOC;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Preferences;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.telemetry.TelemetryService;

/**
 * A swerve module that uses Talon SRX's for azimuth motors and Talon FX's for drive motors. Uses a
 * {@link V6Builder} to construct.
 *
 * <pre>
 *     V6TalonSwerveModule module =
 *     new V6TalonSwerveModule.Builder()
 *          .azimuthTalon(azimuthTalon)
 *          .driveTalon(driveTalon)
 *          .wheelDiameterInches(kWheelDiameterInches)
 *          .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
 *          .wheelLocationMeters(kWheelLocationMeters)
 *          .build();
 * </pre>
 */
public class V6TalonSwerveModule implements SwerveModule {

  private static final Logger logger = LoggerFactory.getLogger(V6TalonSwerveModule.class);

  private final int k100msPerSecond = 10;

  private final TalonSRX azimuthTalon;

  private final TalonFX driveTalon;

  private final double azimuthCountsPerRev;

  private final double driveCountsPerRev;

  private final double driveGearRatio;

  private final double wheelCircumferenceMeters;

  private final double driveDeadbandMetersPerSecond;

  private final double driveMaximumMetersPerSecond;

  private final Translation2d wheelLocationMeters;

  private Rotation2d previousAngle = new Rotation2d();

  private final ClosedLoopUnits units;

  private final boolean enableFOC;

  private int closedLoopSlot;

  private boolean compensateLatency;

  private V6TalonSwerveModule(V6Builder builder) {

    azimuthTalon = builder.azimuthTalon;
    driveTalon = builder.driveTalon;
    azimuthCountsPerRev = builder.azimuthCountsPerRev;
    driveCountsPerRev = builder.driveCountsPerRev;
    driveGearRatio = builder.driveGearRatio;
    wheelCircumferenceMeters = Math.PI * Units.inchesToMeters(builder.wheelDiameterInches);
    driveDeadbandMetersPerSecond = builder.driveDeadbandMetersPerSecond;
    driveMaximumMetersPerSecond = builder.driveMaximumMetersPerSecond;
    wheelLocationMeters = builder.wheelLocationMeters;
    units = builder.units;
    enableFOC = builder.enableFOC;
    closedLoopSlot = builder.closedLoopSlot;
    compensateLatency = builder.compensateLatency;
    resetDriveEncoder();
  }

  @Override
  public double getMaxSpeedMetersPerSecond() {
    return driveMaximumMetersPerSecond;
  }

  @Override
  public Translation2d getWheelLocationMeters() {
    return wheelLocationMeters;
  }

  public double getDriveCountsPerRev() {
    return driveCountsPerRev;
  }

  @Override
  public SwerveModuleState getState() {
    double speedMetersPerSecond = getDriveMetersPerSecond();
    Rotation2d angle = getAzimuthRotation2d();
    return new SwerveModuleState(speedMetersPerSecond, angle);
  }

  @Override
  public SwerveModulePosition getPosition() {
    double wheelPositionMeters = getDrivePositionMeters();
    Rotation2d angle = getAzimuthRotation2d();
    return new SwerveModulePosition(wheelPositionMeters, angle);
  }

  @Override
  public void setDesiredState(SwerveModuleState desiredState, boolean isDriveOpenLoop) {
    assert desiredState.speedMetersPerSecond >= 0.0;

    if (desiredState.speedMetersPerSecond < driveDeadbandMetersPerSecond) {
      desiredState = new SwerveModuleState(0.0, previousAngle);
    }

    SwerveModuleState optimizedState = setAzimuthOptimizedState(desiredState);

    if (isDriveOpenLoop) setDriveOpenLoopMetersPerSecond(optimizedState.speedMetersPerSecond);
    else setDriveClosedLoopMetersPerSecond(optimizedState.speedMetersPerSecond);
  }

  @Override
  public void resetDriveEncoder() {
    StatusCode error = driveTalon.setPosition(0.0);
    if (error != StatusCode.OK) {
      logger.error("Drive talon error code while resetting encoder to zero: {}", error.getName());
    }
  }

  public void setClosedLoopDriveSlot(int slot) {
    if (slot < 0 || slot > 2) throw new IllegalArgumentException("slot must be between 0 and 2");
    closedLoopSlot = slot;
  }

  @Override
  public void storeAzimuthZeroReference() {
    int index = getWheelIndex();
    int position = getAzimuthAbsoluteEncoderCounts();
    String key = String.format("SwerveDrive/wheel.%d", index);
    Preferences.setInt(key, position);
    logger.info("azimuth {}: saved zero = {}", index, position);
  }

  @Override
  public void loadAndSetAzimuthZeroReference() {
    int index = getWheelIndex();
    String key = String.format("SwerveDrive/wheel.%d", index);
    int reference = Preferences.getInt(key, Integer.MIN_VALUE);
    if (reference == Integer.MIN_VALUE) {
      logger.error("no saved azimuth zero reference for swerve module {}", index);
    }
    logger.info("swerve module {}: loaded azimuth zero reference = {}", index, reference);

    int azimuthAbsoluteCounts = getAzimuthAbsoluteEncoderCounts();
    logger.info("swerve module {}: azimuth absolute position = {}", index, azimuthAbsoluteCounts);

    int azimuthSetpoint = azimuthAbsoluteCounts - reference;
    ErrorCode errorCode = azimuthTalon.setSelectedSensorPosition(azimuthSetpoint, 0, 10);
    if (errorCode.value != 0)
      logger.error("Talon error code while setting azimuth zero: {}", errorCode);

    azimuthTalon.set(TalonSRXControlMode.MotionMagic, azimuthSetpoint);
    logger.info("swervve module {}: set azimuth enoder = {}", index, azimuthSetpoint);
  }

  public TalonSRX getAzimuthTalon() {
    return azimuthTalon;
  }

  public TalonFX getDriveTalon() {
    return driveTalon;
  }

  private int getAzimuthAbsoluteEncoderCounts() {
    return azimuthTalon.getSensorCollection().getPulseWidthPosition() & 0xFFF;
  }

  @Override
  public Rotation2d getAzimuthRotation2d() {
    double azimuthCounts = azimuthTalon.getSelectedSensorPosition();
    double radians = 2.0 * Math.PI * azimuthCounts / azimuthCountsPerRev;
    return new Rotation2d(radians);
  }

  @Override
  public void setAzimuthRotation2d(Rotation2d angle) {
    setAzimuthOptimizedState(new SwerveModuleState(0.0, angle));
    logger.info("azimuth {}: set rotation to: {}", azimuthTalon.getDeviceID(), angle);
  }

  @NotNull
  private SwerveModuleState setAzimuthOptimizedState(SwerveModuleState desiredState) {
    // minimize heading change by potentially reversing drive direction
    Rotation2d currentAngle = getAzimuthRotation2d();
    SwerveModuleState optimizedState = SwerveModuleState.optimize(desiredState, currentAngle);

    // set azimuth wheel position
    double countsBefore = azimuthTalon.getSelectedSensorPosition();
    double countsFromAngle =
        optimizedState.angle.getRadians() / (2.0 * Math.PI) * azimuthCountsPerRev;
    double countsDelta = Math.IEEEremainder(countsFromAngle - countsBefore, azimuthCountsPerRev);
    azimuthTalon.set(ControlMode.MotionMagic, countsBefore + countsDelta);

    // save previous angle for use if inside deadband in setDesiredState()
    previousAngle = optimizedState.angle;
    return optimizedState;
  }

  private double getDriveMetersPerSecond() {
    double shaftVelocity = driveTalon.getVelocity().getValue(); // rotations per second
    double motorRotations = shaftVelocity / driveCountsPerRev; // default = 1.0 for counts
    double wheelRotations = motorRotations * driveGearRatio;
    double metersPerSecond = wheelRotations * wheelCircumferenceMeters;
    return metersPerSecond;
  }

  private double getDrivePositionMeters() {
    double latency = driveTalon.getPosition().getTimestamp().getLatency();
    double shaftPosition = driveTalon.getPosition().getValue(); // rotations
    double motorPosition = shaftPosition / driveCountsPerRev; // default = 1.0 for counts
    double wheelPosition = motorPosition * driveGearRatio;
    double wheelPositionMeters = wheelPosition * wheelCircumferenceMeters;
    double velocityMetersPerSecond = getDriveMetersPerSecond();
    double wheelPositionMetersComp = wheelPositionMeters + latency * velocityMetersPerSecond;
    return compensateLatency ? wheelPositionMetersComp : wheelPositionMeters;
  }

  private void setDriveClosedLoopMetersPerSecond(double metersPerSecond) {
    double wheelRotationsPerSecond = metersPerSecond / wheelCircumferenceMeters;
    double motorRotationsPerSecond = wheelRotationsPerSecond / driveGearRatio;
    double encoderCountsPerSecond = motorRotationsPerSecond * driveCountsPerRev;
    if (units == ClosedLoopUnits.DUTY_CYCLE) {
      VelocityDutyCycle controlRequest =
          new VelocityDutyCycle(encoderCountsPerSecond)
              .withEnableFOC(enableFOC)
              .withSlot(closedLoopSlot);
      driveTalon.setControl(controlRequest);
    } else if (units == ClosedLoopUnits.VOLTAGE) {
      VelocityVoltage controlRequest =
          new VelocityVoltage(encoderCountsPerSecond)
              .withEnableFOC(enableFOC)
              .withSlot(closedLoopSlot);
      driveTalon.setControl(controlRequest);
    } else {
      VelocityTorqueCurrentFOC controlRequest =
          new VelocityTorqueCurrentFOC(encoderCountsPerSecond).withSlot(closedLoopSlot);
      driveTalon.setControl(controlRequest);
    }
  }

  private void setDriveOpenLoopMetersPerSecond(double metersPerSecond) {
    DutyCycleOut controlRequest = new DutyCycleOut(metersPerSecond / driveMaximumMetersPerSecond);
    driveTalon.setControl(controlRequest);
  }

  private int getWheelIndex() {
    if (wheelLocationMeters.getX() > 0 && wheelLocationMeters.getY() > 0) return 0;
    if (wheelLocationMeters.getX() > 0 && wheelLocationMeters.getY() < 0) return 1;
    if (wheelLocationMeters.getX() < 0 && wheelLocationMeters.getY() > 0) return 2;
    return 3;
  }

  @Override
  public void registerWith(@NotNull TelemetryService telemetryService) {
    telemetryService.register(azimuthTalon);
    telemetryService.register(driveTalon, false);
  }

  @Override
  public String toString() {
    return "V6TalonSwerveModule{" + getWheelIndex() + "}";
  }

  public static class V6Builder {
    public static final int kDefaultTalonSRXCountsPerRev = 4096;
    public static final double kDefaultTalonFXCountsPerRev = 1.0;

    private int azimuthCountsPerRev = kDefaultTalonSRXCountsPerRev;
    private double driveCountsPerRev = kDefaultTalonFXCountsPerRev;

    private TalonSRX azimuthTalon;

    private TalonFX driveTalon;

    private double driveGearRatio;

    private double wheelDiameterInches;

    private double driveDeadbandMetersPerSecond = -1.0;

    private double driveMaximumMetersPerSecond;

    private Translation2d wheelLocationMeters;

    private ClosedLoopUnits units = ClosedLoopUnits.DUTY_CYCLE;

    private boolean enableFOC = false;

    private int closedLoopSlot = 0;

    private boolean compensateLatency = false;

    public V6Builder() {}

    public V6Builder azimuthTalon(TalonSRX azimuthTalon) {
      this.azimuthTalon = azimuthTalon;
      return this;
    }

    public V6Builder driveTalon(TalonFX driveTalon) {
      this.driveTalon = driveTalon;
      return this;
    }

    public V6Builder driveGearRatio(double ratio) {
      driveGearRatio = ratio;
      return this;
    }

    public V6Builder wheelDiameterInches(double diameterInches) {
      wheelDiameterInches = diameterInches;
      return this;
    }

    public V6Builder driveEncoderCountsPerRevolution(double countsPerRev) {
      driveCountsPerRev = countsPerRev;
      return this;
    }

    public V6Builder azimuthEncoderCountsPerRevolution(int countsPerRev) {
      azimuthCountsPerRev = countsPerRev;
      return this;
    }

    public V6Builder driveDeadbandMetersPerSecond(double metersPerSecond) {
      driveDeadbandMetersPerSecond = metersPerSecond;
      return this;
    }

    public V6Builder driveMaximumMetersPerSecond(double metersPerSecond) {
      driveMaximumMetersPerSecond = metersPerSecond;
      return this;
    }

    public V6Builder wheelLocationMeters(Translation2d locationMeters) {
      wheelLocationMeters = locationMeters;
      return this;
    }

    public V6Builder closedLoopUnits(ClosedLoopUnits unit) {
      units = unit;
      return this;
    }

    public V6Builder enableFOC(boolean enableFOC) {
      this.enableFOC = enableFOC;
      return this;
    }

    public V6Builder closedLoopSlot(int slot) {
      this.closedLoopSlot = slot;
      return this;
    }

    public V6Builder latencyCompensation(boolean compensate) {
      this.compensateLatency = compensate;
      return this;
    }

    public V6TalonSwerveModule build() {
      if (driveDeadbandMetersPerSecond < 0) {
        driveDeadbandMetersPerSecond = 0.01 * driveMaximumMetersPerSecond;
      }
      var module = new V6TalonSwerveModule(this);
      validateSwerveModuleObject(module);
      return module;
    }

    private void validateSwerveModuleObject(V6TalonSwerveModule module) {
      if (module.azimuthTalon == null)
        throw new IllegalArgumentException("azimuth talon must be set.");

      if (module.driveTalon == null) throw new IllegalArgumentException("drive talon must be set.");

      if (module.driveGearRatio <= 0)
        throw new IllegalArgumentException("drive gear ratio must be greater than zero.");

      if (module.azimuthCountsPerRev <= 0)
        throw new IllegalArgumentException(
            "azimuth encoder counts per revolution must be greater than zero.");

      if (module.driveCountsPerRev <= 0)
        throw new IllegalArgumentException(
            "drive encoder counts per revolution must be greater than zero.");

      if (module.wheelCircumferenceMeters <= 0)
        throw new IllegalArgumentException("wheel diameter must be greater than zero.");

      if (module.driveMaximumMetersPerSecond <= 0)
        throw new IllegalArgumentException("drive maximum speed must be greater than zero.");

      if (module.wheelLocationMeters == null)
        throw new IllegalArgumentException("wheel location must be set.");

      if (closedLoopSlot > 2 || closedLoopSlot < 0)
        throw new IllegalArgumentException("slot index must be between 0 and 2");

      if (!enableFOC && units == ClosedLoopUnits.TORQUE_CURRENT)
        throw new IllegalArgumentException("torque current control requires FOC enabled");

      if (module.driveCountsPerRev != kDefaultTalonFXCountsPerRev)
        logger.warn("drive talonFX counts per rev = {}", module.driveCountsPerRev);

      if (module.azimuthCountsPerRev != kDefaultTalonSRXCountsPerRev)
        logger.warn("azimuth talonSRX counts per rev = {}", module.azimuthCountsPerRev);
    }
  }

  public enum ClosedLoopUnits {
    DUTY_CYCLE,
    VOLTAGE,
    TORQUE_CURRENT
  }
}
