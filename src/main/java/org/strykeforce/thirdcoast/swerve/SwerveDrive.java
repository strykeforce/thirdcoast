package org.strykeforce.thirdcoast.swerve;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI.Port;
import org.strykeforce.thirdcoast.robot.Subsystem;

/**
 * Control a Third Coast swerve drive.
 *
 * Wheels are numbered 0-3 from front to back, with even numbers on the left side when facing
 * forward.
 *
 * <p>Derivation of inverse kinematic equations are from Ether's <a href="https://www.chiefdelphi.com/media/papers/2426">Swerve
 * Kinematics and Programming</a>.
 *
 * @see Wheel
 */
public class SwerveDrive implements Subsystem {

  private final Wheel[] wheels;
  private final AHRS gyro;

  SwerveDrive(AHRS gyro, Wheel[] wheels) {
    this.gyro = gyro;
    this.wheels = wheels;
  }

  SwerveDrive(AHRS gyro) {
    this(gyro, new Wheel[]{
        new Wheel(0), // front left
        new Wheel(1), // front right
        new Wheel(2), // rear left
        new Wheel(3)  // rear right
    });
  }

  /**
   * Get the drive subsystem singleton. This is lazy initialized to avoid UnsatisfiedLinkError
   * against the CTRE JNI libs during unit testing.
   *
   * @return the drive singleton
   */
  public static SwerveDrive getInstance() {

    return LazyHolder.INSTANCE;
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

  public void drive(double forward, double strafe, double azimuth) {

    if (gyro != null) {
      double angle = gyro.getYaw() * Math.PI / 180.0;
      double temp = forward * Math.cos(angle) + strafe * Math.sin(angle);
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
      for (int i = 0; i != ws.length; i++) {
        ws[i] /= maxWheelSpeed;
      }
    }

    // set wheels
    for (int i = 0; i != wheels.length; i++) {
      wheels[i].set(wa[i], ws[i]);
    }
  }

  @Override
  public void stop() {
    for (Wheel wheel : wheels) {
      wheel.stop();
    }
  }

  @Override
  public void zeroSensors() {
    int[] zeros = new int[]{3593, 3359, 2966, 1236};
    for (int i = 0; i != wheels.length; i++) {
      wheels[i].setAzimuthZero(zeros[i]);
    }
  }

  private static class LazyHolder {

    static final SwerveDrive INSTANCE = new SwerveDrive(new AHRS(Port.kMXP));
  }

}
