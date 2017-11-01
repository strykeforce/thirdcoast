package org.strykeforce.thirdcoast.telemetry.tct;

import edu.wpi.first.wpilibj.SampleRobot;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Robot extends SampleRobot {

  final static Logger logger = LoggerFactory.getLogger(Robot.class);
  private Executor executor = Executors.newSingleThreadExecutor();

  @Override
  protected void robotInit() {
    executor.execute(new Main());
  }

  @Override
  public void operatorControl() {
    while (isEnabled()) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        logger.debug("operatorControl interrupted", e);
      }
    }
  }

  @Override
  protected void disabled() {
  }
}
