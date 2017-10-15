package org.strykeforce.thirdcoast.telemetry.grapher;

import java.util.Map;
import java.util.TreeMap;

public enum Measure {
  SETPOINT("Setpoint"),
  OUTPUT_CURRENT("Output Current"),
  OUTPUT_VOLTAGE("Output Voltage"),
  ENCODER_POSITION("Encoder Position"),
  ENCODER_VELOCITY("Encoder Velocity"),
  ABSOLUTE_ENCODER_POSITION("Absolute Encoder Position"),
  CONTROL_LOOP_ERROR("Control Loop Error"),
  INTEGRATOR_ACCUMULATOR("Integrator Accumulator"),
  BUS_VOLTAGE("Bus Voltage"),
  FORWARD_HARD_LIMIT_CLOSED("Forward Hard Limit Closed"),
  REVERSE_HARD_LIMIT_CLOSED("Reverse Hard Limit Closed"),
  FORWARD_SOFT_LIMIT_OK("Forward Soft Limit OK"),
  REVERSE_SOFT_LIMIT_OK("Reverse Soft Limit OK");

  private final String description;

  Measure(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
