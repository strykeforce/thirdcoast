package org.strykeforce.telemetry.measurable

import com.ctre.phoenix.CANifier
import com.ctre.phoenix.CANifier.PWMChannel

/** Represents a PWM ultrasonic rangefinder telemetry-enable `Measurable` item connected to a `CANifier`.  */
class UltrasonicRangefinderMeasurable @JvmOverloads constructor(
    canId: Int,
    private val pwmChannel: PWMChannel,
    override val description: String = "Sensor ${canId * 10 + pwmChannel.value}"
) : Measurable {

    override val deviceId = canId * 10 + pwmChannel.value
    override val measures = setOf(Measure(VALUE, "PWM Duty Cycle") {
        canifier.getPWMInput(pwmChannel, dutyCycleAndPeriod)
        dutyCycleAndPeriod[0]
    })


    private val canifier: CANifier = CANifier(canId)
    private val dutyCycleAndPeriod = DoubleArray(2)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UltrasonicRangefinderMeasurable

        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId
}
