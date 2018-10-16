package org.strykeforce.thirdcoast.telemetry.item

import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.DigitalOutput
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import java.util.*
import java.util.function.DoubleSupplier

private const val TYPE = "digitalOutput"

/** Represents a [DigitalInput] telemetry-enable Item.  */
class DigitalOutputItem @JvmOverloads constructor(
  private val digitalOutput: DigitalOutput,
  description: String = "Digital Output " + digitalOutput.channel
) : AbstractItem(TYPE, description, MEASURES) {

  companion object {
    val MEASURES: Set<Measure> = Collections.unmodifiableSet(EnumSet.of(Measure.VALUE))
  }

  override fun deviceId(): Int {
    return digitalOutput.channel
  }

  override fun measurementFor(measure: Measure): DoubleSupplier {
    if (!MEASURES.contains(measure)) {
      throw IllegalArgumentException("invalid measure: " + measure.name)
    }
    return { if (digitalOutput.get()) 1.0 else 0.0 } as DoubleSupplier  // FIXME: change to () -> Double
  }

  /**
   * Indicates if some other `DigitalOutputItem` has the same underlying `DigitalOutput`
   * as this one.
   *
   * @param other the reference object with which to compare.
   * @return true if this DigitalOutput has the same channel ID, false otherwise.
   */
  override fun equals(other: Any?): Boolean {
    if (other === this) {
      return true
    }
    if (other !is DigitalOutputItem) {
      return false
    }
    val item = other as DigitalOutputItem?
    return item!!.digitalOutput.channel == digitalOutput.channel
  }

  /**
   * Returns a hashcode value for this DigitalOutputItem.
   *
   * @return a hashcode value for this DigitalOutputItem.
   */
  override fun hashCode(): Int {
    return digitalOutput.channel
  }

  override fun toString(): String {
    return "DigitalInputItem{" + "digitalOutput=" + digitalOutput + "} " + super.toString()
  }

}
