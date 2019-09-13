package org.strykeforce.thirdcoast.telemetry.item

import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.DigitalOutput
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import org.strykeforce.thirdcoast.telemetry.grapher.Measure.VALUE
import java.util.function.DoubleSupplier


/** Represents a [DigitalInput] telemetry-enable `Measurable` item.  */
class DigitalOutputItem @JvmOverloads constructor(
    private val digitalOutput: DigitalOutput,
    override val description: String = "Digital Output ${digitalOutput.channel}"
) : Measurable {

    override val deviceId = digitalOutput.channel
    override val type = "digitalOutput"
    override val measures = setOf(VALUE)

    override fun measurementFor(measure: Measure): DoubleSupplier {
        require(measures.contains(measure))
        return DoubleSupplier { digitalOutput.get().toDouble() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DigitalOutputItem

        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId
}
