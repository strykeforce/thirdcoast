package org.strykeforce.thirdcoast.swerve;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;
import org.strykeforce.thirdcoast.util.Settings;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Control a Third Coast swerve drive.
 *
 * <p>Wheels are injected by a Dagger provider in {@link WheelModule}. They are a singleton array
 * numbered 0-3 from front to back, with even numbers on the left side when facing forward. The
 * azimuth and drive Talons are configured using {@link TalonConfiguration} named "azimuth" and
 * "drive", respectively.
 *
 * <p>The gyro is injected by a Dagger provider in {@link GyroModule}.
 *
 * <p>Derivation of inverse kinematic equations are from Ether's <a
 * href="https://www.chiefdelphi.com/media/papers/2426">Swerve Kinematics and Programming</a>.
 *
 * @see Wheel
 */
@SuppressWarnings("unused")
@Singleton
public class SwerveDrive {

  private static final Logger logger = LoggerFactory.getLogger(SwerveDrive.class);
  private static final String TABLE = "THIRDCOAST.SWERVE";
  private static final int WHEEL_COUNT = 4;
  final AHRS gyro;
  private final double kGyroRateCorrection;
  private final Wheel[] wheels;
  private final double[] ws = new double[WHEEL_COUNT];
  private final double[] wa = new double[WHEEL_COUNT];
  private double[] kLengthComponents;
  private double[] kWidthComponents;

  @Inject
  SwerveDrive(AHRS gyro, Wheel[] wheels, Settings settings) {
    this.gyro = gyro;
    this.wheels = wheels;
    logger.info("field orientation driving is {}", gyro == null ? "DISABLED" : "ENABLED");

    if (settings != null) {

      Toml toml = settings.getTable(TABLE);
      boolean enableGyroLogging = toml.getBoolean("enableGyroLogging", true);

      kLengthComponents = new double[4];
      kWidthComponents = new double[4];

      double length = toml.getDouble("length");
      double width = toml.getDouble("width");
      double offsetX = toml.getDouble("offsetX");
      double offsetY = toml.getDouble("offsetY");

      double[] radii = findRadii(length, width, offsetX, offsetY);

      for (int i = 0; i < radii.length; i++) {

        if (radii[i] != 0) {
          kLengthComponents[i] = length / radii[i];
        } else {
          kLengthComponents[i] = 0.0;
        }

        if (radii[i] != 0) {
          kWidthComponents[i] = width / radii[i];
        } else {
          kWidthComponents[i] = 0.0;
        }
      }

      if (gyro != null && gyro.isConnected()) {
        gyro.enableLogging(enableGyroLogging);
        double robotPeriod = toml.getDouble("robotPeriod");
        double gyroRateCoeff = toml.getDouble("gyroRateCoeff");
        int rate = gyro.getActualUpdateRate();
        double gyroPeriod = 1.0 / rate;
        kGyroRateCorrection = (robotPeriod / gyroPeriod) * gyroRateCoeff;
        logger.debug("gyro frequency = {} Hz", rate);
      } else {
        logger.warn("gyro is missing or not enabled");
        kGyroRateCorrection = 0;
      }

      logger.debug("length = {}", length);
      logger.debug("width = {}", width);
      logger.debug("enableGyroLogging = {}", enableGyroLogging);
      logger.debug("gyroRateCorrection = {}", kGyroRateCorrection);
    } else {
      kGyroRateCorrection = 0;
    }
  }

  public double[] findRadii(double length, double width, double offsetX, double offsetY) {

    double[] radii = new double[4];

    radii[0] = Math.hypot((width - offsetX), (length - offsetY));
    radii[1] = Math.hypot((width + offsetX), (length - offsetY));
    radii[2] = Math.hypot((width - offsetX), (length + offsetY));
    radii[3] = Math.hypot((width + offsetX), (length + offsetY));

    return radii;
  }

  /**
   * Set the drive mode.
   *
   * @param driveMode the drive mode
   */
  public void setDriveMode(DriveMode driveMode) {
    for (Wheel wheel : wheels) {
      wheel.setDriveMode(driveMode);
    }
    logger.info("drive mode = {}", driveMode);
  }

  /**
   * Set all four wheels to specified values.
   *
   * @param azimuth -0.5 to 0.5 rotations, measured clockwise with zero being the robot
   *     straight-ahead position
   * @param drive 0 to 1 in the direction of the wheel azimuth
   */
  public void set(double azimuth, double drive) {
    for (Wheel wheel : wheels) {
      wheel.set(azimuth, drive);
    }
  }

