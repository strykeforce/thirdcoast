package org.strykeforce.thirdcoast.swerve;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Preferences;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;
import org.strykeforce.thirdcoast.util.Settings;

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
  private final double kLengthComponent;
  private final double kWidthComponent;
  private final Wheel[] wheels;
  private final double[] ws = new double[WHEEL_COUNT];
  private final double[] wa = new double[WHEEL_COUNT];

  @Inject
  SwerveDrive(AHRS gyro, Wheel[] wheels, Settings settings) {
    this.gyro = gyro;
    this.wheels = wheels;
    logger.info("field orientation driving is {}", gyro == null ? "DISABLED" : "ENABLED");

    Toml toml = settings.getTable(TABLE);
    boolean enableGyroLogging = toml.getBoolean("enableGyroLogging", true);
    if (gyro != null) gyro.enableLogging(enableGyroLogging);

    double length = toml.getDouble("length");
    double width = toml.getDouble("width");
    double radius = Math.hypot(length, width);
    kLengthComponent = length / radius;
    kWidthComponent = width / radius;

    logger.debug("length = {}", length);
    logger.debug("width = {}", width);
    logger.debug("enableGyroLogging = {}", enableGyroLogging);
  }

  /**
   * Return key that wheel zero information is stored under in WPI preferences.
   * @param wheel the wheel number
   * @return the String key
   */
  public static String getPreferenceKeyForWheel(int wheel) {
    return String.format("%s/wheel.%d", SwerveDrive.class.getSimpleName(), wheel);
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

    // field-oriented
    if (gyro != null) {
      final double angle = gyro.getYaw() * Math.PI / 180.0;
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
    CLOSED_LOOP
  }
}
