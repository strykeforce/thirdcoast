package org.team2767.thirdcoast;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import org.strykeforce.thirdcoast.talon.TalonFactory;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class TalonTestRobot extends IterativeRobot {

  private ThirdCoastTalon talon;
  private Timer timer = new Timer();
  private double setpoint;

  @Override
  public void robotInit() {
    RobotComponent component = DaggerRobotComponent.builder().config(Robot.CONFIG_FILE).build();
    TalonFactory talonFactory = component.talonFactory();
    TelemetryService telemetryService = component.telemetryService();
//    talon = new TalonSRX(11);
    talon = (ThirdCoastTalon) talonFactory.getTalon(11);
    telemetryService.register(talon);
    ErrorCode err = talon.configVoltageCompSaturation(12, 10);
    check(err);
    err = talon.configContinuousCurrentLimit(50, 10);
    check(err);
    talon.enableCurrentLimit(false);
    talon.enableVoltageCompensation(true);
    telemetryService.start();
  }

  @Override
  public void teleopInit() {
    setpoint = 0.2;
    timer.start();
  }

  @Override
  public void teleopPeriodic() {
    // reverse Talon every 2 seconds
    setpoint *= timer.hasPeriodPassed(1) ? -1 : 1;
    talon.set(ControlMode.PercentOutput, setpoint);
  }

  private void check(ErrorCode code) {
    if (code != ErrorCode.OK) {
      System.out.println("Talon config error: " + code);
    }
  }
}
