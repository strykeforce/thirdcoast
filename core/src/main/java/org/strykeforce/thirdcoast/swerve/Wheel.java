package org.strykeforce.thirdcoast.swerve;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Errors;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.talon.TalonFactory;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;

/**
 * Controls a swerve drive wheel azimuth and drive motors. The azimuth and drive Talons are
 * configured using {@link TalonConfiguration} named "azimuth" and "drive", respectively.
 *
 * <p>The swerve-drive inverse kinematics algorithm will always calculate individual wheel angles as
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

  public static final double TICKS_PER_ROTATION = 4096;
  private static final Logger logger = LoggerFactory.getLogger(Wheel.class);
  private final TalonProvisioner talonProvisioner;
  private final ThirdCoastTalon azimuthTalon;
  private final ThirdCoastTalon driveTalon;

  private double driveSetpointMax;
  private double azimuthSetpoint;
  private double driveSetpoint;

  /**
   * This designated constructor constructs a wheel by supplying azimuthTalon and driveTalon talons.
   * They are initialized with Talon configurations named "azimuthTalon" and "driveTalon"
   * respectively. Assumes the Talon configurations have been registered.
   *
   * @param talonProvisioner the TalonProvisioner used to provision Talons
   * @param azimuth the azimuthTalon TalonSRX
   * @param drive the driveTalon TalonSRX
   */
  public Wheel(TalonProvisioner talonProvisioner, ThirdCoastTalon azimuth, ThirdCoastTalon drive) {
    final String AZIMUTH_PARAMETERS = "azimuth";
    final String DRIVE_PARAMETERS = "drive";

    this.talonProvisioner = talonProvisioner;
    azimuthTalon = azimuth;
    driveTalon = drive;
    setAzimuthParameters(AZIMUTH_PARAMETERS);
    setDriveParameters(DRIVE_PARAMETERS);
    logger.info("initialized azimuth = {}, drive = {}", azimuthTalon, driveTalon);
  }

  /**
   * Convenience constructor for a wheel by specifying the swerve driveTalon wheel number (0-3).
   *
   * @param talonFactory the TalonFactory used to create Talons
   * @param index the wheel number
   */
  public Wheel(TalonFactory talonFactory, int index) {
    this(
        talonFactory.getProvisioner(),
        (ThirdCoastTalon) talonFactory.getTalon(index),
        (ThirdCoastTalon) talonFactory.getTalon(index + 10));
  }

  /**
   * This method calculates the optimal driveTalon settings and applies them.
   *
   * <p>The drive setpoint is scaled by the drive Talon {@code setpoint_max} parameter configured in
   * {@link TalonConfiguration}. For instance, with an open-loop {@code setpoint_max = 12.0} volts,
   * a drive setpoint of 1.0 would result in the drive Talon being set to 12.0.
   *
   * @param azimuth -0.5 to 0.5 rotations, measured clockwise with zero being the wheel's zeroed
   *     position
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

    double azimuthPosition = azimuthTalon.getSelectedSensorPosition(0);
    double azimuthError =
        Math.IEEEremainder(azimuth * TICKS_PER_ROTATION - azimuthPosition, TICKS_PER_ROTATION);
    if (Math.abs(azimuthError) > 0.25 * TICKS_PER_ROTATION) {
      azimuthError -= Math.copySign(0.5 * TICKS_PER_ROTATION, azimuthError);
      driveSetpoint = -driveSetpoint;
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
    azimuthSetpoint = azimuthTalon.getSelectedSensorPosition(0);
    azimuthTalon.set(azimuthSetpoint);
    driveTalon.set(0);
  }

  void setAzimuthParameters(String name) {
    try {
      TalonConfiguration talonConfiguration = talonProvisioner.configurationFor(name);
      talonConfiguration.configure(azimuthTalon);
    } catch (IllegalArgumentException e) {
      logger.error("azimuth parameters missing", e);
      throw e;
    }
  }

  private void setDriveParameters(String name) {
    try {
      TalonConfiguration talonConfiguration = talonProvisioner.configurationFor(name);
      talonConfiguration.configure(driveTalon);
      driveSetpointMax = talonConfiguration.getSetpointMax();
    } catch (IllegalArgumentException e) {
      logger.error("drive parameters missing", e);
      throw e;
    }
  }

  /**
   * Set the azimuthTalon encoder relative to wheel zero alignment position.
   *
   * @param zero encoder position (in ticks) where wheel is zeroed.
   */
  public void setAzimuthZero(int zero) {
    azimuthSetpoint = (double) (getAzimuthAbsolutePosition() - zero);
    // TODO: test this timeout value is OK
    ErrorCode e = azimuthTalon.setSelectedSensorPosition((int) azimuthSetpoint, 0, 10);
    Errors.check(e, logger);
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

  //  /**
  //   * Return the drive speed setpoint. Note this may differ from the actual speed if the wheel is
  //   * accelerating.
  //   *
  //   * @return speed setpoint
  //   */
  //  public double getDriveSetpoint() {
  //    return driveSetpoint;
  //  }

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
    return azimuthTalon.getSensorCollection().getPulseWidthPosition() & 0xFFF;
  }

  /**
   * Get the azimuth Talon controller.
   *
   * @return azimuth Talon instance used by wheel
   */
  public TalonSRX getAzimuthTalon() {
    return azimuthTalon;
  }

  /**
   * Get the drive Talon controller.
   *
   * @return drive Talon instance used by wheel
   */
  public TalonSRX getDriveTalon() {
    return driveTalon;
  }

  @Override
  public String toString() {
    return "Wheel{"
        + "azimuthTalon="
        + azimuthTalon
        + ", driveTalon="
        + driveTalon
        + ", driveSetpointMax="
        + driveSetpointMax
        + '}';
  }
}
