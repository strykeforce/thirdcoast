package org.strykeforce.thirdcoast.telemetry.item

import edu.wpi.first.wpilibj.DigitalInput
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import java.util.*
import java.util.function.DoubleSupplier

private const val TYPE = "digitalInput"
private val MEASURES = Collections.unmodifiableSet(EnumSet.of(Measure.VALUE))

/** Represents a [DigitalInput] telemetry-enable Item.  */
class DigitalInputItem @JvmOverloads constructor(
  private val digitalInput: DigitalInput,
  description: String = "Digital Input " + digitalInput.channel
) : AbstractItem(TYPE, description, MEASURES) {

  override fun deviceId(): Int {
    return digitalInput.channel
  }

  override fun measurementFor(measure: Measure): DoubleSupplier {
    if (!MEASURES.contains(measure)) {
      throw IllegalArgumentException("invalid measure: " + measure.name)
    }
    return { if (digitalInput.get()) 1.0 else 0.0 } as DoubleSupplier // FIXME: change to () -> Double
  }

  /**
   * Indicates if some other `DigitalInputItem` has the same underlying `DigitalInput`
   * as this one.
   *
   * @param other the reference object with which to compare.
   * @return true if this DigitalInput has the same channel ID, false otherwise.
   */
  override fun equals(other: Any?): Boolean {
    if (other === this) {
      return true
    }
    if (other !is DigitalInputItem) {
      return false
    }
    val item = other as DigitalInputItem?
    return item!!.digitalInput.channel == digitalInput.channel
  }

  /**
   * Returns a hashcode value for this DigitalInputItem.
   *
   * @return a hashcode value for this DigitalInputItem.
   */
  override fun hashCode(): Int {
    return digitalInput.channel
  }

  override fun toString(): String {
    return "DigitalInputItem{" + "digitalInput=" + digitalInput + "} " + super.toString()
  }

}
