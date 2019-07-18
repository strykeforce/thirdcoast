package org.strykeforce.thirdcoast.telemetry.graphable

import com.ctre.phoenix.CANifier
import com.ctre.phoenix.CANifier.PWMChannel
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import org.strykeforce.thirdcoast.telemetry.grapher.Measure.VALUE
import java.util.function.DoubleSupplier

class UltrasonicRangefinderGraphable @JvmOverloads constructor(
    canId: Int,
    private val pwmChannel: PWMChannel,
    override val description: String = "Sensor ${canId * 10 + pwmChannel.value}"
) : Graphable {

    override val deviceId = canId * 10 + pwmChannel.value
    override val type = "sensor"
    override val measures = setOf(VALUE)


    private val canifier: CANifier = CANifier(canId)
    private val dutyCycleAndPeriod = DoubleArray(2)

    override fun measurementFor(measure: Measure): DoubleSupplier {
        require(measures.contains(measure))
        return DoubleSupplier {
            canifier.getPWMInput(pwmChannel, dutyCycleAndPeriod)
            dutyCycleAndPeriod[0]
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UltrasonicRangefinderGraphable

        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId
}
