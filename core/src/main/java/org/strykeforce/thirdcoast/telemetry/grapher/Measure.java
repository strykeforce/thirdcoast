package org.strykeforce.thirdcoast.telemetry.grapher;

/** Available measurement types. */
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
  REVERSE_SOFT_LIMIT_OK("Reverse Soft Limit OK"),
  ANGLE("Angle"),
  POSITION("Position"),
  SPEED("Speed"),
  FEEDBACK("Feedback"),
  VALUE("Value"),
  ANALOG_RAW("Analog Raw"),
  MOMAGIC_ACCL("Motion Magic Acceleration"),
  MOMAGIC_A_TRAJ_POS("Motion Magic Trajectory Point Target Position"),
  MOMAGIC_A_TRAJ_VEL("Motion Magic Trajectory Point Target Velocity"),
  MOMAGIC_CRUISE_VEL("Motion Magic Cruise Velocity");

  //  private final static Map<Item.Type, Set<Measure>> byType = new HashMap<>();
  //
  //  static {
  //    byType.put(Type.DIGITAL_INPUT, EnumSet.of(VALUE));
  //    byType.put(Type.SERVO, EnumSet.of(POSITION, ANGLE));
  //
  //    byType.put(Type.TALON, EnumSet.allOf(Measure.class));
  //    byType.get(Type.TALON).removeAll(byType.get(Type.DIGITAL_INPUT));
  //    byType.get(Type.TALON).removeAll(byType.get(Type.SERVO));
  //  }
  //
  private final String description;

  Measure(String description) {
    this.description = description;
  }

  //  public static Set<Measure> measuresByType(String type) {
  //    return byType.getOrDefault(Type.valueOf(type), EnumSet.noneOf(Measure.class));
  //  }

  public String getDescription() {
    return description;
  }
}
