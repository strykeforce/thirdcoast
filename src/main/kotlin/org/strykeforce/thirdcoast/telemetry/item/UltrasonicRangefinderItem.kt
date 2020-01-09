package org.strykeforce.thirdcoast.telemetry.item

import com.ctre.phoenix.CANifier
import com.ctre.phoenix.CANifier.PWMChannel
import java.util.function.DoubleSupplier

/** Represents a PWM ultrasonic rangefinder telemetry-enable `Measurable` item connected to a `CANifier`.  */
class UltrasonicRangefinderItem @JvmOverloads constructor(
  canId: Int,
  private val pwmChannel: PWMChannel,
  override val description: String = "Sensor ${canId * 10 + pwmChannel.value}"
) : Measurable {

  override val deviceId = canId * 10 + pwmChannel.value
  override val type = "sensor"
  override val measures = setOf(Measure(VALUE, "PWM Duty Cycle"))


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

    other as UltrasonicRangefinderItem

    if (deviceId != other.deviceId) return false

    return true
  }

  override fun hashCode() = deviceId
}
