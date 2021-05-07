package org.strykeforce.telemetry.measurable

import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.DigitalOutput


/** Represents a [DigitalInput] telemetry-enable `Measurable` item.  */
class DigitalOutputMeasurable @JvmOverloads constructor(
    private val digitalOutput: DigitalOutput,
    override val description: String = "Digital Output ${digitalOutput.channel}"
) : Measurable {

    override val deviceId = digitalOutput.channel
    override val measures =
        setOf(Measure(VALUE, "Digital Output Value") { digitalOutput.get().toDouble() })

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DigitalOutputMeasurable

        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId
}
