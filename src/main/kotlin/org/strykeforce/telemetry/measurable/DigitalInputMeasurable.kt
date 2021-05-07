package org.strykeforce.telemetry.measurable

import edu.wpi.first.wpilibj.DigitalInput
import java.util.function.DoubleSupplier

internal const val VALUE = "VALUE"

/** Represents a [DigitalInput] telemetry-enable `Measurable` item.  */
class DigitalInputMeasurable @JvmOverloads constructor(
    private val digitalInput: DigitalInput,
    override val description: String = "Digital Input ${digitalInput.channel}"
) : Measurable {

    override val deviceId = digitalInput.channel
    override val measures =
        setOf(Measure(VALUE, "Digital Input Value") { digitalInput.get().toDouble() })

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DigitalInputMeasurable

        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId

}
