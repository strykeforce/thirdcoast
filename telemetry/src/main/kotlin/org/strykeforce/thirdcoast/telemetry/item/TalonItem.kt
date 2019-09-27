package org.strykeforce.thirdcoast.telemetry.item

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import java.util.function.DoubleSupplier

internal const val CLOSED_LOOP_TARGET = "CLOSED_LOOP_TARGET"
internal const val OUTPUT_CURRENT = "OUTPUT_CURRENT"
internal const val OUTPUT_VOLTAGE = "OUTPUT_VOLTAGE"
internal const val OUTPUT_PERCENT = "OUTPUT_PERCENT"
internal const val SELECTED_SENSOR_POSITION = "SELECTED_SENSOR_POSITION"
internal const val SELECTED_SENSOR_VELOCITY = "SELECTED_SENSOR_VELOCITY"
internal const val ACTIVE_TRAJECTORY_POSITION = "ACTIVE_TRAJECTORY_POSITION"
internal const val ACTIVE_TRAJECTORY_VELOCITY = "ACTIVE_TRAJECTORY_VELOCITY"
internal const val CLOSED_LOOP_ERROR = "CLOSED_LOOP_ERROR"
internal const val BUS_VOLTAGE = "BUS_VOLTAGE"
internal const val ERROR_DERIVATIVE = "ERROR_DERIVATIVE"
internal const val INTEGRAL_ACCUMULATOR = "INTEGRAL_ACCUMULATOR"
internal const val ANALOG_IN = "ANALOG_IN"
internal const val ANALOG_IN_RAW = "ANALOG_IN_RAW"
internal const val ANALOG_IN_VELOCITY = "ANALOG_IN_VELOCITY"
internal const val QUAD_A_PIN = "QUAD_A_PIN"
internal const val QUAD_B_PIN = "QUAD_B_PIN"
internal const val QUAD_IDX_PIN = "QUAD_IDX_PIN"
internal const val PULSE_WIDTH_POSITION = "PULSE_WIDTH_POSITION"
internal const val PULSE_WIDTH_VELOCITY = "PULSE_WIDTH_VELOCITY"
internal const val PULSE_WIDTH_RISE_TO_FALL = "PULSE_WIDTH_RISE_TO_FALL"
internal const val PULSE_WIDTH_RISE_TO_RISE = "PULSE_WIDTH_RISE_TO_RISE"
internal const val FORWARD_LIMIT_SWITCH_CLOSED = "FORWARD_LIMIT_SWITCH_CLOSED"
internal const val REVERSE_LIMIT_SWITCH_CLOSED = "REVERSE_LIMIT_SWITCH_CLOSED"

