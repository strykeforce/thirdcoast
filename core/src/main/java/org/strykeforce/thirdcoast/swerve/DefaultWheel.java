package org.strykeforce.thirdcoast.swerve;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;
import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import java.util.function.DoubleConsumer;
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.util.Settings;

public class DefaultWheel extends Wheel {

  private final DoubleConsumer openLoopDriver;
  private final DoubleConsumer closedLoopDriver;

  public DefaultWheel(Settings settings, TalonSRX azimuth, TalonSRX drive) {
    super(settings, azimuth, drive);
    currentDriver = openLoopDriver = (setpoint) -> driveTalon.set(PercentOutput, setpoint);
    closedLoopDriver = (setpoint) -> driveTalon.set(Velocity, setpoint * kDriveSetpointMax);
    logger.info("initializing DEFAULT wheel");
  }

  /**
   * Convenience constructor for a wheel by specifying the swerve driveTalon wheel number (0-3).
   *
   * @param talons the TalonFactory used to create Talons
   * @param settings the settings from TOML config file
   * @param index the wheel number
   */
  public DefaultWheel(Talons talons, Settings settings, int index) {
    this(settings, talons.getTalon(index), talons.getTalon(index + 10));
  }

  /**
   * Set the operating mode of the wheel's drive motors. In this default wheel implementation {@code
   * OPEN_LOOP} and {@code TELEOP} are equivalent and {@code CLOSED_LOOP}, {@code TRAJECTORY} and
   * {@code AZIMUTH} are equivalent.
   *
   * @param driveMode the desired drive mode
   */
  @Override
  public void setDriveMode(DriveMode driveMode) {
    switch (driveMode) {
      case OPEN_LOOP:
      case TELEOP:
        currentDriver = openLoopDriver;
        break;
      case CLOSED_LOOP:
      case TRAJECTORY:
      case AZIMUTH:
        currentDriver = closedLoopDriver;
        break;
    }
  }
}
