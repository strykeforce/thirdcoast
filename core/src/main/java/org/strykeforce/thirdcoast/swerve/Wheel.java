package org.strykeforce.thirdcoast.swerve;

import static com.ctre.phoenix.motorcontrol.ControlMode.MotionMagic;
import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;
import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity;
import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.OPEN_LOOP;
import static org.strykeforce.thirdcoast.swerve.Wheel.State.HIGH;
import static org.strykeforce.thirdcoast.swerve.Wheel.State.INIT;
import static org.strykeforce.thirdcoast.swerve.Wheel.State.LOW;
import static org.strykeforce.thirdcoast.swerve.Wheel.State.MID;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import java.util.function.DoubleConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode;
import org.strykeforce.thirdcoast.talon.Errors;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.util.Settings;

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

  private static final String TABLE = "THIRDCOAST.WHEEL";
  private static final int TICKS = 4096;
  private static final int LOW_SHIFT = 400;
  private static final int MID_SHIFT = 4000;
  private static final int LOW_SLOT = 0;
  private static final int MID_SLOT = 1;
  private static final int HIGH_SLOT = 2;
  private static final Logger logger = LoggerFactory.getLogger(Wheel.class);
  private final double kDriveSetpointMax;
  private final TalonSRX azimuthTalon;
  private final TalonSRX driveTalon;
  private final DoubleConsumer openLoopDriver;
  private final DoubleConsumer closedLoopDriver;
  private DoubleConsumer currentDriver;
  private int prevDriverSlot = -1;
  private State state;
  /**
   * This designated constructor constructs a wheel by supplying azimuth and drive talons. They are
   * initialized with Talon configurations named "azimuth" and "drive" respectively.
   *
   * @param settings the settings from TOML config file
   * @param azimuth the configured azimuth TalonSRX
   * @param drive the configured drive TalonSRX
   */
  public Wheel(Settings settings, TalonSRX azimuth, TalonSRX drive) {

    Toml toml = settings.getTable(TABLE);
    kDriveSetpointMax = (double) toml.getLong("driveSetpointMax");

    azimuthTalon = azimuth;
    driveTalon = drive;

    openLoopDriver = (setpoint) -> driveTalon.set(PercentOutput, setpoint);
    closedLoopDriver = closedLoopDriver();

    if (azimuthTalon == null || driveTalon == null) {
      logger.error("Talons missing, aborting initialization");
      return;
    }

    setDriveMode(OPEN_LOOP);

    logger.debug("azimuth = {} drive = {}", azimuthTalon.getDeviceID(), driveTalon.getDeviceID());
    logger.debug("driveSetpointMax = {}", kDriveSetpointMax);
  }

  /**
   * Convenience constructor for a wheel by specifying the swerve driveTalon wheel number (0-3).
   *
   * @param talons the TalonFactory used to create Talons
   * @param settings the settings from TOML config file
   * @param index the wheel number
   */
  public Wheel(Talons talons, Settings settings, int index) {
    this(settings, talons.getTalon(index), talons.getTalon(index + 10));
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
    // don't reset wheel azimuth direction to zero when returning to neutral
    if (drive == 0) {
      currentDriver.accept(0d);
      return;
    }

    azimuth *= -TICKS; // flip azimuth, hardware configuration dependent

    double azimuthPosition = azimuthTalon.getSelectedSensorPosition(0);
    double azimuthError = Math.IEEEremainder(azimuth - azimuthPosition, TICKS);

    // minimize azimuth rotation, reversing drive if necessary
    if (Math.abs(azimuthError) > 0.25 * TICKS) {
      azimuthError -= Math.copySign(0.5 * TICKS, azimuthError);
      drive = -drive;
    }

    azimuthTalon.set(MotionMagic, azimuthPosition + azimuthError);
    currentDriver.accept(drive);
  }

  /**
   * Set the drive mode
   *
   * @param driveMode the drive mode
   */
  public void setDriveMode(DriveMode driveMode) {
    switch (driveMode) {
      case OPEN_LOOP:
        currentDriver = openLoopDriver;
        break;
      case CLOSED_LOOP:
        currentDriver = closedLoopDriver;
        state = INIT;
        break;
    }
  }

  /**
   * Stop azimuth and drive movement. This resets the azimuth setpoint and relative encoder to the
   * current position in case the wheel has been manually rotated away from its previous setpoint.
   */
  public void stop() {
    azimuthTalon.set(MotionMagic, azimuthTalon.getSelectedSensorPosition(0));
    currentDriver.accept(0d);
  }

  /**
   * Set the azimuthTalon encoder relative to wheel zero alignment position.
   *
   * @param zero encoder position (in ticks) where wheel is zeroed.
   */
  public void setAzimuthZero(int zero) {
    if (azimuthTalon == null) {
      logger.error("azimuth Talon not present, aborting setAzimuthZero(int)");
      return;
    }
    int azimuthSetpoint = getAzimuthAbsolutePosition() - zero;
    ErrorCode e = azimuthTalon.setSelectedSensorPosition((int) azimuthSetpoint, 0, 10);
    Errors.check(e, logger);
    azimuthTalon.set(MotionMagic, azimuthSetpoint);
  }

  /**
   * Returns the wheel's azimuth absolute position in encoder ticks.
   *
   * @return 0 - 4095 encoder ticks
   */
  public int getAzimuthAbsolutePosition() {
    if (azimuthTalon == null) {
      logger.error("azimuth Talon not present, returning 0 for getAzimuthAbsolutePosition()");
      return 0;
    }
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
    return kDriveSetpointMax;
  }

  private DoubleConsumer closedLoopDriver() {
    return (setpoint) -> {
      int output = (int) (setpoint * kDriveSetpointMax);
      int magnitude = Math.abs(output);
      State prev = state;

      // We assume smooth transitions through states, starting with LOW. If not, for example jumping
      // from LOW to high speed, we will apply mid-range tuning for 1 control-loop iteration.
      switch (state) {
        case INIT:
          state = nextState(magnitude, prev, LOW);
          break;
        case LOW:
          if (magnitude > LOW.max) state = nextState(output, prev, MID);
          break;
        case MID:
          if (magnitude > MID.max) state = nextState(output, prev, HIGH);
          else if (magnitude < MID.min) state = nextState(output, prev, LOW);
          break;
        case HIGH:
          if (magnitude < HIGH.min) state = nextState(output, prev, MID);
          break;
      }

      if (state != prev) driveTalon.selectProfileSlot(state.slot, 0);
      driveTalon.set(Velocity, output);
    };
  }

  private State nextState(int output, State prev, State state) {
    logger.info("output = {}, switching state from {} to {}", output, prev, state);
    return state;
  }

  @Override
  public String toString() {
    return "Wheel{"
        + "azimuthTalon="
        + azimuthTalon
        + ", driveTalon="
        + driveTalon
        + ", kDriveSetpointMax="
        + kDriveSetpointMax
        + '}';
  }

  enum State {
    INIT(0, 0, 0),
    LOW(0, 500, 3000),
    MID(1, 2500, 6000),
    HIGH(2, 5000, 40_000);

    final int slot;
    final int min, max;

    State(int slot, int min, int max) {
      this.slot = slot;
      this.min = min;
      this.max = max;
    }
  }
}