/** Represents a [TalonSRX] telemetry-enable `Measurable` item.  */
class TalonItem @JvmOverloads constructor(
  private val talon: TalonSRX,
  override val description: String = "TalonSRX ${talon.deviceID}"
) : Measurable {

  override val deviceId = talon.deviceID
  override val type = "talon"
  override val measures = setOf(
    Measure(CLOSED_LOOP_TARGET, "Closed-loop Setpoint (PID 0)"),
    Measure(OUTPUT_CURRENT, "Output Current"),
    Measure(OUTPUT_VOLTAGE, "Output Voltage"),
    Measure(OUTPUT_PERCENT, "Output Percentage"),
    Measure(SELECTED_SENSOR_POSITION, "Selected Sensor Position (PID 0)"),
    Measure(SELECTED_SENSOR_VELOCITY, "Selected Sensor Velocity (PID 0)"),
    Measure(ACTIVE_TRAJECTORY_POSITION, "Active Trajectory Position"),
    Measure(ACTIVE_TRAJECTORY_VELOCITY, "Active Trajectory Velocity"),
    Measure(CLOSED_LOOP_ERROR, "Closed Loop Error (PID 0)"),
    Measure(BUS_VOLTAGE, "Bus Voltage"),
    Measure(ERROR_DERIVATIVE, "Error Derivative (PID 0)"),
    Measure(INTEGRAL_ACCUMULATOR, "Integral Accumulator (PID 0)"),
    Measure(ANALOG_IN, "Analog Position Input"),
    Measure(ANALOG_IN_RAW, "Analog Raw Input"),
    Measure(ANALOG_IN_VELOCITY, "Analog Velocity Input"),
    Measure(QUAD_A_PIN, "Quad A Pin State"),
    Measure(QUAD_B_PIN, "Quad B Pin State"),
    Measure(QUAD_IDX_PIN, "Quad Index Pin State"),
    Measure(QUAD_POSITION, "Quad Position"),
    Measure(QUAD_VELOCITY, "Quad Velocity"),
    Measure(PULSE_WIDTH_POSITION, "Pulse Width Position"),
    Measure(PULSE_WIDTH_VELOCITY, "Pulse Width Velocity"),
    Measure(PULSE_WIDTH_RISE_TO_FALL, "PWM Pulse Width"),
    Measure(PULSE_WIDTH_RISE_TO_RISE, "PWM Period"),
    Measure(FORWARD_LIMIT_SWITCH_CLOSED, "Forward Limit Switch Closed"),
    Measure(REVERSE_LIMIT_SWITCH_CLOSED, "Reverse Limit Switch Closed")
  )

  private val sensorCollection = requireNotNull(talon.sensorCollection)

  override fun measurementFor(measure: Measure): DoubleSupplier {
    return when (measure.name) {
      CLOSED_LOOP_TARGET -> DoubleSupplier { talon.getClosedLoopTarget(0) }
      OUTPUT_CURRENT -> DoubleSupplier { talon.outputCurrent }
      OUTPUT_VOLTAGE -> DoubleSupplier { talon.motorOutputVoltage }
      OUTPUT_PERCENT -> DoubleSupplier { talon.motorOutputPercent }
      SELECTED_SENSOR_POSITION -> DoubleSupplier { talon.getSelectedSensorPosition(0).toDouble() }
      SELECTED_SENSOR_VELOCITY -> DoubleSupplier { talon.getSelectedSensorVelocity(0).toDouble() }
      ACTIVE_TRAJECTORY_POSITION -> DoubleSupplier { talon.activeTrajectoryPosition.toDouble() }
      ACTIVE_TRAJECTORY_VELOCITY -> DoubleSupplier { talon.activeTrajectoryVelocity.toDouble() }
      CLOSED_LOOP_ERROR -> DoubleSupplier { talon.getClosedLoopError(0).toDouble() }
      BUS_VOLTAGE -> DoubleSupplier { talon.busVoltage }
      ERROR_DERIVATIVE -> DoubleSupplier { talon.getErrorDerivative(0) }
      INTEGRAL_ACCUMULATOR -> DoubleSupplier { talon.getIntegralAccumulator(0) }
      ANALOG_IN -> DoubleSupplier { sensorCollection.analogIn.toDouble() }
      ANALOG_IN_RAW -> DoubleSupplier { sensorCollection.analogInRaw.toDouble() }
      ANALOG_IN_VELOCITY -> DoubleSupplier { sensorCollection.analogInVel.toDouble() }
      QUAD_POSITION -> DoubleSupplier { sensorCollection.quadraturePosition.toDouble() }
      QUAD_VELOCITY -> DoubleSupplier { sensorCollection.quadratureVelocity.toDouble() }
      QUAD_A_PIN -> DoubleSupplier { sensorCollection.pinStateQuadA.toDouble() }
      QUAD_B_PIN -> DoubleSupplier { sensorCollection.pinStateQuadB.toDouble() }
      QUAD_IDX_PIN -> DoubleSupplier { sensorCollection.pinStateQuadIdx.toDouble() }
      PULSE_WIDTH_POSITION -> DoubleSupplier { (sensorCollection.pulseWidthPosition and 0xFFF).toDouble() }
      PULSE_WIDTH_VELOCITY -> DoubleSupplier { sensorCollection.pulseWidthVelocity.toDouble() }
      PULSE_WIDTH_RISE_TO_FALL -> DoubleSupplier { sensorCollection.pulseWidthRiseToFallUs.toDouble() }
      PULSE_WIDTH_RISE_TO_RISE -> DoubleSupplier { sensorCollection.pulseWidthRiseToRiseUs.toDouble() }
      FORWARD_LIMIT_SWITCH_CLOSED -> DoubleSupplier { sensorCollection.isFwdLimitSwitchClosed.toDouble() }
      REVERSE_LIMIT_SWITCH_CLOSED -> DoubleSupplier { sensorCollection.isRevLimitSwitchClosed.toDouble() }
      else -> DoubleSupplier { 2767.0 }
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as TalonItem

    if (deviceId != other.deviceId) return false

    return true
  }

  override fun hashCode() = deviceId
}
