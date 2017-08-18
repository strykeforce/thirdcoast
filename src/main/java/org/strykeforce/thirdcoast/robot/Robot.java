package org.strykeforce.thirdcoast.robot;

import com.electronwill.nightconfig.core.file.FileConfig;
import edu.wpi.first.wpilibj.IterativeRobot;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.talon.TalonParameters;

/**
 * Third Coast test robot.
 */

public class Robot extends IterativeRobot {

  static {
    try {
      FileConfig config = FileConfig.builder("/home/lvuser/thirdcoast.toml")
          .defaultResource("/org/strykeforce/thirdcoast.toml")
          .build();
      config.load();
      config.close();
      TalonParameters.register(config.unmodifiable());
    } catch (Exception e) {
      e.printStackTrace();
    }
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
