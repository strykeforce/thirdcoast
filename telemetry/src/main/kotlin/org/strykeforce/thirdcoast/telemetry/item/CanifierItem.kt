package org.strykeforce.thirdcoast.telemetry.item

import com.ctre.phoenix.CANifier
import com.ctre.phoenix.CANifier.PWMChannel.*
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import org.strykeforce.thirdcoast.telemetry.grapher.Measure.*
import java.util.function.DoubleSupplier

/** Represents a [TalonSRX] telemetry-enable `Measurable` item.  */
class CanifierItem @JvmOverloads constructor(
    private val canifier: CANifier,
    override val description: String = "CANifier ${canifier.deviceID}"
) : Measurable {

    override val deviceId = canifier.deviceID
    override val type = "canifier"
    override val measures
        get() = MEASURES

    override fun measurementFor(measure: Measure): DoubleSupplier {

        return when (measure) {
            UNKNOWN -> DoubleSupplier { 2767.0 }
            PWM0_PULSE_WIDTH -> DoubleSupplier { pulseWidthFor(PWMChannel0) }
            PWM0_PERIOD -> DoubleSupplier { periodFor(PWMChannel0) }
            PWM0_PULSE_WIDTH_POSITION -> DoubleSupplier { pulseWidthPositionFor(PWMChannel0) }
            PWM1_PULSE_WIDTH -> DoubleSupplier { pulseWidthFor(PWMChannel1) }
            PWM1_PERIOD -> DoubleSupplier { periodFor(PWMChannel1) }
            PWM1_PULSE_WIDTH_POSITION -> DoubleSupplier { pulseWidthPositionFor(PWMChannel1) }
            PWM2_PULSE_WIDTH -> DoubleSupplier { pulseWidthFor(PWMChannel2) }
            PWM2_PERIOD -> DoubleSupplier { periodFor(PWMChannel2) }
            PWM2_PULSE_WIDTH_POSITION -> DoubleSupplier { pulseWidthPositionFor(PWMChannel2) }
            PWM3_PULSE_WIDTH -> DoubleSupplier { pulseWidthFor(PWMChannel3) }
            PWM3_PERIOD -> DoubleSupplier { periodFor(PWMChannel3) }
            PWM3_PULSE_WIDTH_POSITION -> DoubleSupplier { pulseWidthPositionFor(PWMChannel3) }
            QUAD_POSITION -> DoubleSupplier { canifier.quadraturePosition.toDouble() }
            QUAD_VELOCITY -> DoubleSupplier { canifier.quadratureVelocity.toDouble() }

            else -> TODO("$measure not implemented")
        }
    }

    private fun pulseWidthFor(channel: CANifier.PWMChannel): Double {
        val pulseWidthAndPeriod = DoubleArray(2)
        canifier.getPWMInput(channel, pulseWidthAndPeriod)
        return pulseWidthAndPeriod[0]
    }

    private fun periodFor(channel: CANifier.PWMChannel): Double {
        val pulseWidthAndPeriod = DoubleArray(2)
        canifier.getPWMInput(channel, pulseWidthAndPeriod)
        return pulseWidthAndPeriod[1]
    }

    private fun pulseWidthPositionFor(channel: CANifier.PWMChannel): Double {
        val pulseWidthAndPeriod = DoubleArray(2)
        canifier.getPWMInput(channel, pulseWidthAndPeriod)
        return 4096.0 * pulseWidthAndPeriod[0] / pulseWidthAndPeriod[1]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CanifierItem

        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId

    companion object {
        val MEASURES = setOf(
            PWM0_PULSE_WIDTH_POSITION,
            QUAD_POSITION,
            QUAD_VELOCITY,
            PWM0_PULSE_WIDTH,
            PWM0_PERIOD,
            PWM1_PULSE_WIDTH,
            PWM1_PERIOD,
            PWM2_PULSE_WIDTH,
            PWM2_PERIOD,
            PWM3_PULSE_WIDTH,
            PWM3_PERIOD
        )
    }
}
