package org.strykeforce.thirdcoast.telemetry.item

import com.squareup.moshi.JsonWriter
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import java.io.IOException
import java.util.function.DoubleSupplier

/**
 * An item that can be graphed. These are used as `Set` elements and implementing classes
 * should implement an identity-based version of equals and hashCode.
 *
 *
 * The abstract base class implementing `Item` implements `Comparable` by comparing
 * the results returned by `Item#deviceId()`.
 */
interface Item : Comparable<Item> {

    /**
     * Returns the underlying device id, for example, CAN bus address or PWM port.
     *
     * @return the device id.
     */
    fun deviceId(): Int

    /**
     * A `String` representing the underlying device type.
     *
     * @return the type.
     */
    fun type(): String

    /**
     * The description of this item.
     *
     * @return the description.
     */
    fun description(): String

    /**
     * `Set` of `Measure` items applicable to this item type.
     *
     * @return Set of Measure enumeration values.
     */
    fun measures(): Set<Measure>

    /**
     * Suppliers that implement the measures.
     *
     * @param measure the Measure to supply.
     * @return the supplier that gives the current measurement.
     */
    fun measurementFor(measure: Measure): DoubleSupplier

    /**
     * Provides a detailed JSON representation of the underlying device.
     *
     * @param writer the writer to write to.
     * @throws IOException if an IO error occurs.
     */
    @Throws(IOException::class)
    fun toJson(writer: JsonWriter)
}
