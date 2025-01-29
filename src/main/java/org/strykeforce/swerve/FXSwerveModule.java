package org.strykeforce.swerve;

import static com.ctre.phoenix6.BaseStatusSignal.refreshAll;
import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.hardware.TalonFXS;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.units.measure.*;
import edu.wpi.first.wpilibj.Preferences;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.telemetry.TelemetryService;

/**
 * A swerve module that uses Talon FXS's for azimuth motors and Talon FX's for drive motors. Uses a
 * {@link FXBuilder} to construct.
 *
 * <pre>
 *     FXTalonSwerveModule module =
 *     new FXTalonSwerveModule.Builder()
 *          .azimuthTalon(azimuthTalon)
 *          .driveTalon(driveTalon)
 *          .wheelDiameterInches(kWheelDiameterInches)
 *          .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
 *          .wheelLocationMeters(kWheelLocationMeters)
 *          .build();
 * </pre>
 */
public class FXSwerveModule implements SwerveModule {

  private static final Logger logger = LoggerFactory.getLogger(FXSwerveModule.class);
  private final TalonFXS azimuthTalon;
  private final TalonFX driveTalon;
  private final double azimuthCountsPerRev;
  private final double driveCountsPerRev;
  private final double azimuthPulseWidthCPR;
  private final double driveGearRatio;
  private final double wheelCircumferenceMeters;
  private final double driveDeadbandMetersPerSecond;
  private final double driveMaximumMetersPerSecond;
  private final Translation2d wheelLocationMeters;
  private Rotation2d previousAngle = new Rotation2d();
  private V6TalonSwerveModule.ClosedLoopUnits units;
  private final boolean enableFOC;
  private int closedLoopSlot;
  private int azimuthSlot;
  private boolean compensateLatency;
  private double azimuthPositionOffset = 0.0;

  // Status Signals
  private StatusSignal<Angle> drivePosition;
  private StatusSignal<AngularVelocity> driveVelocity;
  private StatusSignal<Angle> azimuthPosition;

  private FXSwerveModule(FXBuilder builder) {
    azimuthTalon = builder.azimuthTalon;
    driveTalon = builder.driveTalon;
    azimuthCountsPerRev = builder.azimuthCountsPerRev;
    driveCountsPerRev = builder.driveCountsPerRev;
    azimuthPulseWidthCPR = builder.azimuthPulseWidthCPR;
    driveGearRatio = builder.driveGearRatio;
    wheelCircumferenceMeters = Math.PI * Inches.of(builder.wheelDiameterInches).in(Meters);
    driveDeadbandMetersPerSecond = builder.driveDeadbandMetersPerSecond;
    driveMaximumMetersPerSecond = builder.driveMaximumVelocityMetersPerSecond;
    wheelLocationMeters = builder.wheelLocationMeters;
    units = builder.units;
    enableFOC = builder.enableFOC;
    closedLoopSlot = builder.closedLoopSlot;
    azimuthSlot = builder.azimuthSlot;
    compensateLatency = builder.compensateLatency;
    resetDriveEncoder();
  }

  private void attachStatusSigs() {
    drivePosition = driveTalon.getPosition();
    driveVelocity = driveTalon.getVelocity();
    azimuthPosition = azimuthTalon.getPosition();
  }

  @Override
  public void refreshMotorControllers() {
    refreshAll(drivePosition, driveVelocity, azimuthPosition);
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
    double speedMetersPerSecond = getDriveMetersPerSecond().in(MetersPerSecond);
    Rotation2d angle = getAzimuthRotation2d();
    return new SwerveModuleState(speedMetersPerSecond, angle);
  }

  @Override
  public SwerveModulePosition getPosition() {
    double wheelPositionMeters = getDrivePositionMeters().in(Meters);
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
    else setDriveClosedLoopMetersPerSecond(MetersPerSecond.of(optimizedState.speedMetersPerSecond));
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
    double position = getAzimuthAbsoluteEncoderCounts();
    String key = String.format("SwerveDrive/wheel.%d", index);
    Preferences.setDouble(key, position);
    logger.info("azimuth {}: saved zero = {}", index, position);
  }

