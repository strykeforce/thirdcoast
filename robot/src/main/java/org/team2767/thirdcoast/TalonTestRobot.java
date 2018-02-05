package org.team2767.thirdcoast;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import java.io.File;
import org.strykeforce.thirdcoast.talon.Talons;

public class TalonTestRobot extends IterativeRobot {

  static final File CONFIG_FILE = new File("/home/lvuser/thirdcoast.toml");

  private TalonSRX talon;
  private Timer timer = new Timer();
  private double setpoint;

  @Override
  public void robotInit() {
    RobotComponent component = DaggerRobotComponent.builder().config(CONFIG_FILE).build();
    Talons talons = component.talons();
    talon = talons.getTalon(6);
    Talons.dump(talon);
  }

  @Override
  public void teleopInit() {
    setpoint = 0.5;
    timer.start();
  }

  @Override
  public void teleopPeriodic() {
    // reverse Talon every 2 seconds
    setpoint *= timer.hasPeriodPassed(2) ? -1 : 1;
    talon.set(ControlMode.PercentOutput, setpoint);
  }
}
