package org.strykeforce.thirdcoast.telemetry.tct;

import java.io.File;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.tct.di.DaggerMainComponent;
import org.strykeforce.thirdcoast.telemetry.tct.di.MainComponent;

public class Main implements Runnable {

  private final static Logger logger = LoggerFactory.getLogger(Main.class);
  private final MainComponent component;

  Main() {
    component = DaggerMainComponent.builder().configFile(new File("tct.toml")).build();
  }

  @Override
  public void run() {
    Terminal terminal = component.terminal();
    terminal.writer().println(Main.class.getPackage().getImplementationTitle() + " " +
        Main.class.getPackage().getImplementationVersion());
    terminal.writer().println();
    try {
      Menu menu = component.menu();
      menu.display();
    } catch (Throwable t) {
      logger.error("fatal error", t);
      terminal.writer().println("fatal error: " + t.getMessage());
      terminal.flush();
      System.exit(-1);
    }
  }
}
