package org.strykeforce.thirdcoast.telemetry.grapher;

/** Available telemetry measurement types. */
public enum Measure {
  CLOSED_LOOP_TARGET("Closed-loop Setpoint"),

  // BaseMotorController
  OUTPUT_CURRENT("Output Current"),
  OUTPUT_VOLTAGE("Output Voltage"),
  OUTPUT_PERCENT("Output Percentage"),
  SELECTED_SENSOR_POSITION("Selected Sensor Position"),
  SELECTED_SENSOR_VELOCITY("Selected Sensor Velocity"),
  ACTIVE_TRAJECTORY_POSITION("Active Trajectory Position"),
  ACTIVE_TRAJECTORY_VELOCITY("Active Trajectory Velocity"),
  //  ACTIVE_TRAJECTORY_HEADING("Active Trajectory Heading"),
  CLOSED_LOOP_ERROR("Closed Loop Error"),
  BUS_VOLTAGE("Bus Voltage"),
  ERROR_DERIVATIVE("Error Derivative"),
  INTEGRAL_ACCUMULATOR("Integral Accumulator"),
  // TODO: add motion profile

  // TalonSRX SensorCollection
  ANALOG_IN("Analog Input"),
  ANALOG_RAW("Analog Raw Input"),
  ANALOG_POSITION("Analog Position"),
  ANALOG_VELOCITY("Analog Velocity"),
  QUAD_POSITION("Quadrature Position"),
  QUAD_VELOCITY("Quadrature Velocity"),
  QUAD_A_PIN("Quadrature A State"),
  QUAD_B_PIN("Quadrature B State"),
  QUAD_IDX_PIN("Quadrature Index State"),
  PULSE_WIDTH_POSITION("Pulse Width Position"),
  PULSE_WIDTH_VELOCITY("Pulse Width Velocity"),
  PULSE_WIDTH_RISE_TO_FALL("Pulse Width Rise-Fall"),
  PULSE_WIDTH_RISE_TO_RISE("Pulse Width Rise-Rise"),
  FORWARD_LIMIT_SWITCH_CLOSED("Forward Limit Switch Closed"),
  REVERSE_LIMIT_SWITCH_CLOSED("Reverse Limit Switch Closed"),
  //  FORWARD_SOFT_LIMIT("Forward Soft Limit"),
  //  REVERSE_SOFT_LIMIT("Reverse Soft Limit"),

  ANGLE("Angle"),
  POSITION("Position"),
  //  SPEED("Speed"),
  VALUE("Value"),

  // Swerve
  GYRO_YAW("Gyro Yaw"),
  GYRO_YAW_RATE("Gyro Yaw Rate (deg/sec)"),
  SWERVE_FORWARD("Swerve Forward"),
  SWERVE_STRAFE("Swerve Strafe"),
  SWERVE_AZIMUTH("Swerve Azimuth"),

  // PathFinder
  PATH_SEG_X("Trajectory X Coordinate"),
  PATH_SEG_Y("Trajectory Y Coordinate"),
  PATH_SEG_POSITION_METERS("Trajectory Position (meters)"),
  PATH_SEG_POSITION_TICKS("Trajectory Position (ticks)"),
  PATH_SEG_VELOCITY("Trajectory Velocity"),
  PATH_SEG_ACCELERATION("Trajectory Acceleration"),
  PATH_SEG_JERK("Trajectory Jerk"),
  PATH_SEG_HEADING("Trajectory Heading"),
  PATH_DISTANCE("Distance Travelled"),
  PATH_POSITION_ERROR("Path Position Error"),
  ;

  private final String description;

  Measure(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
