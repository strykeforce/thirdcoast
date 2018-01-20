package org.team2767.thirdcoast;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;

public class TalonTestRobot extends IterativeRobot {

  private TalonSRX talon;
  private Timer timer = new Timer();
  private double setpoint;

  @Override
  public void robotInit() {
    talon = new TalonSRX(12);
    ErrorCode err = talon.configVoltageCompSaturation(12, 10);
    check(err);
    err = talon.configContinuousCurrentLimit(50, 10);
    check(err);
    err = talon.configPeakCurrentDuration(0, 10);
    check(err);
    talon.enableCurrentLimit(true);
    talon.enableVoltageCompensation(true);
  }

  @Override
  public void teleopInit() {
    setpoint = 0.2;
    timer.start();
  }

  @Override
  public void teleopPeriodic() {
    // reverse Talon every 2 seconds
    setpoint *= timer.hasPeriodPassed(2) ? -1 : 1;
    talon.set(ControlMode.PercentOutput, setpoint);
  }

  private void check(ErrorCode code) {
    if (code != ErrorCode.OK) {
      System.out.println("Talon config error: " + code);
    }
  }
}
