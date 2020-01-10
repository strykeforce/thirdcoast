package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonFX
import org.strykeforce.thirdcoast.telemetry.item.Measurable
import org.strykeforce.thirdcoast.telemetry.item.Measure
import java.util.function.DoubleSupplier

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

class TalonFXItem @JvmOverloads constructor(
    private val talonFX: TalonFX,
    override val description: String = "TalonFX ${talonFX.deviceID}"
) : Measurable {

    override val deviceId = talonFX.deviceID
    override val type = "talonFX"
    override val measures = setOf(
        Measure(CLOSED_LOOP_TARGET, "Closed-loop Setpoint (PID 0)"),
        Measure(STATOR_CURRENT, "Stator Current"),
        Measure(SUPPLY_CURRENT, "Supply Current"),
        Measure(OUTPUT_VOLTAGE, "Output Voltage"),
        Measure(OUTPUT_PERCENT, "Output Percent"),
        Measure(INTEGRATED_SENSOR_POSITION, "Integrated Sensor Position"),
        Measure(INTEGRATED_SENSOR_ABSOLUTE_POSITION, "Integrated Sensor ABS Position"),
        Measure(INTEGRATED_SENSOR_VELOCITY, "Integrated Sensor Velocity"),
        Measure(SELECTED_SENSOR_POSITION, "Selected Sensor Position"),
        Measure(SELECTED_SENSOR_VELOCITY, "Selected Sensor Velocity"),
        Measure(ACTIVE_TRAJECTORY_POSITION, "Active Trajectory Position"),
        Measure(ACTIVE_TRAJECTORY_VELOCITY, "Active Trajectory Velocity"),
        Measure(ACTIVE_TRAJECTORY_ARB_FEED_FWD, "Active Trajectory Arb. Feed FWD"),
        Measure(CLOSED_LOOP_ERROR, "Closed Loop Error (PID 0)"),
        Measure(BUS_VOLTAGE, "Bus Voltage"),
        Measure(ERROR_DERIVATIVE, "Error Derivative (PID 0)"),
        Measure(INTEGRAL_ACCUMULATOR, "Integral Accumulator (PID 0)"),
        Measure(FORWARD_LIMIT_SWITCH_CLOSED, "Forward Limit Switch Closed"),
        Measure(REVERSE_LIMIT_SWITCH_CLOSED, "Reverse Limit Switch Closed"),
        Measure(TEMPERATURE, "Controller Temperature")

    )

    private val sensorCollection = requireNotNull(talonFX.sensorCollection)


    override fun measurementFor(measure: Measure): DoubleSupplier {
        return when (measure.name) {
            CLOSED_LOOP_TARGET -> DoubleSupplier { talonFX.closedLoopTarget }
            STATOR_CURRENT -> DoubleSupplier { talonFX.statorCurrent }
            SUPPLY_CURRENT -> DoubleSupplier { talonFX.supplyCurrent }
            OUTPUT_VOLTAGE -> DoubleSupplier { talonFX.motorOutputVoltage }
            OUTPUT_PERCENT -> DoubleSupplier { talonFX.motorOutputPercent }
            INTEGRATED_SENSOR_VELOCITY -> DoubleSupplier { sensorCollection.integratedSensorVelocity }
            SELECTED_SENSOR_POSITION -> DoubleSupplier { talonFX.selectedSensorPosition.toDouble() }
            SELECTED_SENSOR_VELOCITY -> DoubleSupplier { talonFX.selectedSensorVelocity.toDouble() }
            ACTIVE_TRAJECTORY_POSITION -> DoubleSupplier { talonFX.activeTrajectoryPosition.toDouble() }
            ACTIVE_TRAJECTORY_VELOCITY -> DoubleSupplier { talonFX.activeTrajectoryVelocity.toDouble() }
            ACTIVE_TRAJECTORY_ARB_FEED_FWD -> DoubleSupplier { talonFX.activeTrajectoryArbFeedFwd }
            CLOSED_LOOP_ERROR -> DoubleSupplier { talonFX.closedLoopError.toDouble() }
            BUS_VOLTAGE -> DoubleSupplier { talonFX.busVoltage }
            ERROR_DERIVATIVE -> DoubleSupplier { talonFX.errorDerivative }
            INTEGRAL_ACCUMULATOR -> DoubleSupplier { talonFX.integralAccumulator }
            FORWARD_LIMIT_SWITCH_CLOSED -> DoubleSupplier { sensorCollection.isFwdLimitSwitchClosed.toDouble() }
            REVERSE_LIMIT_SWITCH_CLOSED -> DoubleSupplier { sensorCollection.isRevLimitSwitchClosed.toDouble() }
            INTEGRATED_SENSOR_POSITION -> DoubleSupplier { sensorCollection.integratedSensorPosition }
            INTEGRATED_SENSOR_ABSOLUTE_POSITION -> DoubleSupplier { sensorCollection.integratedSensorAbsolutePosition }
            TEMPERATURE -> DoubleSupplier { talonFX.temperature }
            else -> DoubleSupplier { 2767.0 }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (javaClass != other?.javaClass) return false
        other as TalonFXItem
        if (deviceId != other.deviceId) return false
        return true
    }

    override fun hashCode() = deviceId
}