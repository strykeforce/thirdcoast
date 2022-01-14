package org.strykeforce.telemetry.measurable

import com.ctre.phoenix.motorcontrol.can.BaseTalon

/** Represents a [BaseTalon] telemetry-enable [Measurable] item.  */
class BaseTalonMeasurable @JvmOverloads constructor(
    private val talon: BaseTalon, override val description: String = "BaseTalon ${talon.deviceID}"
) : Measurable {
    override val deviceId = talon.deviceID

    override val measures = setOf(Measure(
        CLOSED_LOOP_TARGET, "Closed-loop Setpoint (PID 0)"
    ) { talon.getClosedLoopTarget(0) },
        Measure(
            org.strykeforce.thirdcoast.talon.STATOR_CURRENT, "Stator Current"
        ) { talon.statorCurrent },
        Measure(
            org.strykeforce.thirdcoast.talon.SUPPLY_CURRENT, "Supply Current"
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
            ACTIVE_TRAJECTORY_POSITION, "Active Trajectory Position"
        ) { talon.activeTrajectoryPosition },
        Measure(
            ACTIVE_TRAJECTORY_VELOCITY, "Active Trajectory Velocity"
        ) { talon.activeTrajectoryVelocity },
        Measure(CLOSED_LOOP_ERROR, "Closed Loop Error (PID 0)") {
            talon.getClosedLoopError(0)
        },
        Measure(BUS_VOLTAGE, "Bus Voltage") { talon.busVoltage },
        Measure(ERROR_DERIVATIVE, "Error Derivative (PID 0)") { talon.getErrorDerivative(0) },
        Measure(
            INTEGRAL_ACCUMULATOR, "Integral Accumulator (PID 0)"
        ) { talon.getIntegralAccumulator(0) },
        Measure(TEMPERATURE, "Controller Temperature") { talon.temperature })


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseTalonMeasurable

        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId
}