package org.strykeforce.thirdcoast.telemetry.tct;

import com.electronwill.nightconfig.core.InMemoryFormat;
import edu.wpi.first.wpilibj.SampleRobot;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;

public class Main extends SampleRobot {

  final static Logger logger = LoggerFactory.getLogger(Main.class);
  Menu menu;
  MainComponent component;
  Executor menuExecutor;

  @Override
  protected void robotInit() {
    logger.trace("robotInit start");
    MainComponent component = DaggerMainComponent.builder().toml(TalonProvisioner.DEFAULT).build();

    menu = component.menu();
    menuExecutor = Executors.newSingleThreadExecutor();
    menuExecutor.execute(menu);
    logger.trace("robotInit finish");
  }

  @Override
  public void operatorControl() {
    while (isEnabled()) {
      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        logger.debug("tele interrupted", e);
      }
    }
  }

  @Override
  protected void disabled() {
  }
}
