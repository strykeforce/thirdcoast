package org.strykeforce.telemetry.measurable

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
class CanifierMeasurable @JvmOverloads constructor(
  private val canifier: CANifier,
  override val description: String = "CANifier ${canifier.deviceID}"
) : Measurable {

  override val deviceId = canifier.deviceID
  override val measures = setOf(
    Measure(PWM0_PULSE_WIDTH, "PWM 0 Pulse Width"){ pulseWidthFor(PWMChannel0) },
    Measure(PWM0_PERIOD, "PWM 0 Period"){ periodFor(PWMChannel0) },
    Measure(PWM0_PULSE_WIDTH_POSITION, "PWM 0 Pulse Width Position"){ pulseWidthPositionFor(PWMChannel0) },
    Measure(PWM1_PULSE_WIDTH, "PWM 1 Pulse Width"){ pulseWidthFor(PWMChannel1) },
    Measure(PWM1_PERIOD, "PWM 1 Period"){ periodFor(PWMChannel1) },
    Measure(PWM1_PULSE_WIDTH_POSITION, "PWM 1 Pulse Width Position"){ pulseWidthPositionFor(PWMChannel1) },
    Measure(PWM2_PULSE_WIDTH, "PWM 2 Pulse Width"){ pulseWidthFor(PWMChannel2) },
    Measure(PWM2_PERIOD, "PWM 2 Period"){ periodFor(PWMChannel2) },
    Measure(PWM2_PULSE_WIDTH_POSITION, "PWM 2 Pulse Width Position"){ pulseWidthPositionFor(PWMChannel2) },
    Measure(PWM3_PULSE_WIDTH, "PWM 3 Pulse Width"){ pulseWidthFor(PWMChannel3) },
    Measure(PWM3_PERIOD, "PWM 3 Period"){ periodFor(PWMChannel3) },
    Measure(PWM3_PULSE_WIDTH_POSITION, "PWM 3 Pulse Width Position"){ pulseWidthPositionFor(PWMChannel3) },
    Measure(QUAD_POSITION, "Quadrature Position"){ canifier.quadraturePosition.toDouble() },
    Measure(QUAD_VELOCITY, "Quadrature Velocity"){ canifier.quadratureVelocity.toDouble() }
  )

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

    other as CanifierMeasurable

    if (deviceId != other.deviceId) return false

    return true
  }

  override fun hashCode() = deviceId

}
