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

  private final SwerveDrive swerve = SwerveDrive.getInstance();
  private final Controls controls = Controls.getInstance();

  @Override
  public void robotInit() {
    swerve.zeroSensors();
  }

  @Override
  public void teleopInit() {
  }

  private double applyDeadband(double input) {
    if (Math.abs(input) < 0.05) {
      return 0;
    }
    return input;
  }

  @Override
  public void teleopPeriodic() {
    double forward = applyDeadband(controls.getForward());
    double strafe = applyDeadband(controls.getStrafe());
    double azimuth = applyDeadband(controls.getAzimuth());

    swerve.drive(forward, strafe, azimuth);
  }

  @Override
  public void disabledInit() {
    swerve.stop();
  }

  @Override
  public void disabledPeriodic() {
    swerve.stop();
  }
}
