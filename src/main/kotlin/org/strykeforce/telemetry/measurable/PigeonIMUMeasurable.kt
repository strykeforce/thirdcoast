package org.strykeforce.telemetry.measurable

import com.ctre.phoenix.sensors.PigeonIMU
import edu.wpi.first.wpilibj.Servo

internal const val COMPASS_HEADING = "COMPASS_HEADING"
internal const val ABS_COMPASS_HEADING = "ABS_COMPASS_HEADING"
internal const val COMPASS_FIELD_STRENGTH = "COMPASS_FIELD_STRENGTH"
internal const val YAW = "YAW"
internal const val PITCH = "PITCH"
internal const val ROLL = "ROLL"

/** Represents a [Servo] telemetry-enable `Measurable` item.  */
class PigeonIMUMeasurable @JvmOverloads constructor(
    private val pigeon: PigeonIMU,
    override val description: String = "PigeonIMU ${pigeon.deviceID}"
) : Measurable {

    override val deviceId = pigeon.deviceID
    override val measures = setOf(
        Measure(COMPASS_HEADING, "Compass Heading") { pigeon.compassHeading },
        Measure(ABS_COMPASS_HEADING, "Absolute Compass Heading") { pigeon.absoluteCompassHeading },
        Measure(COMPASS_FIELD_STRENGTH, "Compass Field Strength") { pigeon.compassFieldStrength },
        Measure(YAW, "Yaw") {
            val ypr: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0)
            pigeon.getYawPitchRoll(ypr)
            ypr[0]
        },
        Measure(PITCH, "Pitch") {
            val ypr: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0)
            pigeon.getYawPitchRoll(ypr)
            ypr[1]
        },
        Measure(ROLL, "Roll") {
            val ypr: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0)
            pigeon.getYawPitchRoll(ypr)
            ypr[2]
        }
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PigeonIMUMeasurable

        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId
}
