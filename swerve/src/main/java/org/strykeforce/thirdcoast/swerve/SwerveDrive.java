package org.strykeforce.thirdcoast.swerve;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Errors;

/**
 * Control a Third Coast swerve drive.
 *
 * <p>Wheels are a array numbered 0-3 from front to back, with even numbers on the left side when
 * facing forward.
 *
 * <p>Derivation of inverse kinematic equations are from Ether's <a
 * href="https://www.chiefdelphi.com/media/papers/2426">Swerve Kinematics and Programming</a>.
 *
 * @see Wheel
 */
@SuppressWarnings("unused")
public class SwerveDrive {

  private static final Logger logger = LoggerFactory.getLogger(SwerveDrive.class);
  private static final int WHEEL_COUNT = 4;
  final AHRS gyro;
  private final double kLengthComponent;
  private final double kWidthComponent;
  private final double kGyroRateCorrection;
  private final Wheel[] wheels;
  private final double[] ws = new double[WHEEL_COUNT];
  private final double[] wa = new double[WHEEL_COUNT];

  public SwerveDrive(SwerveDriveConfig config) {
    gyro = config.gyro;
    wheels = config.wheels;
    logger.info("field orientation driving is {}", gyro == null ? "DISABLED" : "ENABLED");

    final boolean summarizeErrors = config.summarizeTalonErrors;
    Errors.setSummarized(summarizeErrors);
    Errors.setCount(0);
    logger.debug("TalonSRX configuration errors summarized = {}", summarizeErrors);

    double length = config.length;
    double width = config.width;
    double radius = Math.hypot(length, width);
    kLengthComponent = length / radius;
    kWidthComponent = width / radius;

    if (gyro != null && gyro.isConnected()) {
      gyro.enableLogging(config.gyroLoggingEnabled);
      double robotPeriod = config.robotPeriod;
      double gyroRateCoeff = config.gyroRateCoeff;
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
    logger.debug("enableGyroLogging = {}", config.gyroLoggingEnabled);
    logger.debug("gyroRateCorrection = {}", kGyroRateCorrection);
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

    final double a = strafe - azimuth * kLengthComponent;
    final double b = strafe + azimuth * kLengthComponent;
    final double c = forward - azimuth * kWidthComponent;
    final double d = forward + azimuth * kWidthComponent;

    // wheel speed
    ws[0] = Math.hypot(b, d);
    ws[1] = Math.hypot(b, c);
    ws[2] = Math.hypot(a, d);
    ws[3] = Math.hypot(a, c);

    // wheel azimuth
    wa[0] = Math.atan2(b, d) * 0.5 / Math.PI;
    wa[1] = Math.atan2(b, c) * 0.5 / Math.PI;
    wa[2] = Math.atan2(a, d) * 0.5 / Math.PI;
    wa[3] = Math.atan2(a, c) * 0.5 / Math.PI;

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
   * TEMPORARY, needs API and more
   *
   * <p>Change center of rotation on the robot
   */
  public void driveCenterOfRotation() {
    double[] radii = new double[4];

    double[] wa = new double[4];
    double[] ws = new double[4];

    double offsetX = -10.75;
    double offsetY = 0.0;

    logger.debug("offsetX = {} offsetY = {}", offsetX, offsetY);

    double length = 21.5 / 2.0;
    double width = 21.5 / 2.0;

    double velocity = 0.2;

    double[] centerOfRot = {offsetX, offsetY};

    double[] w0 = {width, -length};
    double[] w1 = {width, length};
    double[] w2 = {-width, -length};
    double[] w3 = {-width, length};

    logger.debug("l = {} w = {} ", length, width);

    radii[0] = Math.hypot(centerOfRot[0] - w0[0], centerOfRot[1] - w0[1]);
    radii[1] = Math.hypot(centerOfRot[0] - w1[0], centerOfRot[1] - w1[1]);
    radii[2] = Math.hypot(centerOfRot[0] - w2[0], centerOfRot[1] - w2[1]);
    radii[3] = Math.hypot(centerOfRot[0] - w3[0], centerOfRot[1] - w3[1]);

    logger.debug("radii = {}", radii);

    double x;
    double y;

    // wheel 0
    ws[0] = radii[0] * velocity / 15.0;
    x = centerOfRot[0] - w0[0];
    y = centerOfRot[1] - w0[1];
    wa[0] = 0.25 + Math.atan2(y, x) * 0.5 / Math.PI;

    // wheel 1
    ws[1] = radii[1] * velocity / 15.0;
    x = centerOfRot[0] - w1[0];
    y = centerOfRot[1] - w1[1];
    wa[1] = 0.25 + Math.atan2(y, x) * 0.5 / Math.PI;

    // wheel 2
    ws[2] = radii[2] * velocity / 15.0;
    x = centerOfRot[0] - w2[0];
    y = centerOfRot[1] - w2[1];
    wa[2] = 0.25 + Math.atan2(y, x) * 0.5 / Math.PI;

    // wheel 3
    ws[3] = radii[3] * velocity / 15.0;
    x = centerOfRot[0] - w3[0];
    y = centerOfRot[1] - w3[1];
    wa[3] = 0.25 + Math.atan2(y, x) * 0.5 / Math.PI;

    // normalize wheel speed
    double maxWheelSpeed = Math.max(Math.max(ws[0], ws[1]), Math.max(ws[2], ws[3]));
    if (maxWheelSpeed > 1.0) {
      for (int i = 0; i < 4; i++) {
        ws[i] /= maxWheelSpeed;
      }
    }

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
    Errors.setCount(0);
    Preferences prefs = Preferences.getInstance();
    for (int i = 0; i < WHEEL_COUNT; i++) {
      int position = prefs.getInt(getPreferenceKeyForWheel(i), 0);
      wheels[i].setAzimuthZero(position);
      logger.info("azimuth {}: loaded zero = {}", i, position);
    }
    int errorCount = Errors.getCount();
    if (errorCount > 0) logger.error("TalonSRX set azimuth zero error count = {}", errorCount);
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
  double getLengthComponent() {
    return kLengthComponent;
  }

  /**
   * Unit testing
   *
   * @return width
   */
  double getWidthComponent() {
    return kWidthComponent;
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
