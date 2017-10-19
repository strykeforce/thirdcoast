package org.strykeforce.thirdcoast.telemetry.tct;

import com.ctre.CANTalon;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.grapher.GrapherController;

public class Main extends SampleRobot {

  CANTalon talon = new CANTalon(6);

  @Override
  protected void robotInit() {
    TelemetryService telemetry = new TelemetryService();
    telemetry.register(talon);
    telemetry.start();
    talon.changeControlMode(TalonControlMode.Voltage);
    talon.setVoltageRampRate(0);
    talon.setVoltageCompensationRampRate(0);
    talon.setStatusFrameRateMs(StatusFrameRate.General, 5);
    talon.setStatusFrameRateMs(StatusFrameRate.Feedback, 5);
    talon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 5);
    talon.setStatusFrameRateMs(StatusFrameRate.PulseWidth, 5);
    talon.setStatusFrameRateMs(StatusFrameRate.AnalogTempVbat, 5);
    talon.setCurrentLimit(10);
  }

  @Override
  public void operatorControl() {
    long start = System.currentTimeMillis();
    try {
      while (true) {
        long elapsed = System.currentTimeMillis() - start;
        if (elapsed < 4000) {
          talon.set(0);
        } else if (elapsed < 8000) {
          talon.set(12);
        } else if (elapsed < 12000) {
          talon.set(6);
        } else {
          talon.set(0);
        }
        Thread.sleep(10);
      }
    } catch (InterruptedException e) {
    }
  }

  @Override
  protected void disabled() {
  }
}
