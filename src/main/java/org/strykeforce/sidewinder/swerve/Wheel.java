package org.strykeforce.sidewinder.swerve;

import com.ctre.CANTalon;
import org.strykeforce.sidewinder.talon.TalonParameters;

/**
 * Controls a swerve drive wheel azimuth and drive motors.
 *
 * The swerve-drive inverse kinematics algorithm will always calculate individual wheel angles as
 * -180 to 180 degrees, measured clockwise with zero being the straight-ahead position. Wheel speed
 * is calculated as 0 to 1 in the direction of the wheel angle.
 *
 * <p>This class will decide how to implement this angle and speed optimally for the azimuth and
 * drive motors. In some cases it makes sense to reverse wheel speed to avoid driving the wheel
 * azimuth 180 degrees.
 *
 * <p>Hardware assumed by this class includes a CTRE magnetic encoder on the azimuth motor and no
 * limits on wheel azimuth.
 */
public class Wheel {

  private final CANTalon azimuth;
  private final CANTalon drive;

  private double azimuthLastPosition;
  private double azimuthPosition;
  private double driveSpeed;
  private boolean isDriveReversed = false;

  /**
   * This designated constructor constructs a wheel by supplying azimuth and drive talons. They are
   * initialized with Talon configurations named "azimuth" and "drive" respectively. Assumes the
   * Talon configurations have been registered.
   *
   * @param azimuth the azimuth CANTalon
   * @param drive the drive CANTalon
   * @see org.strykeforce.sidewinder.talon.TalonParameters#register(String)
   */
  public Wheel(CANTalon azimuth, CANTalon drive) {
    String AZIMUTH_PARAMETERS = "azimuth";
    String DRIVE_PARAMETERS = "drive";

    this.azimuth = azimuth;
    this.drive = drive;
    setAzimuthParameters(AZIMUTH_PARAMETERS);
    setDriveParameters(DRIVE_PARAMETERS);
  }

  /**
   * Convenience constructor for a wheel by specifying the swerve drive wheel number (0-3).
   *
   * @param index the wheel number
   */
  public Wheel(int index) {
    this(new CANTalon(index), new CANTalon(index + 10));
  }

  double getAzimuthPositionError(double setpoint) {
    return Math.IEEEremainder(setpoint - azimuthPosition, 2.0);
  }

  /**
   * This method calculates the optimal drive settings and applies them.
   *
   * @param azimuth wheel angle as calculated by the inverse-kinematics algorithm
   * @param drive wheel speed as calculated by the inverse-kinematics algorithm
   */
  public void set(double azimuth, double drive) {
    double azimuthError = getAzimuthPositionError(azimuth);


    azimuthPosition = azimuthLastPosition + getAzimuthPositionError(azimuth);
    this.azimuth.set(azimuthPosition);
    this.drive.set(drive);
    driveSpeed = drive;
  }

  public void stop() {
    azimuthPosition = azimuth.getPosition();
    azimuth.set(azimuthPosition);
    drive.set(0);
  }

  public void setAzimuthParameters(String parameters) {
    TalonParameters talonParameters = TalonParameters.getInstance(parameters);
    talonParameters.configure(azimuth);
  }

  public void setDriveParameters(String parameters) {
    TalonParameters talonParameters = TalonParameters.getInstance(parameters);
    talonParameters.configure(drive);
  }

  /**
   * Set the azimuth encoder relative to wheel zeroSensors position.
   *
   * @param zero encoder position (in ticks) where wheel is zeroed.
   */
  public void setAzimuthZero(int zero) {
    double offset = getAzimuthAbsolutePosition() - zero; // encoder ticks
    azimuthPosition = offset / 0xFFF; // wheel rotations
    azimuth.setPosition(azimuthPosition);
    azimuth.set(azimuthPosition);
  }

  public double getAzimuthPosition() {
    return azimuthPosition;
  }

  public double getDriveSpeed() {
    return driveSpeed;
  }

  public int getAzimuthAbsolutePosition() {
    return azimuth.getPulseWidthPosition() & 0xFFF;
  }

  public CANTalon getAzimuth() {
    return azimuth;
  }

  public CANTalon getDrive() {
    return drive;
  }
}
