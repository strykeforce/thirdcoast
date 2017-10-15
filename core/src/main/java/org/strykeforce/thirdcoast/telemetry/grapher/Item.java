package org.strykeforce.thirdcoast.telemetry.grapher;

import java.util.function.DoubleSupplier;

/**
 * An item that can be graphed.
 */
public interface Item {

  int id();

  String type();

  String description();

  DoubleSupplier measurementFor(Measure measure);

}
