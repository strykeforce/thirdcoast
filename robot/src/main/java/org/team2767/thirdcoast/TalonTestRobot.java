package org.team2767.thirdcoast;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.strykeforce.thirdcoast.talon.Talons;

public class TalonTestRobot extends IterativeRobot {

  private TalonSRX talon;
  private Timer timer = new Timer();
  private double setpoint;

  @Override
  public void robotInit() {
    URL config = null;
    try {
      config = new File("/home/lvuser/thirdcoast.toml").toURI().toURL();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    RobotComponent component = DaggerRobotComponent.builder().config(config).build();
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
