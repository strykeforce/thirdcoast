package org.strykeforce.thirdcoast.telemetry.grapher;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.DoubleSupplier;

/**
 * An item that can be graphed.
 */
public interface Item {

  int id();

  String type();

  String description();

  DoubleSupplier measurementFor(Measure measure);

  public enum Measure {
    SETPOINT(0, "Setpoint"),
    OUTPUT_CURRENT(1, "Output Current"),
    OUTPUT_VOLTAGE(2, "Output Voltage"),
    ENCODER_POSITION(3, "Encoder Position"),
    ENCODER_VELOCITY(4, "Encoder Velocity"),
    ABSOLUTE_ENCODER_POSITION(5, "Absolute Encoder Position"),
    CONTROL_LOOP_ERROR(6, "Control Loop Error"),
    INTEGRATOR_ACCUMULATOR(7, "Integrator Accumulator"),
    BUS_VOLTAGE(8, "Bus Voltage"),
    FORWARD_HARD_LIMIT_CLOSED(9, "Forward Hard Limit Closed"),
    REVERSE_HARD_LIMIT_CLOSED(10, "Reverse Hard Limit Closed"),
    FORWARD_SOFT_LIMIT_OK(11, "Forward Soft Limit OK"),
    REVERSE_SOFT_LIMIT_OK(12, "Reverse Soft Limit OK");

    private final static Map<Integer, Measure> map;

    static {
      map = new TreeMap<>();
      for (Measure m : Measure.values()) {
        map.put(m.jsonId, m);
      }
    }

    private final int jsonId;
    private final String description;

    Measure(int jsonId, String description) {
      this.jsonId = jsonId;
      this.description = description;
    }

    public static Measure findByJsonId(int jsonId) {
      if (jsonId < 0 || jsonId > 12) {
        throw new IllegalArgumentException("jsonId out of range: " + jsonId);
      }
      return map.get(jsonId);
    }

    public int getJsonId() {
      return jsonId;
    }

    public String getDescription() {
      return description;
    }
  }

}
