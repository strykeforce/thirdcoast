package org.strykeforce.thirdcoast.telemetry.app;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.HLUsageReporting;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.internal.HardwareHLUsageReporting;
import edu.wpi.first.wpilibj.internal.HardwareTimer;
import org.strykeforce.thirdcoast.telemetry.app.command.Command;
import org.strykeforce.thirdcoast.telemetry.grapher.GrapherController;

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
    if (!simulated) {
      initializeHardwareConfiguration();
    }
    RobotComponent component =
        simulated ? DaggerSimulationComponent.create() : DaggerRobotComponent.create();
    GrapherController grapherController = component.grapherController();
    grapherController.start();
//    Command command = component.mainCommand();
//    command.run();
    System.exit(0);
  }

  public static void initializeHardwareConfiguration() {
    int rv = HAL.initialize(0);
    assert rv == 1;

    // Set some implementations so that the static methods work properly
    Timer.SetImplementation(new HardwareTimer());
    HLUsageReporting.SetImplementation(new HardwareHLUsageReporting());
    RobotState.SetImplementation(DriverStation.getInstance());
  }

}
