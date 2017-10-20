package org.strykeforce.thirdcoast.telemetry.sim;

import java.io.IOException;
import org.strykeforce.thirdcoast.telemetry.TelemetryController;

public class Main {

  public static void main(String[] args) {
    SimulationComponent component = DaggerSimulationComponent.create();
    TelemetryController telemetryController = component.telemetryController();
    telemetryController.start();

    System.out.println("\nPress enter to exit.\n");
    try {
      System.in.read();
    } catch (IOException e) {
    }
    System.exit(0);
  }

}
