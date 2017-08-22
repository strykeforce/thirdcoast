package org.strykeforce.thirdcoast.swerve;

import com.ctre.CANTalon;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import org.strykeforce.thirdcoast.talon.TalonParameters;

/**
 * Controls a swerve drive wheel azimuth and drive motors.
 *
 * The swerve-drive inverse kinematics algorithm will always calculate individual wheel angles as
 * -0.5 to 0.5 rotations, measured clockwise with zero being the straight-ahead position. Wheel
 * speed is calculated as 0 to 1 in the direction of the wheel angle.
 *
 * <p>This class will calculate how to implement this angle and drive direction optimally for the
 * azimuth and drive motors. In some cases it makes sense to reverse wheel direction to avoid
 * rotating the wheel azimuth 180 degrees.
 *
 * <p>Hardware assumed by this class includes a CTRE magnetic encoder on the azimuth motor and no
 * limits on wheel azimuth rotation. Azimuth Talons have an ID in the range 0-3 with corresponding
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
   * @see org.strykeforce.thirdcoast.talon.TalonParameters#register(UnmodifiableConfig)
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
   * <p>The drive setpoint is scaled by the drive Talon {@code setpoint_max} parameter configured in
   * {@link TalonParameters}. For instance, with an open-loop {@code setpoint_max = 12.0} volts, a
   * drive setpoint of 1.0 would result in the drive Talon being set to 12.0.
   *
   * @param azimuth -0.5 to 0.5 rotations, measured clockwise with zero being the wheel's zeroed
   * position
   * @param drive 0 to 1.0 in the direction of the wheel azimuth
   */
  public void set(double azimuth, double drive) {
    driveSetpoint = drive * driveSetpointMax;
    azimuth = -azimuth; // azimuth hardware configuration dependent

    // don't reset wheel azimuth direction to zero when returning to neutral
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

  /**
   * Stop azimuth and drive movement. This resets the azimuth setpoint and relative encoder to the
   * current position in case the wheel has been manually rotated away from its previous setpoint.
   */
  public void stop() {
    azimuthSetpoint = azimuthTalon.getPosition();
    azimuthTalon.set(azimuthSetpoint);
    driveTalon.set(0);
  }


  void setAzimuthParameters(String parameters) {
    TalonParameters talonParameters = TalonParameters.getInstance(parameters);
    talonParameters.configure(azimuthTalon);
  }

  void setDriveParameters(String parameters) {
    TalonParameters talonParameters = TalonParameters.getInstance(parameters);
    talonParameters.configure(driveTalon);
    driveSetpointMax = talonParameters.getSetpointMax();
  }

  /**
   * Set the azimuthTalon encoder relative to wheel zero alignment position.
   *
   * @param zero encoder position (in ticks) where wheel is zeroed.
   */
  public void setAzimuthZero(int zero) {
    double offset = getAzimuthAbsolutePosition() - zero; // encoder ticks
    azimuthSetpoint = offset / 0xFFF; // wheel rotations
    azimuthTalon.setPosition(azimuthSetpoint);
    azimuthTalon.set(azimuthSetpoint);
  }

  /**
   * Return the azimuth position setpoint. Note this may differ from the actual position if the
   * wheel is still rotating into position.
   *
   * @return azimuth setpoint
   */
  public double getAzimuthSetpoint() {
    return azimuthSetpoint;
  }

  /**
   * Return the drive speed setpoint. Note this may differ from the actual speed if the wheel is
   * accelerating.
   *
   * @return speed setpoint
   */
  public double getDriveSetpoint() {
    return driveSetpoint;
  }

  /**
   * Indicates if the wheel has reversed drive direction to optimize azimuth rotation.
   *
   * @return true if reversed
   */
  public boolean isDriveReversed() {
    return driveSetpoint < 0;
  }

  /**
   * Returns the wheel's azimuth absolute position in encoder ticks.
   *
   * @return 0 - 4095 encoder ticks
   */
  public int getAzimuthAbsolutePosition() {
    return azimuthTalon.getPulseWidthPosition() & 0xFFF;
  }

  /**
   * Get the azimuth Talon controller.
   *
   * @return azimuth Talon instance used by wheel
   */
  public CANTalon getAzimuthTalon() {
    return azimuthTalon;
  }

  /**
   * Get the drive Talon controller.
   *
   * @return drive Talon instance used by wheel
   */
  public CANTalon getDriveTalon() {
    return driveTalon;
  }
}