  @Override
  public void loadAndSetAzimuthZeroReference() {
    int index = getWheelIndex();
    String key = String.format("SwerveDrive/wheel.%d", index);
    double reference = Preferences.getDouble(key, Double.MIN_VALUE);
    if (reference == Double.MIN_VALUE) {
      logger.error("no saved azimuth zero reference for swerve module {}", index);
    }
    logger.info("swerve module {}: loaded azimuth zero reference = {}", index, reference);

    double azimuthAbsoluteCounts = getAzimuthAbsoluteEncoderCounts();
    logger.info("swerve module {}: azimuth absolute position = {}", index, azimuthAbsoluteCounts);
    logger.info(
        "swerve module {}: current azimuth endocder position = {}",
        index,
        azimuthAbsoluteCounts - reference);

    azimuthPositionOffset = reference / azimuthPulseWidthCPR;
    setAzimuthPosition(azimuthAbsoluteCounts);
  }

  public TalonFXS getAzimuthTalon() {
    return azimuthTalon;
  }

  public TalonFX getDriveTalon() {
    return driveTalon;
  }

  private double getAzimuthAbsoluteEncoderCounts() {
    //    double extraRotations =
    //        azimuthPosition.getValueAsDouble() > 0
    //            ? Math.floor(azimuthPosition.getValueAsDouble())
    //            : Math.ceil(azimuthPosition.getValueAsDouble());
    //    return azimuthPosition.getValueAsDouble() - extraRotations; // correct to within one
    // rotation
    return ((int) (azimuthPosition.getValueAsDouble() * azimuthPulseWidthCPR)) & 0xFFF;
  }

  private double getAzimuthEcoderPosition() {
    return azimuthPosition.getValueAsDouble() - azimuthPositionOffset;
  }

  public double getAzimuthPositionOffset() {
    return azimuthPositionOffset;
  }

  @Override
  public Rotation2d getAzimuthRotation2d() {
    double azimuthCounts = getAzimuthEcoderPosition();
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
    desiredState.optimize(currentAngle);

    // setPosition
    double countsBefore = getAzimuthEcoderPosition();
    double countsFromAngle =
        desiredState.angle.getRadians() / (2.0 * Math.PI) * azimuthCountsPerRev;
    double countsDelta = Math.IEEEremainder(countsFromAngle - countsBefore, azimuthCountsPerRev);
    setAzimuthPosition(countsBefore + countsDelta);

    previousAngle = desiredState.angle;
    return desiredState;
  }

  private void setAzimuthPosition(double position) {
    if (units == V6TalonSwerveModule.ClosedLoopUnits.DUTY_CYCLE) {
      MotionMagicDutyCycle controlRequest =
          new MotionMagicDutyCycle(position + azimuthPositionOffset)
              .withEnableFOC(false)
              .withSlot(azimuthSlot);
      azimuthTalon.setControl(controlRequest);
    } else if (units == V6TalonSwerveModule.ClosedLoopUnits.VOLTAGE) {
      MotionMagicVoltage controlReqest =
          new MotionMagicVoltage(position + azimuthPositionOffset)
              .withEnableFOC(false)
              .withSlot(azimuthSlot);
      azimuthTalon.setControl(controlReqest);
    } else {
      MotionMagicTorqueCurrentFOC controlRequest =
          new MotionMagicTorqueCurrentFOC(position + azimuthPositionOffset).withSlot(azimuthSlot);
      azimuthTalon.setControl(controlRequest);
    }
  }

  private LinearVelocity getDriveMetersPerSecond() {
    double shaftVelocity = driveVelocity.getValueAsDouble(); // rotations per second
    double motorRotations = shaftVelocity / driveCountsPerRev; // default = 1.0 for counts
    double wheelRotations = motorRotations * driveGearRatio;
    double metersPerSecond = wheelRotations * wheelCircumferenceMeters;
    return MetersPerSecond.of(metersPerSecond);
  }

