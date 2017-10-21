package org.strykeforce.thirdcoast.robot;

import com.electronwill.nightconfig.core.file.FileConfig;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

/**
 * Third Coast swerve drive demo robot.
 */

public class Robot extends IterativeRobot {

  private final static String CONFIG = "/home/lvuser/thirdcoast.toml";
  private final static String DEFAULT_CONFIG = "/org/strykeforce/thirdcoast.toml";

  /*
   * Register Talon parameters in static initializer. This must be done before the SwerveDrive
   * object is instantiated below.
   */
  static {
  }

  private TelemetryService telemetryService;
  private SwerveDrive swerve;
  private Controls controls;
  private final Trigger gyroResetButton = new Trigger() {
    @Override
    public boolean get() {
      return controls.getResetButton();
    }
  };
  private final Trigger alignWheelsButton = new Trigger() {
    @Override
    public boolean get() {
      return controls.getGamepadBackButton() && controls.getGamepadStartButton();
    }
  };

  @Override
  public void robotInit() {
    RobotComponent component;
    try (FileConfig toml = FileConfig.builder(CONFIG).defaultResource(DEFAULT_CONFIG).build()) {
      toml.load();
      component = DaggerRobotComponent.builder().toml(toml.unmodifiable()).build();
    }
    controls = component.controls();
    swerve = component.swerveDrive();
    telemetryService = component.telemetryService();
    swerve.registerWith(telemetryService);
    telemetryService.start();
    swerve.zeroAzimuthEncoders();
  }

  @Override
  public void teleopInit() {
    swerve.stop();
  }

  @Override
  public void teleopPeriodic() {
    if (gyroResetButton.hasActivated()) {
      swerve.getGyro().zeroYaw();
    }
    double forward = applyDeadband(controls.getForward());
    double strafe = applyDeadband(controls.getStrafe());
    double azimuth = applyDeadband(controls.getAzimuth());

    swerve.drive(forward, strafe, azimuth);
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    if (alignWheelsButton.hasActivated()) {
      swerve.saveAzimuthPositions();
      swerve.zeroAzimuthEncoders();
      DriverStation.reportWarning("drive wheels were re-aligned", false);
    }
  }

  private double applyDeadband(double input) {
    if (Math.abs(input) < 0.05) {
      return 0;
    }
    return input;
  }

}
