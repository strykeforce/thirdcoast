package org.strykeforce.thirdcoast.telemetry.app;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import org.strykeforce.thirdcoast.telemetry.app.command.Command;
import org.strykeforce.thirdcoast.telemetry.grapher.GrapherController;

public class Main extends RobotBase {

  private final static boolean simulated = true;

  public Main() {
  }

  public static void main(String[] args) {
    RobotComponent component = DaggerSimulationComponent.create();
    GrapherController grapherController = component.grapherController();
    grapherController.start();
    Command command = component.mainCommand();
    try {
      System.out.println("\nPress <enter> key to stop.");
      System.in.read();
    } catch (Throwable ignored) {
    }
    System.exit(0);
  }

  @Override
  public void startCompetition() {
    RobotComponent component =
        simulated ? DaggerSimulationComponent.create() : DaggerRobotComponent.create();
    GrapherController grapherController = component.grapherController();
    grapherController.start();
//    Command command = component.mainCommand();
    while (true) {
      Timer.delay(0.1);
    }
  }
}
