package org.strykeforce.telemetry.measurable

import edu.wpi.first.wpilibj.Servo

internal const val POSITION = "POSITION"
internal const val ANGLE = "ANGLE"

/** Represents a [Servo] telemetry-enable `Measurable` item.  */
class ServoMeasurable @JvmOverloads constructor(
    private val servo: Servo,
    override val description: String = "Servo ${servo.channel}"
) : Measurable {

    override val deviceId = servo.channel
    override val measures = setOf(
        Measure(POSITION, "Servo Position") { servo.position },
        Measure(ANGLE, "Servo Angle") { servo.angle }
    )


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServoMeasurable

        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId
}
