package org.strykeforce.thirdcoast.telemetry.item

import com.ctre.phoenix.CANifier
import com.ctre.phoenix.CANifier.PWMChannel
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import org.strykeforce.thirdcoast.telemetry.grapher.Measure.VALUE
import java.util.*
import java.util.function.DoubleSupplier

class UltrasonicRangefinderItem(canId: Int, private val pwmChannel: PWMChannel) :
  AbstractItem("sensor", "Ultrasonic Rangefinder ($canId, $pwmChannel)", MEASURES) {

  private val deviceId: Int = canId * 10 + pwmChannel.value
  private val canifier: CANifier = CANifier(canId)
  private val dutyCycleAndPeriod = DoubleArray(2)

  override fun deviceId(): Int {
    return deviceId
  }

  override fun measurementFor(measure: Measure): DoubleSupplier {
    if (!MEASURES.contains(measure)) {
      throw IllegalArgumentException("invalid measure: " + measure.name)
    }
    return {
      canifier.getPWMInput(pwmChannel, dutyCycleAndPeriod)
      dutyCycleAndPeriod[0]
    } as DoubleSupplier
  }

  companion object {

    private val MEASURES = Collections.unmodifiableSet(EnumSet.of(VALUE))
  }
}
