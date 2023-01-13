package org.strykeforce.swerve;

import static com.ctre.phoenix.motorcontrol.ControlMode.MotionMagic;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
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
 * A swerve module that uses Talons for azimuth and drive motors. Uses a {@link Builder} to
 * construct.
 *
 * <pre>
 * TalonSwerveModule module =
 *   new TalonSwerveModule.Builder()
 *       .azimuthTalon(azimuthTalon)
 *       .driveTalon(driveTalon)
 *       .driveGearRatio(kDriveGearRatio)
 *       .wheelDiameterInches(kWheelDiameterInches)
 *       .driveMaximumMetersPerSecond(kMaxSpeedMetersPerSecond)
 *       .wheelLocationMeters(kWheelLocationMeters)
 *       .build();
 * </pre>
 */
public class TalonSwerveModule implements SwerveModule {

  private static final Logger logger = LoggerFactory.getLogger(TalonSwerveModule.class);

  final int k100msPerSecond = 10;

  private final TalonSRX azimuthTalon;
  private final BaseTalon driveTalon;
  private final double azimuthCountsPerRev;
  private final double driveCountsPerRev;
  private final double driveGearRatio;
  private final double wheelCircumferenceMeters;
  private final double driveDeadbandMetersPerSecond;
  private final double driveMaximumMetersPerSecond;
  private final Translation2d wheelLocationMeters;

  private Rotation2d previousAngle = new Rotation2d();

  private TalonSwerveModule(Builder builder) {
    azimuthTalon = builder.azimuthTalon;
    driveTalon = builder.driveTalon;
    azimuthCountsPerRev = builder.azimuthCountsPerRev;
    driveCountsPerRev = builder.driveCountsPerRev;
    driveGearRatio = builder.driveGearRatio;
    wheelCircumferenceMeters = Math.PI * Units.inchesToMeters(builder.wheelDiameterInches);
    driveDeadbandMetersPerSecond = builder.driveDeadbandMetersPerSecond;
    driveMaximumMetersPerSecond = builder.driveMaximumMetersPerSecond;
    wheelLocationMeters = builder.wheelLocationMeters;
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

    if (isDriveOpenLoop) {
      setDriveOpenLoopMetersPerSecond(optimizedState.speedMetersPerSecond);
    } else {
      setDriveClosedLoopMetersPerSecond(optimizedState.speedMetersPerSecond);
    }
  }

  @Override
  public void resetDriveEncoder() {
    var errorCode = driveTalon.setSelectedSensorPosition(0);
    if (errorCode.value != 0) {
      logger.error("Talon error code while resetting encoder to 0: {}", errorCode);
    }
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
    if (errorCode.value != 0) {
      logger.error("Talon error code while setting azimuth zero: {}", errorCode);
    }

    azimuthTalon.set(MotionMagic, azimuthSetpoint);
    logger.info("swerve module {}: set azimuth encoder = {}", index, azimuthSetpoint);
  }

  public TalonSRX getAzimuthTalon() {
    return azimuthTalon;
  }

  public BaseTalon getDriveTalon() {
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
    // minimize change in heading by potentially reversing the drive direction
    Rotation2d currentAngle = getAzimuthRotation2d();
    SwerveModuleState optimizedState = SwerveModuleState.optimize(desiredState, currentAngle);

    // set the azimuth wheel position
    double countsBefore = azimuthTalon.getSelectedSensorPosition();
    double countsFromAngle =
        optimizedState.angle.getRadians() / (2.0 * Math.PI) * azimuthCountsPerRev;
    double countsDelta = Math.IEEEremainder(countsFromAngle - countsBefore, azimuthCountsPerRev);
    azimuthTalon.set(MotionMagic, countsBefore + countsDelta);

    // save previous angle for use if inside deadband in setDesiredState()
    previousAngle = optimizedState.angle;
    return optimizedState;
  }

  private double getDriveMetersPerSecond() {
    double encoderCountsPer100ms = driveTalon.getSelectedSensorVelocity();
    double motorRotationsPer100ms = encoderCountsPer100ms / driveCountsPerRev;
    double wheelRotationsPer100ms = motorRotationsPer100ms * driveGearRatio;
    double metersPer100ms = wheelRotationsPer100ms * wheelCircumferenceMeters;
    return metersPer100ms * k100msPerSecond;
  }

  private double getDrivePositionMeters() {
    double encoderTicks = driveTalon.getSelectedSensorPosition();
    double motorPositionTicks = encoderTicks / driveCountsPerRev;
    double wheelPositionTicks = motorPositionTicks * driveGearRatio;
    double wheelPositionMeters = wheelPositionTicks * wheelCircumferenceMeters;
    return wheelPositionMeters;
  }

  private void setDriveOpenLoopMetersPerSecond(double metersPerSecond) {
    driveTalon.set(ControlMode.PercentOutput, metersPerSecond / driveMaximumMetersPerSecond);
  }

  private void setDriveClosedLoopMetersPerSecond(double metersPerSecond) {
    double wheelRotationsPerSecond = metersPerSecond / wheelCircumferenceMeters;
    double motorRotationsPerSecond = wheelRotationsPerSecond / driveGearRatio;
    double encoderCountsPerSecond = motorRotationsPerSecond * driveCountsPerRev;
    driveTalon.set(ControlMode.Velocity, encoderCountsPerSecond / k100msPerSecond);
  }

