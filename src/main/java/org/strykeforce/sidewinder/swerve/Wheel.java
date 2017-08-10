package org.strykeforce.sidewinder.swerve;

import com.ctre.CANTalon;
import org.strykeforce.sidewinder.talon.TalonParameters;

/**
 * Controls a swerve driveTalon wheel azimuthTalon and driveTalon motors.
 *
 * The swerve-driveTalon inverse kinematics algorithm will always calculate individual wheel angles
 * as -0.5 to 0.5 rotations, measured clockwise with zero being the straight-ahead position. Wheel
 * speed is calculated as 0 to 1 in the direction of the wheel angle.
 *
 * <p>This class will decide how to implement this angle and speed optimally for the azimuthTalon
 * and driveTalon motors. In some cases it makes sense to reverse wheel speed to avoid driving the
 * wheel azimuthTalon 180 degrees.
 *
 * <p>Hardware assumed by this class includes a CTRE magnetic encoder on the azimuthTalon motor and
 * no limits on wheel azimuthTalon. Azimuth Talons have an ID in the range 0-3 with corresponding
 * drive Talon IDs in the range 10-13.
 */
public class Wheel {

  private final CANTalon azimuthTalon;
  private final CANTalon driveTalon;

  private double driveSetpointMax;
  private double azimuthSetpoint;
  private double driveSetpoint;

  /**
   * This designated constructor constructs a wheel by supplying azimuthTalon and driveTalon talons.
   * They are initialized with Talon configurations named "azimuthTalon" and "driveTalon"
   * respectively. Assumes the Talon configurations have been registered.
   *
   * @param azimuth the azimuthTalon CANTalon
   * @param drive the driveTalon CANTalon
   * @see org.strykeforce.sidewinder.talon.TalonParameters#register(String)
   */
  public Wheel(CANTalon azimuth, CANTalon drive) {
    final String AZIMUTH_PARAMETERS = "azimuth";
    final String DRIVE_PARAMETERS = "drive";

    azimuthTalon = azimuth;
    driveTalon = drive;
    setAzimuthParameters(AZIMUTH_PARAMETERS);
    setDriveParameters(DRIVE_PARAMETERS);
  }

  /**
   * Convenience constructor for a wheel by specifying the swerve driveTalon wheel number (0-3).
   *
   * @param index the wheel number
   */
  public Wheel(int index) {
    this(new CANTalon(index), new CANTalon(index + 10));
  }

  /**
   * This method calculates the optimal driveTalon settings and applies them.
   *
   * @param azimuth -0.5 to 0.5 rotations, measured clockwise with zero being the wheel's zeroed
   * position
   * @param drive 0 to 1 in the direction of the wheel azimuth
   */
  public void set(double azimuth, double drive) {
    driveSetpoint = drive * driveSetpointMax;
    azimuth = -azimuth; // azimuth configuration requires reversing

    // don't reset wheel azimuth in neutral
    if (driveSetpoint == 0) {
      driveTalon.set(0);
      return;
    }

    double azimuthPosition = azimuthTalon.getPosition();
    double azimuthError = Math.IEEEremainder(azimuth - azimuthPosition, 1.0);
    if (Math.abs(azimuthError) > 0.25) {
      azimuthError -= Math.copySign(0.5, azimuthError);
      driveSetpoint *= -1.0;
    }
    azimuthSetpoint = azimuthPosition + azimuthError;

    azimuthTalon.set(azimuthSetpoint);
    driveTalon.set(driveSetpoint);
  }

  public void stop() {
    azimuthSetpoint = azimuthTalon.getPosition();
    azimuthTalon.set(azimuthSetpoint);
    driveTalon.set(0);
  }

  public void setAzimuthParameters(String parameters) {
    TalonParameters talonParameters = TalonParameters.getInstance(parameters);
    talonParameters.configure(azimuthTalon);
  }

  public void setDriveParameters(String parameters) {
    TalonParameters talonParameters = TalonParameters.getInstance(parameters);
    talonParameters.configure(driveTalon);
    driveSetpointMax = talonParameters.getSetpointMax();
  }

  /**
   * Set the azimuthTalon encoder relative to wheel zeroSensors position.
   *
   * @param zero encoder position (in ticks) where wheel is zeroed.
   */
  public void setAzimuthZero(int zero) {
    double offset = getAzimuthAbsolutePosition() - zero; // encoder ticks
    azimuthSetpoint = offset / 0xFFF; // wheel rotations
    azimuthTalon.setPosition(azimuthSetpoint);
    azimuthTalon.set(azimuthSetpoint);
  }

  public double getAzimuthSetpoint() {
    return azimuthSetpoint;
  }

  public double getDriveSetpoint() {
    return driveSetpoint;
  }

  public boolean isDriveReversed() {
    return driveSetpoint < 0;
  }

  public int getAzimuthAbsolutePosition() {
    return azimuthTalon.getPulseWidthPosition() & 0xFFF;
  }

  public CANTalon getAzimuthTalon() {
    return azimuthTalon;
  }

  public CANTalon getDriveTalon() {
    return driveTalon;
  }
}
