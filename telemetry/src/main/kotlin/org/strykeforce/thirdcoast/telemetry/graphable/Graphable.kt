package org.strykeforce.thirdcoast.telemetry.graphable

import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import java.util.function.DoubleSupplier

/**
 * An graphable that can be graphed. These are used as `Set` elements and implementing classes
 * should implement an identity-based version of equals and hashCode.
 *
 *
 * The abstract base class implementing `Graphable` implements `Comparable` by comparing
 * the results returned by `Graphable#deviceId()`.
 */
interface Graphable : Comparable<Graphable> {

    /**
     * Returns the underlying device id, for example, CAN bus address or PWM port.
     */
    val deviceId: Int

    /**
     * A `String` representing the underlying device type.
     */
    val type: String

    /**
     * The description of this graphable.
     */
    val description: String

    /**
     * `Set` of `Measure` items applicable to this graphable type.
     */
    val measures: Set<Measure>

    /**
     * Suppliers that implement the measures.
     * @return the supplier that gives the current measurement.
     */
    fun measurementFor(measure: Measure): DoubleSupplier

    override fun compareTo(other: Graphable): Int {
        val result = type.compareTo(other.type)
        return if (result != 0) result else deviceId.compareTo(other.deviceId)
    }

}

internal fun Boolean.toDouble() = if (this) 1.0 else 0.0