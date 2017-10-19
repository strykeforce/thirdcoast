package org.strykeforce.thirdcoast.telemetry.grapher;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleSupplier;

/**
 * An item that can be graphed.
 */
public interface Item {

  int id();

  Type type();

  String description();

  DoubleSupplier measurementFor(Measure measure);

  /**
   * Represents the type of sensors, actuators or subsystems we can send telemetry for.
   */
  enum Type {
    DIGITAL_INPUT, SERVO, TALON;

    private final static Map<Type, Set<Measure>> measures = new HashMap<>();

    static {
      measures.put(DIGITAL_INPUT, EnumSet.of(Measure.VALUE));
      measures.put(SERVO, EnumSet.of(Measure.POSITION, Measure.ANGLE));
      measures.put(TALON, EnumSet.range(Measure.SETPOINT, Measure.REVERSE_SOFT_LIMIT_OK));
    }

    public Set<Measure> measures() {
      return measures.get(this);
    }
  }

}
