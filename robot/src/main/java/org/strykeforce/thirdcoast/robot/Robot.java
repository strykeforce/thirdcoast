package org.strykeforce.thirdcoast.robot;

import com.ctre.CANTalon;
import com.electronwill.nightconfig.core.file.FileConfig;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.talon.StatusFrameRate;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

/**
 * Third Coast swerve drive demo robot.
 */

public class Robot extends IterativeRobot {

  final static Logger logger = LoggerFactory.getLogger(Robot.class);
  private final static String CONFIG = "/home/lvuser/thirdcoast.toml";
  private final static String DEFAULT_CONFIG = "/org/strykeforce/thirdcoast/defaults.toml";

  private TelemetryService telemetryService;
  private SwerveDrive swerve;
  private Controls controls;
  private final Trigger gyroResetButton = new Trigger() {
    @Override
    public boolean get() {
      return controls.getResetButton();
    }

    @Override
    public String toString() {
      return "gyro reset button";
    }
  };
  private final Trigger alignWheelsButton = new Trigger() {
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
    try {
      RobotComponent component;
      try (FileConfig toml = FileConfig.builder(CONFIG).defaultResource(DEFAULT_CONFIG).build()) {
        logger.info("loading robot configuration from {}", CONFIG);
        toml.load();
        component = DaggerRobotComponent.builder().toml(toml.unmodifiable()).build();
      }
      controls = component.controls();
//      swerve = component.swerveDrive();
      telemetryService = component.telemetryService();
      telemetryService.register(new CANTalon(6));
//      swerve.registerWith(telemetryService);
      StatusFrameRate rates = StatusFrameRate.builder().general(5).feedback(5).build();
      telemetryService.configureStatusFrameRates(6, rates);
      telemetryService.start();
//      swerve.zeroAzimuthEncoders();
    } catch (Throwable t) {
      logger.error("Error initializing robot", t);
    }
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
    logger.info("Robot is disabled");
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

}
