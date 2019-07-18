package org.strykeforce.thirdcoast.telemetry.graphable

import edu.wpi.first.wpilibj.Servo
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import org.strykeforce.thirdcoast.telemetry.grapher.Measure.ANGLE
import org.strykeforce.thirdcoast.telemetry.grapher.Measure.POSITION
import java.util.function.DoubleSupplier

/** Represents a [Servo] telemetry-enable Graphable.  */
class ServoGraphable @JvmOverloads constructor(
    private val servo: Servo,
    override val description: String = "Servo ${servo.channel}"
) : Graphable {

    override val deviceId = servo.channel
    override val type = "servo"
    override val measures = setOf(POSITION, ANGLE)

    override fun measurementFor(measure: Measure): DoubleSupplier {
        require(measures.contains(measure))

        return when (measure) {
            POSITION -> DoubleSupplier { servo.position }
            ANGLE -> DoubleSupplier { servo.angle }
            else -> TODO("$measure not implemented")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServoGraphable

        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId
}
