package org.strykeforce.thirdcoast.telemetry.item

import com.ctre.phoenix.CANifier
import com.ctre.phoenix.CANifier.PWMChannel.*
import java.util.function.DoubleSupplier

internal const val PWM0_PULSE_WIDTH = "PWM0_PULSE_WIDTH"
internal const val PWM0_PERIOD = "PWM0_PERIOD"
internal const val PWM0_PULSE_WIDTH_POSITION = "PWM0_PULSE_WIDTH_POSITION"
internal const val PWM1_PULSE_WIDTH = "PWM1_PULSE_WIDTH"
internal const val PWM1_PERIOD = "PWM1_PERIOD"
internal const val PWM1_PULSE_WIDTH_POSITION = "PWM1_PULSE_WIDTH_POSITION"
internal const val PWM2_PULSE_WIDTH = "PWM2_PULSE_WIDTH"
internal const val PWM2_PERIOD = "PWM2_PERIOD"
internal const val PWM2_PULSE_WIDTH_POSITION = "PWM2_PULSE_WIDTH_POSITION"
internal const val PWM3_PULSE_WIDTH = "PWM3_PULSE_WIDTH"
internal const val PWM3_PERIOD = "PWM3_PERIOD"
internal const val PWM3_PULSE_WIDTH_POSITION = "PWM3_PULSE_WIDTH_POSITION"
internal const val QUAD_POSITION = "QUAD_POSITION"
internal const val QUAD_VELOCITY = "QUAD_VELOCITY"

/** Represents a `CANifier` telemetry-enable `Measurable` item.  */
class CanifierItem @JvmOverloads constructor(
  private val canifier: CANifier,
  override val description: String = "CANifier ${canifier.deviceID}"
) : Measurable {

  override val deviceId = canifier.deviceID
  override val type = "canifier"
  override val measures = setOf(
    Measure(PWM0_PULSE_WIDTH, "PWM 0 Pulse Width"),
    Measure(PWM0_PERIOD, "PWM 0 Period"),
    Measure(PWM0_PULSE_WIDTH_POSITION, "PWM 0 Pulse Width Position"),
    Measure(PWM1_PULSE_WIDTH, "PWM 1 Pulse Width"),
    Measure(PWM1_PERIOD, "PWM 1 Period"),
    Measure(PWM1_PULSE_WIDTH_POSITION, "PWM 1 Pulse Width Position"),
    Measure(PWM2_PULSE_WIDTH, "PWM 2 Pulse Width"),
    Measure(PWM2_PERIOD, "PWM 2 Period"),
    Measure(PWM2_PULSE_WIDTH_POSITION, "PWM 2 Pulse Width Position"),
    Measure(PWM3_PULSE_WIDTH, "PWM 3 Pulse Width"),
    Measure(PWM3_PERIOD, "PWM 3 Period"),
    Measure(PWM3_PULSE_WIDTH_POSITION, "PWM 3 Pulse Width Position"),
    Measure(QUAD_POSITION, "Quadrature Position"),
    Measure(QUAD_VELOCITY, "Quadrature Velocity")
  )

  override fun measurementFor(measure: Measure): DoubleSupplier {

    return when (measure.name) {
      PWM0_PULSE_WIDTH -> DoubleSupplier { pulseWidthFor(PWMChannel0) }
      PWM0_PERIOD -> DoubleSupplier { periodFor(PWMChannel0) }
      PWM0_PULSE_WIDTH_POSITION -> DoubleSupplier { pulseWidthPositionFor(PWMChannel0) }
      PWM1_PULSE_WIDTH -> DoubleSupplier { pulseWidthFor(PWMChannel1) }
      PWM1_PERIOD -> DoubleSupplier { periodFor(PWMChannel1) }
      PWM1_PULSE_WIDTH_POSITION -> DoubleSupplier { pulseWidthPositionFor(PWMChannel1) }
      PWM2_PULSE_WIDTH -> DoubleSupplier { pulseWidthFor(PWMChannel2) }
      PWM2_PERIOD -> DoubleSupplier { periodFor(PWMChannel2) }
      PWM2_PULSE_WIDTH_POSITION -> DoubleSupplier { pulseWidthPositionFor(PWMChannel2) }
      PWM3_PULSE_WIDTH -> DoubleSupplier { pulseWidthFor(PWMChannel3) }
      PWM3_PERIOD -> DoubleSupplier { periodFor(PWMChannel3) }
      PWM3_PULSE_WIDTH_POSITION -> DoubleSupplier { pulseWidthPositionFor(PWMChannel3) }
      QUAD_POSITION -> DoubleSupplier { canifier.quadraturePosition.toDouble() }
      QUAD_VELOCITY -> DoubleSupplier { canifier.quadratureVelocity.toDouble() }

      else -> DoubleSupplier { 2767.0 }
    }
  }

  private fun pulseWidthFor(channel: CANifier.PWMChannel): Double {
    val pulseWidthAndPeriod = DoubleArray(2)
    canifier.getPWMInput(channel, pulseWidthAndPeriod)
    return pulseWidthAndPeriod[0]
  }

  private fun periodFor(channel: CANifier.PWMChannel): Double {
    val pulseWidthAndPeriod = DoubleArray(2)
    canifier.getPWMInput(channel, pulseWidthAndPeriod)
    return pulseWidthAndPeriod[1]
  }

  private fun pulseWidthPositionFor(channel: CANifier.PWMChannel): Double {
    val pulseWidthAndPeriod = DoubleArray(2)
    canifier.getPWMInput(channel, pulseWidthAndPeriod)
    return 4096.0 * pulseWidthAndPeriod[0] / pulseWidthAndPeriod[1]
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as CanifierItem

    if (deviceId != other.deviceId) return false

    return true
  }

  override fun hashCode() = deviceId

}
