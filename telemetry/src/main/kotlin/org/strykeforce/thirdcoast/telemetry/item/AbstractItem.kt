package org.strykeforce.thirdcoast.telemetry.item

import com.squareup.moshi.JsonWriter
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import java.io.IOException

/**
 * Abstract base class for Items. This implements `Comparable` by comparing the results
 * returned by `Item#deviceId()`.
 * @property type the type of this item.
 * @property description the description of this item.
 * @property measures the measures that are available for this type of item.
 */
abstract class AbstractItem(val type: String, val description: String, val measures: Set<Measure>) : Item {

    override fun type(): String { // FIXME: these need to be properties in interface
        return type
    }

    override fun description(): String {
        return description
    }

    override fun measures(): Set<Measure> {
        return measures
    }

    override fun compareTo(other: Item): Int {
        val result = type.compareTo(other.type())
        return if (result != 0) result else Integer.compare(deviceId(), other.deviceId())
    }

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter) {
        writer.beginObject()
        writer.name("not").value("implemented")
        writer.endObject()
    }

    override fun toString(): String {
        return ("AbstractItem{"
                + "type='"
                + type
                + '\''.toString()
                + ", description='"
                + description
                + '\''.toString()
                + ", measures="
                + measures
                + '}'.toString())
    }
}
