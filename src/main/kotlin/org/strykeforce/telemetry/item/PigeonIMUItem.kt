package org.strykeforce.telemetry.item

import com.ctre.phoenix.sensors.PigeonIMU
import edu.wpi.first.wpilibj.Servo
import java.lang.IllegalArgumentException
import java.util.function.DoubleSupplier

internal const val COMPASS_HEADING = "COMPASS_HEADING"
internal const val ABS_COMPASS_HEADING = "ABS_COMPASS_HEADING"
internal const val COMPASS_FIELD_STRENGTH = "COMPASS_FIELD_STRENGTH"
internal const val YAW = "YAW"
internal const val PITCH = "PITCH"
internal const val ROLL = "ROLL"

/** Represents a [Servo] telemetry-enable `Measurable` item.  */
class PigeonIMUItem @JvmOverloads constructor(
    private val pigeon: PigeonIMU,
    override val description: String = "PigeonIMU ${pigeon.deviceID}"
) : Measurable {

    override val deviceId = pigeon.deviceID
    override val type = "pigeon"
    override val measures = setOf(
        Measure(COMPASS_HEADING, "Compass Heading"),
        Measure(ABS_COMPASS_HEADING, "Absolute Compass Heading"),
        Measure(COMPASS_FIELD_STRENGTH, "Compass Field Strength"),
        Measure(YAW, "Yaw"),
        Measure(PITCH, "Pitch"),
        Measure(ROLL, "Roll")
    )

    override fun measurementFor(measure: Measure): DoubleSupplier {
        return when (measure.name) {
            COMPASS_HEADING -> DoubleSupplier { pigeon.compassHeading }
            ABS_COMPASS_HEADING -> DoubleSupplier { pigeon.absoluteCompassHeading }
            COMPASS_FIELD_STRENGTH -> DoubleSupplier { pigeon.compassFieldStrength }
            YAW -> DoubleSupplier {
                val ypr:DoubleArray = doubleArrayOf(0.0, 0.0, 0.0)
                pigeon.getYawPitchRoll(ypr)
                ypr[0]
            }
            PITCH -> DoubleSupplier {
                val ypr:DoubleArray = doubleArrayOf(0.0, 0.0, 0.0)
                pigeon.getYawPitchRoll(ypr)
                ypr[1]
            }
            ROLL -> DoubleSupplier {
                val ypr:DoubleArray = doubleArrayOf(0.0, 0.0, 0.0)
                pigeon.getYawPitchRoll(ypr)
                ypr[2]
            }
            else -> throw IllegalArgumentException(measure.name)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PigeonIMUItem

        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode() = deviceId
}
