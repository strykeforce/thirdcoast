package org.strykeforce.thirdcoast.telemetry.item

import edu.wpi.first.wpilibj.Servo
import java.util.function.DoubleSupplier

internal const val POSITION = "POSITION"
internal const val ANGLE = "ANGLE"

/** Represents a [Servo] telemetry-enable `Measurable` item.  */
class ServoItem @JvmOverloads constructor(
  private val servo: Servo,
  override val description: String = "Servo ${servo.channel}"
) : Measurable {

  override val deviceId = servo.channel
  override val type = "servo"
  override val measures = setOf(
    Measure(POSITION, "Servo Position"),
    Measure(ANGLE, "Servo Angle")
  )

  override fun measurementFor(measure: Measure): DoubleSupplier {
    require(measures.contains(measure))

    return when (measure.name) {
      POSITION -> DoubleSupplier { servo.position }
      ANGLE -> DoubleSupplier { servo.angle }
      else -> DoubleSupplier { 2767.0 }
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ServoItem

    if (deviceId != other.deviceId) return false

    return true
  }

  override fun hashCode() = deviceId
}