  private Distance getDrivePositionMeters() {
    double latency = drivePosition.getTimestamp().getLatency();
    double shaftPosition = drivePosition.getValueAsDouble(); // rotations
    double motorPosition = shaftPosition / driveCountsPerRev; // defuault = 1.0 for counts
    double wheelPosition = motorPosition * driveGearRatio;
    double wheelPositionMeters = wheelPosition * wheelCircumferenceMeters;
    double velocityMetersPerSecond = getDriveMetersPerSecond().in(MetersPerSecond);
    double wheelPositionMetersComp = wheelPositionMeters + latency * velocityMetersPerSecond;
    return compensateLatency ? Meters.of(wheelPositionMetersComp) : Meters.of(wheelPositionMeters);
  }

  private void setDriveClosedLoopMetersPerSecond(LinearVelocity metersPerSecond) {
    double wheelRotationsPerSecond = metersPerSecond.in(MetersPerSecond) / wheelCircumferenceMeters;
    double motorRotationsPerSecond = wheelRotationsPerSecond / driveGearRatio;
    double encoderCountsPerSecond = motorRotationsPerSecond * driveCountsPerRev;
    if (units == V6TalonSwerveModule.ClosedLoopUnits.DUTY_CYCLE) {
      VelocityDutyCycle controlRequest =
          new VelocityDutyCycle(encoderCountsPerSecond)
              .withEnableFOC(enableFOC)
              .withSlot(closedLoopSlot);
      driveTalon.setControl(controlRequest);
    } else if (units == V6TalonSwerveModule.ClosedLoopUnits.VOLTAGE) {
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

  @Override
  public void registerWith(@NotNull TelemetryService telemetryService) {
    telemetryService.register(azimuthTalon, false);
    telemetryService.register(driveTalon, false);
  }

  private int getWheelIndex() {
    if (wheelLocationMeters.getX() > 0 && wheelLocationMeters.getY() > 0) return 0;
    if (wheelLocationMeters.getX() > 0 && wheelLocationMeters.getY() < 0) return 1;
    if (wheelLocationMeters.getX() < 0 && wheelLocationMeters.getY() > 0) return 2;
    return 3;
  }

  @Override
  public String toString() {
    return "FXTalonSwerveModule{" + getWheelIndex() + "}";
  }

  public static class FXBuilder {
    public static final double kDefaultTalonFXCountsPerRev = 1.0;
    private double azimuthCountsPerRev = kDefaultTalonFXCountsPerRev;
    private double driveCountsPerRev = kDefaultTalonFXCountsPerRev;
    private double azimuthPulseWidthCPR = 4096.0;

    private TalonFXS azimuthTalon;
    private TalonFX driveTalon;
    private double driveGearRatio;
    private double wheelDiameterInches;
    private double driveDeadbandMetersPerSecond = -1.0;
    private double driveMaximumVelocityMetersPerSecond;
    private Translation2d wheelLocationMeters;
    private V6TalonSwerveModule.ClosedLoopUnits units =
        V6TalonSwerveModule.ClosedLoopUnits.DUTY_CYCLE;
    private boolean enableFOC = false;
    private int closedLoopSlot = 0;
    private int azimuthSlot = 0;
    private boolean compensateLatency = false;

    public FXBuilder azimuthTalon(TalonFXS azimuthTalon) {
      this.azimuthTalon = azimuthTalon;
      return this;
    }

    public FXBuilder driveTalon(TalonFX driveTalon) {
      this.driveTalon = driveTalon;
      return this;
    }

    public FXBuilder driveGearRatio(double driveGearRatio) {
      this.driveGearRatio = driveGearRatio;
      return this;
    }

    public FXBuilder wheelDiameterInches(double wheelDiameterInches) {
      this.wheelDiameterInches = wheelDiameterInches;
      return this;
    }

    public FXBuilder driveEncoderCountsPerRevolution(double countsPerRev) {
      this.driveCountsPerRev = countsPerRev;
      return this;
    }

    public FXBuilder azimuthEncoderCountsPerRevolution(double countsPerRev) {
      this.azimuthCountsPerRev = countsPerRev;
      return this;
    }

    public FXBuilder azimuthPulseWidthCPR(double azimuthPulseWidthCPR) {
      this.azimuthPulseWidthCPR = azimuthPulseWidthCPR;
      return this;
    }

    public FXBuilder driveDeadbandMetersPerSecond(double driveDeadbandMetersPerSecond) {
      this.driveDeadbandMetersPerSecond = driveDeadbandMetersPerSecond;
      return this;
    }

    public FXBuilder driveMaximumMetersPerSecond(double driveMaxVelocityMetersPerSecond) {
      this.driveMaximumVelocityMetersPerSecond = driveMaxVelocityMetersPerSecond;
      return this;
    }

    public FXBuilder wheelLocationMeters(Translation2d locationMeters) {
      wheelLocationMeters = locationMeters;
      return this;
    }

    public FXBuilder closedLoopUnits(V6TalonSwerveModule.ClosedLoopUnits unit) {
      units = unit;
      return this;
    }

    public FXBuilder enableFOC(boolean enableFOC) {
      this.enableFOC = enableFOC;
      return this;
    }

    public FXBuilder closedLoopSlot(int slot) {
      this.closedLoopSlot = slot;
      return this;
    }

    public FXBuilder azimuthSlot(int slot) {
      this.azimuthSlot = slot;
      return this;
    }

    public FXBuilder latencyCompensation(boolean compensate) {
      this.compensateLatency = compensate;
      return this;
    }

    public FXSwerveModule build() {
      if (driveDeadbandMetersPerSecond < 0) {
        driveDeadbandMetersPerSecond = driveMaximumVelocityMetersPerSecond * 0.01;
      }
      var module = new FXSwerveModule(this);
      validateSwerveModuleObject(module);
      module.attachStatusSigs();
      return module;
    }

    private void validateSwerveModuleObject(FXSwerveModule module) {
      if (module.azimuthTalon == null)
        throw new IllegalArgumentException("azimuth talon must be set");
      if (driveTalon == null) throw new IllegalArgumentException("drive talon must be set");
      if (module.driveGearRatio <= 0)
        throw new IllegalArgumentException("drive gear ratio must be greater than zero");
      if (module.azimuthCountsPerRev <= 0)
        throw new IllegalArgumentException(
            "azimuth encoder counts per revolution must be greater than zero.");
      if (module.azimuthPulseWidthCPR <= 0)
        throw new IllegalArgumentException("azimuth pulse width cpr must be greater than zero");
      //      TalonFXSConfiguration azimuthConfig = new TalonFXSConfiguration();
      //      module.azimuthTalon.getConfigurator().refresh(azimuthConfig);
      //      if (azimuthConfig.ExternalFeedback.ExternalFeedbackSensorSource
      //          != ExternalFeedbackSensorSourceValue.PulseWidth)
      //        throw new IllegalArgumentException("Azimuth must use Pulse Width Feedback");
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

      if (azimuthSlot > 2 || azimuthSlot < 0)
        throw new IllegalArgumentException("slot index must be between 0 and 2");

      if (!enableFOC && units == V6TalonSwerveModule.ClosedLoopUnits.TORQUE_CURRENT)
        throw new IllegalArgumentException("torque current control requires FOC enabled");

      if (module.driveCountsPerRev != kDefaultTalonFXCountsPerRev)
        logger.warn("drive talonFX counts per rev = {}", module.driveCountsPerRev);

      if (module.azimuthCountsPerRev != kDefaultTalonFXCountsPerRev)
        logger.warn("azimuth talonSRX counts per rev = {}", module.azimuthCountsPerRev);
    }
  }
}
