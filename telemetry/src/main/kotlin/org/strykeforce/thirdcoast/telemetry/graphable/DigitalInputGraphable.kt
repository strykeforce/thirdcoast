package org.strykeforce.thirdcoast.telemetry.graphable

import edu.wpi.first.wpilibj.DigitalInput
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import org.strykeforce.thirdcoast.telemetry.grapher.Measure.VALUE
import java.util.function.DoubleSupplier


/** Represents a [DigitalInput] telemetry-enable Graphable.  */
class DigitalInputGraphable @JvmOverloads constructor(
    private val digitalInput: DigitalInput,
    override val description: String = "Digital Input ${digitalInput.channel}"
) : Graphable {

    override val deviceId = digitalInput.channel
    override val type = "digitalInput"
    override val measures = setOf(VALUE)

    override fun measurementFor(measure: Measure): DoubleSupplier {
        require(measures.contains(measure))
        return DoubleSupplier { digitalInput.get().toDouble() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DigitalInputGraphable

        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId

}
