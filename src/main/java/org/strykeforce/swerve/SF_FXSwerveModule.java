package org.strykeforce.swerve;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignalCollection;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.hardware.TalonFXS;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.units.measure.*;
import edu.wpi.first.wpilibj.Preferences;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.controller.motorControl.SF_TalonFX;
import org.strykeforce.controller.motorControl.SF_TalonFXS;
import org.strykeforce.telemetry.TelemetryService;

/**
 * A swerve module that uses the Stryke Force wrappers for Talon FXS's for azimuth motors and Talon
 * FX's for drive motors. Uses a {@link FXSwerveModule.FXBuilder} to construct.
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
public class SF_FXSwerveModule implements SwerveModule {
  private static final Logger logger = LoggerFactory.getLogger(FXSwerveModule.class);
  private final SF_TalonFXS azimuthTalon;
  private final SF_TalonFX driveTalon;
  private final double azimuthCountsPerRev;
  private final double driveCountsPerRev;
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
  private boolean encoderOpposed;
  StatusSignalCollection signals = new StatusSignalCollection();

  // Status Signals
  //  private StatusSignal<Angle> drivePosition;
  //  private StatusSignal<AngularVelocity> driveVelocity;
  //  private StatusSignal<Angle> azimuthPosition;

  private SF_FXSwerveModule(SF_FXSwerveModule.SF_FXBuilder builder) {
    azimuthTalon = builder.azimuthTalon;
    driveTalon = builder.driveTalon;
    azimuthCountsPerRev = builder.azimuthCountsPerRev;
    driveCountsPerRev = builder.driveCountsPerRev;
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
    encoderOpposed = builder.encoderOpposed;
    resetDriveEncoder();
  }

  private void attachStatusSigs() {
    driveTalon.registerPosition();
    driveTalon.registerVelocity();
    azimuthTalon.registerPosition();
    azimuthTalon.registerRawPulseWidthPosition(50.0);
    signals.addSignals(driveTalon.getRegisteredSignals());
    signals.addSignals(azimuthTalon.getRegisteredSignals());
    //        drivePosition = driveTalon.getTalonFX().getPosition();
    //        driveVelocity = driveTalon.getTalonFX().getVelocity();
    //        azimuthPosition = azimuthTalon.getTalonFXS().getPosition();
  }

  @Override
  public void refreshMotorControllers() {
    signals.refreshAll();
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
  public void setDesiredState(
      SwerveModuleState desiredState, boolean isDriveOpenLoop, double accel) {
    assert desiredState.speedMetersPerSecond >= 0.0;

    if (desiredState.speedMetersPerSecond < driveDeadbandMetersPerSecond) {
      desiredState = new SwerveModuleState(0.0, previousAngle);
    }

    SwerveModuleState optimizedState = setAzimuthOptimizedState(desiredState);

    if (isDriveOpenLoop) setDriveOpenLoopMetersPerSecond(optimizedState.speedMetersPerSecond);
    else
      setDriveClosedLoopMetersPerSecond(
          MetersPerSecond.of(optimizedState.speedMetersPerSecond), accel);
  }

  @Override
  public void resetDriveEncoder() {
    StatusCode error = driveTalon.getTalonFX().setPosition(0.0);
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
    double azimuthSetpoint =
        encoderOpposed ? reference - azimuthAbsoluteCounts : azimuthAbsoluteCounts - reference;
    azimuthTalon.setPosition(azimuthSetpoint);
    //        if (error != StatusCode.OK) {
    //            azimuthTalon.setPosition(azimuthSetpoint, 0.5);
    //        }
    double position = azimuthTalon.getPosition();
    logger.info(
        "swerve module {}: set azimuth encoder = {}, actual = {}",
        index,
        azimuthSetpoint,
        position);

    setAzimuthPosition(azimuthSetpoint);
  }

  public boolean zeroAndCheck() {
    int index = getWheelIndex();
    String key = String.format("SwerveDrive/wheel.%d", index);
    double reference = Preferences.getDouble(key, Double.MIN_VALUE);
    if (reference == Double.MIN_VALUE) {
      logger.error("no saved azimuth zero reference for swerve module {}", index);
    }
    logger.info("swerve module {}: loaded azimuth zero reference = {}", index, reference);

    double azimuthAbsoluteCounts = getAzimuthAbsoluteEncoderCounts();
    logger.info("swerve module {}: azimuth absolute position = {}", index, azimuthAbsoluteCounts);
    double azimuthSetpoint =
        encoderOpposed ? reference - azimuthAbsoluteCounts : azimuthAbsoluteCounts - reference;
    azimuthTalon.setPosition(azimuthSetpoint);
    //        if (error != StatusCode.OK) {
    //            azimuthTalon.setPosition(azimuthSetpoint);
    //        }
    double position = azimuthTalon.getPosition();
    logger.info(
        "swerve module {}: set azimuth encoder = {}, actual = {}",
        index,
        azimuthSetpoint,
        position);

    setAzimuthPosition(azimuthSetpoint);
    return !(azimuthAbsoluteCounts == 1.0
        || Math.abs(position - azimuthSetpoint) > azimuthAbsoluteCounts / 100);
  }

  public TalonFXS getAzimuthTalon() {
    return azimuthTalon.getTalonFXS();
  }

  public TalonFX getDriveTalon() {
    return driveTalon.getTalonFX();
  }

  private double getAzimuthAbsoluteEncoderCounts() {
    return MathUtil.inputModulus(azimuthTalon.getRawPulseWidthPosition(), 0.0, 1.0);
  }

  private double getAzimuthEncoderPosition() {
    return azimuthTalon.getPosition();
  }

  @Override
  public Rotation2d getAzimuthRotation2d() {
    double azimuthCounts = getAzimuthEncoderPosition();
    double radians = 2.0 * Math.PI * azimuthCounts / azimuthCountsPerRev;
    return new Rotation2d(radians);
  }

  @Override
  public void setAzimuthRotation2d(Rotation2d angle) {
    setAzimuthOptimizedState(new SwerveModuleState(0.0, angle));
    //    logger.info("azimuth {}: set rotation to: {}", azimuthTalon.getDeviceID(), angle);
  }

  @NotNull
  private SwerveModuleState setAzimuthOptimizedState(SwerveModuleState desiredState) {
    // minimize heading change by potentially reversing drive direction
    Rotation2d currentAngle = getAzimuthRotation2d();
    desiredState.optimize(currentAngle);

    // setPosition
    double countsBefore = getAzimuthEncoderPosition();
    double countsFromAngle =
        desiredState.angle.getRadians() / (2.0 * Math.PI) * azimuthCountsPerRev;
    double countsDelta = Math.IEEEremainder(countsFromAngle - countsBefore, azimuthCountsPerRev);
    setAzimuthPosition(countsBefore + countsDelta);

    previousAngle = desiredState.angle;
    return desiredState;
  }

  private void setAzimuthPosition(double position) {
    azimuthTalon.runClosedLoop(position);
  }

  private LinearVelocity getDriveMetersPerSecond() {
    double shaftVelocity = driveTalon.getVelocity(); // rotations per second
    double motorRotations = shaftVelocity / driveCountsPerRev; // default = 1.0 for counts
    double wheelRotations = motorRotations * driveGearRatio;
    double metersPerSecond = wheelRotations * wheelCircumferenceMeters;
    return MetersPerSecond.of(metersPerSecond);
  }

  private Distance getDrivePositionMeters() {
    double latency = driveTalon.getPositionSig().getTimestamp().getLatency();
    double shaftPosition = driveTalon.getPosition(); // rotations
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
    driveTalon.runClosedLoop(encoderCountsPerSecond);
  }

  private void setDriveClosedLoopMetersPerSecond(
      LinearVelocity metersPerSecond, double rotationsPerSecondSquared) {
    double wheelRotationsPerSecond = metersPerSecond.in(MetersPerSecond) / wheelCircumferenceMeters;
    double motorRotationsPerSecond = wheelRotationsPerSecond / driveGearRatio;
    double encoderCountsPerSecond = motorRotationsPerSecond * driveCountsPerRev;
    driveTalon.runClosedLoop(encoderCountsPerSecond, rotationsPerSecondSquared);
  }

  private void setDriveOpenLoopMetersPerSecond(double metersPerSecond) {
    //    DutyCycleOut controlRequest = new DutyCycleOut(metersPerSecond /
    // driveMaximumMetersPerSecond);
    driveTalon.runOpenLoop(metersPerSecond / driveMaximumMetersPerSecond);
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

  public static class SF_FXBuilder {
    public static final double kDefaultTalonFXCountsPerRev = 1.0;
    private double azimuthCountsPerRev = kDefaultTalonFXCountsPerRev;
    private double driveCountsPerRev = kDefaultTalonFXCountsPerRev;

    private SF_TalonFXS azimuthTalon;
    private SF_TalonFX driveTalon;
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
    private boolean encoderOpposed = true;

    public SF_FXSwerveModule.SF_FXBuilder azimuthTalon(SF_TalonFXS azimuthTalon) {
      this.azimuthTalon = azimuthTalon;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder driveTalon(SF_TalonFX driveTalon) {
      this.driveTalon = driveTalon;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder driveGearRatio(double driveGearRatio) {
      this.driveGearRatio = driveGearRatio;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder wheelDiameterInches(double wheelDiameterInches) {
      this.wheelDiameterInches = wheelDiameterInches;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder driveEncoderCountsPerRevolution(double countsPerRev) {
      this.driveCountsPerRev = countsPerRev;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder azimuthEncoderCountsPerRevolution(double countsPerRev) {
      this.azimuthCountsPerRev = countsPerRev;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder driveDeadbandMetersPerSecond(
        double driveDeadbandMetersPerSecond) {
      this.driveDeadbandMetersPerSecond = driveDeadbandMetersPerSecond;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder driveMaximumMetersPerSecond(
        double driveMaxVelocityMetersPerSecond) {
      this.driveMaximumVelocityMetersPerSecond = driveMaxVelocityMetersPerSecond;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder wheelLocationMeters(Translation2d locationMeters) {
      wheelLocationMeters = locationMeters;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder closedLoopUnits(
        V6TalonSwerveModule.ClosedLoopUnits unit) {
      units = unit;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder enableFOC(boolean enableFOC) {
      this.enableFOC = enableFOC;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder closedLoopSlot(int slot) {
      this.closedLoopSlot = slot;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder azimuthSlot(int slot) {
      this.azimuthSlot = slot;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder latencyCompensation(boolean compensate) {
      this.compensateLatency = compensate;
      return this;
    }

    public SF_FXSwerveModule.SF_FXBuilder encoderOpposed(boolean encoderOpposed) {
      this.encoderOpposed = encoderOpposed;
      return this;
    }

    public SF_FXSwerveModule build() {
      if (driveDeadbandMetersPerSecond < 0) {
        driveDeadbandMetersPerSecond = driveMaximumVelocityMetersPerSecond * 0.01;
      }
      var module = new SF_FXSwerveModule(this);
      validateSwerveModuleObject(module);
      module.attachStatusSigs();
      return module;
    }

    private void validateSwerveModuleObject(SF_FXSwerveModule module) {
      if (module.azimuthTalon == null)
        throw new IllegalArgumentException("azimuth talon must be set");
      if (driveTalon == null) throw new IllegalArgumentException("drive talon must be set");
      if (module.driveGearRatio <= 0)
        throw new IllegalArgumentException("drive gear ratio must be greater than zero");
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
