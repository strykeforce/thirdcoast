package org.strykeforce.thirdcoast.swerve;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Preferences;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

/**
 * Control a Third Coast swerve drive.
 *
 * <p>Wheels are injected by a Dagger provider in {@link WheelModule}. They are a singleton array
 * numbered 0-3 from front to back, with even numbers on the left side when facing forward. The
 * azimuth and drive Talons are configured using {@link TalonConfiguration} named "azimuth" and
 * "drive", respectively.
 *
 * The gyro is injected by a Dagger provider in {@link GyroModule}.
 *
 * <p>Derivation of inverse kinematic equations are from Ether's <a href="https://www.chiefdelphi.com/media/papers/2426">Swerve
 * Kinematics and Programming</a>.
 *
 * @see Wheel
 */
@Singleton
public class SwerveDrive {

  private final static int WHEEL_COUNT = 4;

  private final Wheel[] wheels;
  private final AHRS gyro;

  @Inject
  SwerveDrive(AHRS gyro, Wheel[] wheels) {
    if (gyro != null) {
      gyro.enableLogging(true);
    }
    this.gyro = gyro;
    this.wheels = wheels;
  }

  static String getPreferenceKeyForWheel(int i) {
    return String.format("%s/wheel.%d", SwerveDrive.class.getSimpleName(), i);
  }

  /**
   * Set all four wheels to specified values.
   *
   * @param azimuth -0.5 to 0.5 rotations, measured clockwise with zero being the robot
   * straight-ahead position
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

    final double LENGTH = 1.0;
    final double WIDTH = 1.0;
    final double RADIUS = Math.hypot(LENGTH, WIDTH);

    final double a = strafe - azimuth * (LENGTH / RADIUS);
    final double b = strafe + azimuth * (LENGTH / RADIUS);
    final double c = forward - azimuth * (WIDTH / RADIUS);
    final double d = forward + azimuth * (WIDTH / RADIUS);

    // wheel speed
    double[] ws = new double[4];
    ws[0] = Math.hypot(b, d);
    ws[1] = Math.hypot(b, c);
    ws[2] = Math.hypot(a, d);
    ws[3] = Math.hypot(a, c);

    // wheel azimuth
    double[] wa = new double[4];
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
      prefs.putInt(getPreferenceKeyForWheel(i), wheels[i].getAzimuthAbsolutePosition());
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
      wheels[i].setAzimuthZero(prefs.getInt(getPreferenceKeyForWheel(i), 0));
    }
  }

  /**
   * Register the swerve wheel azimuth and drive {@link com.ctre.CANTalon} with the Telemetry
   * service for data collection. The Telemetry service will set the Talon status frame update rates
   * to default values during registration.
   *
   * @param telemetryService the active Telemetry service instance created by the robot
   */
  public void registerWith(TelemetryService telemetryService) {
    for (Wheel wheel : wheels) {
      telemetryService.register(wheel.getAzimuthTalon());
      telemetryService.register(wheel.getDriveTalon());
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

}
