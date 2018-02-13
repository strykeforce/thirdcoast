package org.team2767.thirdcoast;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

/** Third Coast swerve drive demo robot. */
public class Robot extends IterativeRobot {

  static final Logger logger = LoggerFactory.getLogger(Robot.class);

  private RobotComponent component;
  private TelemetryService telemetryService;
  private SwerveDrive swerve;
  private Controls controls;
  private final Trigger gyroResetButton =
      new Trigger() {
        @Override
        public boolean get() {
          return controls.getResetButton();
        }

        @Override
        public String toString() {
          return "gyro reset button";
        }
      };
  private final Trigger alignWheelsButton =
      new Trigger() {
        @Override
        public boolean get() {
          return controls.getGamepadBackButton() && controls.getGamepadStartButton();
        }

        @Override
        public String toString() {
          return "wheel alignment button combination";
        }
      };

  @Override
  public void robotInit() {
    logger.info("Robot is initializing");
    controls = getComponent().controls();
    swerve = getComponent().swerveDrive();
    telemetryService = getComponent().telemetryService();
    swerve.registerWith(telemetryService);
    telemetryService.start();
    swerve.zeroAzimuthEncoders();
  }

  @Override
  public void teleopInit() {
    logger.info("Robot is enabled in tele-op");
    swerve.stop();
  }

  @Override
  public void teleopPeriodic() {
    if (gyroResetButton.hasActivated()) {
      String msg = "Resetting gyro yaw zero";
      logger.warn(msg);
      DriverStation.reportWarning(msg, false);
      swerve.getGyro().zeroYaw();
    }
    double forward = applyDeadband(controls.getForward());
    double strafe = applyDeadband(controls.getStrafe());
    double azimuth = applyDeadband(controls.getAzimuth());

    swerve.drive(forward, strafe, azimuth);
  }

  @Override
  public void disabledInit() {
    Talons.dump(swerve.getWheels()[0].getAzimuthTalon());
  }

  @Override
  public void disabledPeriodic() {
    if (alignWheelsButton.hasActivated()) {
      swerve.saveAzimuthPositions();
      swerve.zeroAzimuthEncoders();
      String msg = "drive wheels were re-aligned";
      logger.info(msg);
      DriverStation.reportWarning(msg, false);
    }
  }

  private double applyDeadband(double input) {
    if (Math.abs(input) < 0.05) {
      return 0;
    }
    return input;
  }

  private RobotComponent getComponent() {
    if (component == null) {
      //      URL config = this.getClass().getResource("/META-INF/robot/settings.toml");
      URL config = null;
      try {
        config = new File("/home/lvuser/thirdcoast.toml").toURI().toURL();
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
      logger.info("reading settings from '{}'", config);
      component = DaggerRobotComponent.builder().config(config).build();
    }
    return component;
  }
}
