package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonFX
import org.strykeforce.telemetry.measurable.Measurable
import org.strykeforce.telemetry.measurable.Measure

internal const val CLOSED_LOOP_TARGET = "CLOSED_LOOP_TARGET"
internal const val STATOR_CURRENT = "STATOR_CURRENT"
internal const val SUPPLY_CURRENT = "SUPPLY_CURRENT"
internal const val OUTPUT_VOLTAGE = "OUTPUT_VOLTAGE"
internal const val OUTPUT_PERCENT = "OUTPUT_PERCENT"
internal const val INTEGRATED_SENSOR_POSITION = "INTEGRATED_SENSOR_POSITION"
internal const val INTEGRATED_SENSOR_VELOCITY = "INTEGRATED_SENSOR_VELOCITY"
internal const val INTEGRATED_SENSOR_ABSOLUTE_POSITION = "INTEGRATED_SENSOR_ABSOLUTE_POSITION"
internal const val SELECTED_SENSOR_POSITION = "SELECTED_SENSOR_POSITION"
internal const val SELECTED_SENSOR_VELOCITY = "SELECTED_SENSOR_VELOCITY"
internal const val ACTIVE_TRAJECTORY_POSITION = "ACTIVE_TRAJECTORY_POSITION"
internal const val ACTIVE_TRAJECTORY_VELOCITY = "ACTIVE_TRAJECTORY_VELOCITY"
internal const val ACTIVE_TRAJECTORY_ARB_FEED_FWD = "ACTIVE_TRAJECTORY_ARB_FEED_FWD"
internal const val CLOSED_LOOP_ERROR = "CLOSED_LOOP_ERROR"
internal const val BUS_VOLTAGE = "BUS_VOLTAGE"
internal const val ERROR_DERIVATIVE = "ERROR_DERIVATIVE"
internal const val INTEGRAL_ACCUMULATOR = "INTEGRAL_ACCUMULATOR"
internal const val FORWARD_LIMIT_SWITCH_CLOSED = "FORWARD_LIMIT_SWITCH_CLOSED"
internal const val REVERSE_LIMIT_SWITCH_CLOSED = "REVERSE_LIMIT_SWITCH_CLOSED"
internal const val TEMPERATURE = "TEMPERATURE"

class TalonFXMeasurable @JvmOverloads constructor(
    private val talonFX: TalonFX,
    override val description: String = "TalonFX ${talonFX.deviceID}"
) : Measurable {

    override val deviceId = talonFX.deviceID
    override val measures = setOf(
        Measure(CLOSED_LOOP_TARGET, "Closed-loop Setpoint (PID 0)") { talonFX.closedLoopTarget },
        Measure(STATOR_CURRENT, "Stator Current") { talonFX.statorCurrent },
        Measure(SUPPLY_CURRENT, "Supply Current") { talonFX.statorCurrent },
        Measure(OUTPUT_VOLTAGE, "Output Voltage") { talonFX.motorOutputVoltage },
        Measure(OUTPUT_PERCENT, "Output Percent") { talonFX.motorOutputPercent },
        Measure(
            INTEGRATED_SENSOR_POSITION,
            "Integrated Sensor Position"
        ) { sensorCollection.integratedSensorPosition },
        Measure(
            INTEGRATED_SENSOR_ABSOLUTE_POSITION,
            "Integrated Sensor Abs. Position"
        ) { sensorCollection.integratedSensorAbsolutePosition },
        Measure(
            INTEGRATED_SENSOR_VELOCITY,
            "Integrated Sensor Velocity"
        ) { sensorCollection.integratedSensorVelocity },
        Measure(
            SELECTED_SENSOR_POSITION,
            "Selected Sensor Position"
        ) { talonFX.selectedSensorPosition },
        Measure(
            SELECTED_SENSOR_VELOCITY,
            "Selected Sensor Velocity"
        ) { talonFX.selectedSensorVelocity },
        Measure(
            ACTIVE_TRAJECTORY_POSITION,
            "Active Trajectory Position"
        ) { talonFX.activeTrajectoryPosition },
        Measure(
            ACTIVE_TRAJECTORY_VELOCITY,
            "Active Trajectory Velocity"
        ) { talonFX.activeTrajectoryVelocity },
        Measure(
            ACTIVE_TRAJECTORY_ARB_FEED_FWD,
            "Active Trajectory Arb. Feed FWD"
        ) { talonFX.activeTrajectoryArbFeedFwd },
        Measure(CLOSED_LOOP_ERROR, "Closed Loop Error (PID 0)") { talonFX.closedLoopError },
        Measure(BUS_VOLTAGE, "Bus Voltage") { talonFX.busVoltage },
        Measure(ERROR_DERIVATIVE, "Error Derivative (PID 0)") { talonFX.errorDerivative },
        Measure(
            INTEGRAL_ACCUMULATOR,
            "Integral Accumulator (PID 0)"
        ) { talonFX.integralAccumulator },
        Measure(
            FORWARD_LIMIT_SWITCH_CLOSED,
            "Forward Limit Switch Closed"
        ) { sensorCollection.isFwdLimitSwitchClosed.toDouble() },
        Measure(
            REVERSE_LIMIT_SWITCH_CLOSED,
            "Reverse Limit Switch Closed"
        ) { sensorCollection.isRevLimitSwitchClosed.toDouble() },
        Measure(TEMPERATURE, "Controller Temperature") { talonFX.temperature }

    )

    private val sensorCollection = requireNotNull(talonFX.sensorCollection)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TalonFXMeasurable
        if (deviceId != other.deviceId) return false
        return true
    }

    override fun hashCode() = deviceId
}