  private int getWheelIndex() {
    if (wheelLocationMeters.getX() > 0 && wheelLocationMeters.getY() > 0) {
      return 0;
    }
    if (wheelLocationMeters.getX() > 0 && wheelLocationMeters.getY() < 0) {
      return 1;
    }
    if (wheelLocationMeters.getX() < 0 && wheelLocationMeters.getY() > 0) {
      return 2;
    }
    return 3;
  }

  @Override
  public void registerWith(@NotNull TelemetryService telemetryService) {
    telemetryService.register(azimuthTalon);
    telemetryService.register(driveTalon);
  }

  @Override
  public String toString() {
    return "TalonSwerveModule{" + getWheelIndex() + '}';
  }

  public static class Builder {

    public static final int kDefaultTalonSRXCountsPerRev = 4096;
    public static final int kDefaultTalonFXCountsPerRev = 2048;
    private final int azimuthCountsPerRev = kDefaultTalonSRXCountsPerRev;
    private TalonSRX azimuthTalon;
    private BaseTalon driveTalon;
    private double driveGearRatio;
    private double wheelDiameterInches;
    private int driveCountsPerRev = kDefaultTalonFXCountsPerRev;
    private double driveDeadbandMetersPerSecond = -1.0;
    private double driveMaximumMetersPerSecond;
    private Translation2d wheelLocationMeters;

    public Builder() {}

    public Builder azimuthTalon(TalonSRX azimuthTalon) {
      this.azimuthTalon = azimuthTalon;
      return this;
    }

    public Builder driveTalon(BaseTalon driveTalon) {
      this.driveTalon = driveTalon;
      if (driveTalon instanceof TalonFX) {
        driveCountsPerRev = kDefaultTalonFXCountsPerRev;
        return this;
      }

      if (driveTalon instanceof TalonSRX) {
        driveCountsPerRev = kDefaultTalonSRXCountsPerRev;
        return this;
      }

      throw new IllegalArgumentException("expect drive talon is TalonFX or TalonSRX");
    }

    public Builder driveGearRatio(double ratio) {
      driveGearRatio = ratio;
      return this;
    }

    public Builder wheelDiameterInches(double diameterInches) {
      wheelDiameterInches = diameterInches;
      return this;
    }

    public Builder driveEncoderCountsPerRevolution(int countsPerRev) {
      driveCountsPerRev = countsPerRev;
      return this;
    }

    public Builder driveDeadbandMetersPerSecond(double metersPerSecond) {
      driveDeadbandMetersPerSecond = metersPerSecond;
      return this;
    }

    // we currently only support TalonSRX for azimuth
    //    public Builder azimuthEncoderCountsPerRevolution(int countsPerRev) {
    //      azimuthCountsPerRev = countsPerRev;
    //      return this;
    //    }

    public Builder driveMaximumMetersPerSecond(double metersPerSecond) {
      driveMaximumMetersPerSecond = metersPerSecond;
      return this;
    }

    public Builder wheelLocationMeters(Translation2d locationMeters) {
      wheelLocationMeters = locationMeters;
      return this;
    }

    public TalonSwerveModule build() {
      if (driveDeadbandMetersPerSecond < 0) {
        driveDeadbandMetersPerSecond = 0.01 * driveMaximumMetersPerSecond;
      }
      var module = new TalonSwerveModule(this);
      validateTalonSwerveModuleObject(module);
      return module;
    }

    private void validateTalonSwerveModuleObject(TalonSwerveModule module) {
      if (module.azimuthTalon == null) {
        throw new IllegalArgumentException("azimuth talon must be set.");
      }

      if (module.driveGearRatio <= 0) {
        throw new IllegalArgumentException("drive gear ratio must be greater than zero.");
      }

      if (module.azimuthCountsPerRev <= 0) {
        throw new IllegalArgumentException(
            "azimuth encoder counts per revolution must be greater than zero.");
      }

      if (module.driveCountsPerRev <= 0) {
        throw new IllegalArgumentException(
            "drive encoder counts per revolution must be greater than zero.");
      }

      if (module.wheelCircumferenceMeters <= 0) {
        throw new IllegalArgumentException("wheel diameter must be greater than zero.");
      }

      if (module.driveMaximumMetersPerSecond <= 0) {
        throw new IllegalArgumentException("drive maximum speed must be greater than zero.");
      }

      if (module.wheelLocationMeters == null) {
        throw new IllegalArgumentException("wheel location must be set.");
      }

      if (module.driveTalon instanceof TalonFX
          && module.driveCountsPerRev != kDefaultTalonFXCountsPerRev) {
        logger.warn("drive TalonFX counts per rev = {}", module.driveCountsPerRev);
      }

      if (module.driveTalon instanceof TalonSRX
          && module.driveCountsPerRev != kDefaultTalonSRXCountsPerRev) {
        logger.warn("drive TalonSRX counts per rev = {}", module.driveCountsPerRev);
      }
    }
  }
}