  /**
   * Drive the robot in given field-relative direction and with given rotation.
   *
   * @param forward Y-axis movement, from -1.0 (reverse) to 1.0 (forward)
   * @param strafe X-axis movement, from -1.0 (left) to 1.0 (right)
   * @param azimuth robot rotation, from -1.0 (CCW) to 1.0 (CW)
   */
  public void drive(double forward, double strafe, double azimuth) {

    // Use gyro for field-oriented drive. We use getAngle instead of getYaw to enable arbitrary
    // autonomous starting positions.
    if (gyro != null) {
      double angle = gyro.getAngle();
      angle += gyro.getRate() * kGyroRateCorrection;
      angle = Math.IEEEremainder(angle, 360.0);

      angle = Math.toRadians(angle);
      final double temp = forward * Math.cos(angle) + strafe * Math.sin(angle);
      strafe = -forward * Math.sin(angle) + strafe * Math.cos(angle);
      forward = temp;
    }

    final double a2 = strafe - azimuth * kLengthComponents[2];
    final double a3 = strafe - azimuth * kLengthComponents[3];
    final double b0 = strafe + azimuth * kLengthComponents[0];
    final double b1 = strafe + azimuth * kLengthComponents[1];
    final double d1 = forward - azimuth * kWidthComponents[1];
    final double c3 = forward - azimuth * kWidthComponents[3];
    final double c0 = forward + azimuth * kWidthComponents[0];
    final double d2 = forward + azimuth * kWidthComponents[2];

    // wheel 0
    ws[0] = Math.hypot(b0, c0);
    wa[0] = Math.atan2(b0, c0) * 0.5 / Math.PI;

    // wheel 1
    ws[1] = Math.hypot(b1, d1);
    wa[1] = Math.atan2(b1, d1) * 0.5 / Math.PI;

    // wheel 2
    ws[2] = Math.hypot(a2, d2);
    wa[2] = Math.atan2(a2, d2) * 0.5 / Math.PI;

    // wheel 3
    ws[3] = Math.hypot(a3, c3);
    wa[3] = Math.atan2(a3, c3) * 0.5 / Math.PI;

    // normalize wheel speed
    final double maxWheelSpeed = Math.max(Math.max(ws[0], ws[1]), Math.max(ws[2], ws[3]));
    if (maxWheelSpeed > 1.0) {
      for (int i = 0; i < WHEEL_COUNT; i++) {
        ws[i] /= maxWheelSpeed;
      }
    }

    // set wheels
    for (int i = 0; i < WHEEL_COUNT; i++) {
      wheels[i].set(wa[i], ws[i]);
    }
  }

  /**
   * Stops all wheels' azimuth and drive movement. Calling this in the robots {@code teleopInit} and
   * {@code autonomousInit} will reset wheel azimuth relative encoders to the current position and
   * thereby prevent wheel rotation if the wheels were moved manually while the robot was disabled.
   */
  public void stop() {
    for (Wheel wheel : wheels) {
      wheel.stop();
    }
    logger.info("stopped all wheels");
  }

  /**
   * Save the wheels' azimuth current position as read by absolute encoder. These values are saved
   * persistently on the roboRIO and are normally used to calculate the relative encoder offset
   * during wheel initialization.
   *
   * <p>The wheel alignment data is saved in the WPI preferences data store and may be viewed using
   * a network tables viewer.
   *
   * @see #zeroAzimuthEncoders()
   */
  public void saveAzimuthPositions() {
    Preferences prefs = Preferences.getInstance();
    for (int i = 0; i < WHEEL_COUNT; i++) {
      int position = wheels[i].getAzimuthAbsolutePosition();
      prefs.putInt(getPreferenceKeyForWheel(i), position);
      logger.info("azimuth {}: saved zero = {}", i, position);
    }
  }

  /**
   * Return key that wheel zero information is stored under in WPI preferences.
   *
   * @param wheel the wheel number
   * @return the String key
   */
  public static String getPreferenceKeyForWheel(int wheel) {
    return String.format("%s/wheel.%d", SwerveDrive.class.getSimpleName(), wheel);
  }

  /**
   * Set wheels' azimuth relative offset from zero based on the current absolute position. This uses
   * the physical zero position as read by the absolute encoder and saved during the wheel alignment
   * process.
   *
   * @see #saveAzimuthPositions()
   */
  public void zeroAzimuthEncoders() {
    Preferences prefs = Preferences.getInstance();
    for (int i = 0; i < WHEEL_COUNT; i++) {
      int position = prefs.getInt(getPreferenceKeyForWheel(i), 0);
      wheels[i].setAzimuthZero(position);
      logger.info("azimuth {}: loaded zero = {}", i, position);
    }
  }

  /**
   * Register the swerve wheel azimuth and drive {@link com.ctre.phoenix.motorcontrol.can.TalonSRX}
   * with the Telemetry service for data collection. The Telemetry service will set the Talon status
   * frame update rates to default values during registration.
   *
   * @param telemetryService the active Telemetry service instance created by the robot
   */
  public void registerWith(TelemetryService telemetryService) {
    for (int i = 0; i < WHEEL_COUNT; i++) {
      TalonSRX t = wheels[i].getAzimuthTalon();
      if (t != null)
        telemetryService.register(
            new TalonItem(t, "Azimuth Talon " + i + " (" + t.getDeviceID() + ")"));
      t = wheels[i].getDriveTalon();
      if (t != null)
        telemetryService.register(
            new TalonItem(t, "Drive Talon " + i + " (" + t.getDeviceID() + ")"));
    }
  }

  /**
   * Returns the four wheels of the swerve drive.
   *
   * @return the Wheel array.
   */
  public Wheel[] getWheels() {
    return wheels;
  }

  /**
   * Returns the wheel speed array of the swerve drive.
   *
   * @return the Wheel speed array
   */
  public double[] getWs() {
    return ws;
  }

  /**
   * Returns the wheel azimuth array.
   *
   * @return the Wheel azimuth array
   */
  public double[] getWa() {
    return wa;
  }

  /**
   * Get the gyro instance being used by the drive.
   *
   * @return the gyro instance
   */
  public AHRS getGyro() {
    return gyro;
  }

  /**
   * Unit testing
   *
   * @return length
   */
  double[] getLengthComponents() {
    return kLengthComponents;
  }

  /**
   * Unit testing
   *
   * @return width
   */
  double[] getWidthComponents() {
    return kWidthComponents;
  }

  /** Swerve Drive drive mode */
  public enum DriveMode {
    OPEN_LOOP,
    CLOSED_LOOP,
    TELEOP,
    TRAJECTORY,
    AZIMUTH
  }
}
