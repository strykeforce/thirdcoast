package org.strykeforce.sidewinder.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import org.strykeforce.sidewinder.swerve.SwerveDrive;
import org.strykeforce.sidewinder.talon.TalonParameters;

/**
 * Sidewinder test robot.
 */

public class Robot extends IterativeRobot {

  static {
    TalonParameters.register("/org/strykeforce/sidewinder.toml");
  }

  private final SwerveDrive drive = SwerveDrive.getInstance();
  private final Controls controls = Controls.getInstance();

  @Override
  public void robotInit() {
    drive.zeroSensors();
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
    double azimuth = controls.getTuner() * 0.5;
    drive.set(azimuth, 0);
  }

  @Override
  public void disabledInit() {
    drive.stop();
  }

  @Override
  public void disabledPeriodic() {
    drive.stop();
  }
}
