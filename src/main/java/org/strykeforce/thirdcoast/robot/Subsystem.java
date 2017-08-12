package org.strykeforce.thirdcoast.robot;

public interface Subsystem {

  default void stop() {}

  default void zeroSensors() {}

  default void updateDashboard() {}
}
