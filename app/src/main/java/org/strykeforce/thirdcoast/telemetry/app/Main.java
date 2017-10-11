package org.strykeforce.thirdcoast.telemetry.app;

import java.io.IOException;
import okio.Buffer;
import org.strykeforce.thirdcoast.telemetry.grapher.GrapherController;
import org.strykeforce.thirdcoast.telemetry.grapher.Inventory;

public class Main {

  public static void main(String[] args) {
    boolean simulated = false;
    for (String s : args) {
      switch (s) {
        case "-simulate":
          simulated = true;
          break;
        default:
          System.out.println("usage: java -jar jarfile [-simulate]");
          System.exit(-1);
      }
    }

    RobotComponent component = simulated ? DaggerSimulationComponent.create() : DaggerRobotComponent.create();
    GrapherController grapherController = component.grapherController();
    grapherController.start();
  }

}
