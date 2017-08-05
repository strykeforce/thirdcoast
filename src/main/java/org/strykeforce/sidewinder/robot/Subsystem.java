package org.strykeforce.sidewinder.robot;

public interface Subsystem {

  default void stop() {}

  default void zeroSensors() {}

  default void updateDashboard() {}
}
