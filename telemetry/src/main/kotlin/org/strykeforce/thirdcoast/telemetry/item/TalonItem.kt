package org.strykeforce.thirdcoast.telemetry.item

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import org.strykeforce.thirdcoast.telemetry.grapher.Measure.*
import java.util.function.DoubleSupplier

/** Represents a [TalonSRX] telemetry-enable Item.  */
class TalonItem @JvmOverloads constructor(
    private val talon: TalonSRX,
    override val description: String = "TalonSRX ${talon.deviceID}"
) : Item {

    override val deviceId = talon.deviceID
    override val type = "talon"
    override val measures = setOf(
        CLOSED_LOOP_TARGET,
        OUTPUT_CURRENT,
        OUTPUT_VOLTAGE,
        OUTPUT_PERCENT,
        SELECTED_SENSOR_POSITION,
        SELECTED_SENSOR_VELOCITY,
        ACTIVE_TRAJECTORY_POSITION,
        ACTIVE_TRAJECTORY_VELOCITY,
        CLOSED_LOOP_ERROR,
        BUS_VOLTAGE,
        ERROR_DERIVATIVE,
        INTEGRAL_ACCUMULATOR,
        ANALOG_IN,
        ANALOG_RAW,
        ANALOG_POSITION,
        ANALOG_VELOCITY,
        QUAD_POSITION,
        QUAD_VELOCITY,
        QUAD_A_PIN,
        QUAD_B_PIN,
        QUAD_IDX_PIN,
        PULSE_WIDTH_POSITION,
        PULSE_WIDTH_VELOCITY,
        PULSE_WIDTH_RISE_TO_FALL,
        PULSE_WIDTH_RISE_TO_RISE,
        FORWARD_LIMIT_SWITCH_CLOSED,
        REVERSE_LIMIT_SWITCH_CLOSED
    )

    private val sensorCollection = talon.sensorCollection

    override fun measurementFor(measure: Measure): DoubleSupplier {
        return when (measure) {
            UNKNOWN -> DoubleSupplier { 2767.0 }
            CLOSED_LOOP_TARGET -> DoubleSupplier { talon.getClosedLoopTarget(0).toDouble() }
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
            ANALOG_RAW -> DoubleSupplier { sensorCollection.analogInRaw.toDouble() }
            ANALOG_VELOCITY -> DoubleSupplier { sensorCollection.analogInVel.toDouble() }
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
            else -> TODO("$measure not implemented")
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