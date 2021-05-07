package org.strykeforce.telemetry.measurable

import com.ctre.phoenix.motorcontrol.can.TalonSRX

internal const val CLOSED_LOOP_TARGET = "CLOSED_LOOP_TARGET"
internal const val STATOR_CURRENT = "STATOR_CURRENT"
internal const val SUPPLY_CURRENT = "SUPPLY_CURRENT"
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
internal const val TEMPERATURE = "TEMPERATURE"

/** Represents a [TalonSRX] telemetry-enable `Measurable` item.  */
class TalonSRXMeasurable @JvmOverloads constructor(
    private val talon: TalonSRX,
    override val description: String = "TalonSRX ${talon.deviceID}"
) : Measurable {

    override val deviceId = talon.deviceID
    override val measures = setOf(
        Measure(
            CLOSED_LOOP_TARGET,
            "Closed-loop Setpoint (PID 0)"
        ) { talon.getClosedLoopTarget(0) },
        Measure(
            org.strykeforce.thirdcoast.talon.STATOR_CURRENT,
            "Stator Current"
        ) { talon.statorCurrent },
        Measure(
            org.strykeforce.thirdcoast.talon.SUPPLY_CURRENT,
            "Supply Current"
        ) { talon.supplyCurrent },
        Measure(OUTPUT_VOLTAGE, "Output Voltage") { talon.motorOutputVoltage },
        Measure(OUTPUT_PERCENT, "Output Percentage") { talon.motorOutputPercent },
        Measure(SELECTED_SENSOR_POSITION, "Selected Sensor Position (PID 0)") {
            talon.getSelectedSensorPosition(0)
        },
        Measure(SELECTED_SENSOR_VELOCITY, "Selected Sensor Velocity (PID 0)") {
            talon.getSelectedSensorVelocity(0)
        },
        Measure(
            ACTIVE_TRAJECTORY_POSITION,
            "Active Trajectory Position"
        ) { talon.activeTrajectoryPosition },
        Measure(
            ACTIVE_TRAJECTORY_VELOCITY,
            "Active Trajectory Velocity"
        ) { talon.activeTrajectoryVelocity },
        Measure(CLOSED_LOOP_ERROR, "Closed Loop Error (PID 0)") {
            talon.getClosedLoopError(0)
        },
        Measure(BUS_VOLTAGE, "Bus Voltage") { talon.busVoltage },
        Measure(ERROR_DERIVATIVE, "Error Derivative (PID 0)") { talon.getErrorDerivative(0) },
        Measure(
            INTEGRAL_ACCUMULATOR,
            "Integral Accumulator (PID 0)"
        ) { talon.getIntegralAccumulator(0) },
        Measure(ANALOG_IN, "Analog Position Input") { sensorCollection.analogIn.toDouble() },
        Measure(ANALOG_IN_RAW, "Analog Raw Input") { sensorCollection.analogInRaw.toDouble() },
        Measure(
            ANALOG_IN_VELOCITY,
            "Analog Velocity Input"
        ) { sensorCollection.analogInVel.toDouble() },
        Measure(QUAD_A_PIN, "Quad A Pin State") { sensorCollection.pinStateQuadA.toDouble() },
        Measure(QUAD_B_PIN, "Quad B Pin State") { sensorCollection.pinStateQuadB.toDouble() },
        Measure(
            QUAD_IDX_PIN,
            "Quad Index Pin State"
        ) { sensorCollection.pinStateQuadIdx.toDouble() },
        Measure(QUAD_POSITION, "Quad Position") { sensorCollection.quadraturePosition.toDouble() },
        Measure(QUAD_VELOCITY, "Quad Velocity") { sensorCollection.quadratureVelocity.toDouble() },
        Measure(
            PULSE_WIDTH_POSITION,
            "Pulse Width Position"
        ) { (sensorCollection.pulseWidthPosition and 0xFFF).toDouble() },
        Measure(
            PULSE_WIDTH_VELOCITY,
            "Pulse Width Velocity"
        ) { sensorCollection.pulseWidthVelocity.toDouble() },
        Measure(
            PULSE_WIDTH_RISE_TO_FALL,
            "PWM Pulse Width"
        ) { sensorCollection.pulseWidthRiseToFallUs.toDouble() },
        Measure(
            PULSE_WIDTH_RISE_TO_RISE,
            "PWM Period"
        ) { sensorCollection.pulseWidthRiseToRiseUs.toDouble() },
        Measure(
            FORWARD_LIMIT_SWITCH_CLOSED,
            "Forward Limit Switch Closed"
        ) { sensorCollection.isFwdLimitSwitchClosed.toDouble() },
        Measure(
            REVERSE_LIMIT_SWITCH_CLOSED,
            "Reverse Limit Switch Closed"
        ) { sensorCollection.isRevLimitSwitchClosed.toDouble() },
        Measure(TEMPERATURE, "Controller Temperature") { talon.temperature }
    )

    private val sensorCollection = requireNotNull(talon.sensorCollection)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TalonSRXMeasurable

        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId
}
