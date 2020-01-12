package org.strykeforce.thirdcoast.swerve;

import static com.ctre.phoenix.motorcontrol.ControlMode.*;
import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.TELEOP;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import java.util.Objects;
import java.util.function.DoubleConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode;
import org.strykeforce.thirdcoast.talon.Errors;

/**
 * Controls a swerve drive wheel azimuth and drive motors.
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
  private static final int TICKS = 4096;

  private static final Logger logger = LoggerFactory.getLogger(Wheel.class);
  private final double driveSetpointMax;
  private final TalonSRX driveTalon;
  private final TalonSRX azimuthTalon;
  protected DoubleConsumer driver;
  private boolean isInverted = false;

  /**
   * This constructs a wheel with supplied azimuth and drive talons.
   *
   * <p>Wheels will scale closed-loop drive output to {@code driveSetpointMax}. For example, if
   * closed-loop drive mode is tuned to have a max usable output of 10,000 ticks per 100ms, set this
   * to 10,000 and the wheel will send a setpoint of 10,000 to the drive talon when wheel is set to
   * max drive output (1.0).
   *
   * @param azimuth the configured azimuth TalonSRX
   * @param drive the configured drive TalonSRX
   * @param driveSetpointMax scales closed-loop drive output to this value when drive setpoint = 1.0
   */
  public Wheel(TalonSRX azimuth, TalonSRX drive, double driveSetpointMax) {
    this.driveSetpointMax = driveSetpointMax;
    azimuthTalon = Objects.requireNonNull(azimuth);
    driveTalon = Objects.requireNonNull(drive);

    setDriveMode(TELEOP);

    logger.debug("azimuth = {} drive = {}", azimuthTalon.getDeviceID(), driveTalon.getDeviceID());
    logger.debug("driveSetpointMax = {}", driveSetpointMax);
    if (driveSetpointMax == 0.0) logger.warn("driveSetpointMax may not have been configured");
  }

  /**
   * This method calculates the optimal driveTalon settings and applies them.
   *
   * @param azimuth -0.5 to 0.5 rotations, measured clockwise with zero being the wheel's zeroed
   *     position
   * @param drive 0 to 1.0 in the direction of the wheel azimuth
   */
  public void set(double azimuth, double drive) {
    // don't reset wheel azimuth direction to zero when returning to neutral
    if (drive == 0) {
      driver.accept(0d);
      return;
    }

    azimuth *= -TICKS; // flip azimuth, hardware configuration dependent

    double azimuthPosition = azimuthTalon.getSelectedSensorPosition(0);
    double azimuthError = Math.IEEEremainder(azimuth - azimuthPosition, TICKS);

    // minimize azimuth rotation, reversing drive if necessary
    isInverted = Math.abs(azimuthError) > 0.25 * TICKS;
    if (isInverted) {
      azimuthError -= Math.copySign(0.5 * TICKS, azimuthError);
      drive = -drive;
    }

    azimuthTalon.set(MotionMagic, azimuthPosition + azimuthError);
    driver.accept(drive);
  }

  /**
   * Set azimuth to encoder position.
   *
   * @param position position in encoder ticks.
   */
  public void setAzimuthPosition(int position) {
    azimuthTalon.set(MotionMagic, position);
  }

  public void disableAzimuth() {
    azimuthTalon.neutralOutput();
  }

  /**
   * Set the operating mode of the wheel's drive motors. In this default wheel implementation {@code
   * OPEN_LOOP} and {@code TELEOP} are equivalent and {@code CLOSED_LOOP}, {@code TRAJECTORY} and
   * {@code AZIMUTH} are equivalent.
   *
   * <p>In closed-loop modes, the drive setpoint is scaled by the drive Talon {@code
   * driveSetpointMax} parameter.
   *
   * <p>This method is intended to be overridden if the open or closed-loop drive wheel drivers need
   * to be customized.
   *
   * @param driveMode the desired drive mode
   */
  public void setDriveMode(DriveMode driveMode) {
    switch (driveMode) {
      case OPEN_LOOP:
      case TELEOP:
        driver = (setpoint) -> driveTalon.set(PercentOutput, setpoint);
        break;
      case CLOSED_LOOP:
      case TRAJECTORY:
      case AZIMUTH:
        driver = (setpoint) -> driveTalon.set(Velocity, setpoint * driveSetpointMax);
        break;
    }
  }

  /**
   * Stop azimuth and drive movement. This resets the azimuth setpoint and relative encoder to the
   * current position in case the wheel has been manually rotated away from its previous setpoint.
   */
  public void stop() {
    azimuthTalon.set(MotionMagic, azimuthTalon.getSelectedSensorPosition(0));
    driver.accept(0d);
  }

  /**
   * Set the azimuthTalon encoder relative to wheel zero alignment position. For example, if current
   * absolute encoder = 0 and zero setpoint = 2767, then current relative setpoint = -2767.
   *
   * <pre>
   *
   * relative:  -2767                               0
   *           ---|---------------------------------|-------
   * absolute:    0                               2767
   *
   * </pre>
   *
   * @param zero zero setpoint, absolute encoder position (in ticks) where wheel is zeroed.
   */
  public void setAzimuthZero(int zero) {
    int azimuthSetpoint = getAzimuthAbsolutePosition() - zero;
    ErrorCode err = azimuthTalon.setSelectedSensorPosition(azimuthSetpoint, 0, 10);
    Errors.check(err, logger);
    azimuthTalon.set(MotionMagic, azimuthSetpoint);
  }

  /**
   * Returns the wheel's azimuth absolute position in encoder ticks.
   *
   * @return 0 - 4095, corresponding to one full revolution.
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

  public double getDriveSetpointMax() {
    return driveSetpointMax;
  }

  public boolean isInverted() {
    return isInverted;
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
