package org.strykeforce.thirdcoast.telemetry.sim;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryController;

public class Main {

  final static Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    logger.debug("Starting");
    SimulationComponent component = DaggerSimulationComponent.create();
    TelemetryController telemetryController = component.telemetryController();
    telemetryController.start();
    System.out.println();
    for (String end : telemetryController.getInventoryEndpoints()) {
      System.out.println("Inventory at " + end);
    }

    System.out.println("\nPress enter to exit.\n");
    try {
      System.in.read();
    } catch (IOException e) {
      logger.error("During read", e);
    }
    logger.info("Shutting down");
    telemetryController.shutdown();
    System.exit(0);
  }

}
