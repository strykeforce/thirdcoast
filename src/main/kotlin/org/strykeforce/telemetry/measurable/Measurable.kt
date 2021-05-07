package org.strykeforce.telemetry.measurable

import java.util.function.DoubleSupplier

/**
 * An item that can be measured and graphed. These are used as `Set` elements and implementing classes
 * should implement an identity-based version of equals and hashCode.
 *
 *
 * The abstract base class implementing `Measurable` implements `Comparable` by comparing
 * the results returned by `Measurable#deviceId()`.
 */
interface Measurable : Comparable<Measurable> {

  /**
   * Returns the underlying device id, for example, CAN bus address or PWM port.
   */
  val deviceId: Int

  /**
   * A `String` representing the underlying device type.
   */
  val type: String
    get() = javaClass.typeName

  /**
   * The description of this item.
   */
  val description: String

  /**
   * `Set` of `Measure` parameters applicable to this item type.
   */
  val measures: Set<Measure>

  override fun compareTo(other: Measurable): Int {
    val result = type.compareTo(other.type)
    return if (result != 0) result else deviceId.compareTo(other.deviceId)
  }

}

internal fun Boolean.toDouble() = if (this) 1.0 else 0.0
