package org.strykeforce.thirdcoast.telemetry

import com.squareup.moshi.JsonWriter
import okio.BufferedSink
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import org.strykeforce.thirdcoast.telemetry.item.Item
import java.io.IOException
import java.util.*

/**
 * An abstract base class intended to be subclassed by concrete implmentations of [Inventory].
 */
abstract class AbstractInventory(items: Collection<Item>) : Inventory {

    protected val items = items.toMutableList().also { it.sort() }

    override fun itemForId(id: Int): Item {
        return items[id]
    }

    @Throws(IOException::class)
    override fun writeInventory(sink: BufferedSink) {
        val writer = JsonWriter.of(sink)
        writer.indent = "  "
        writer.beginObject()
        writer.name("items")
        writeItems(writer)
        writer.name("measures")
        writeMeasures(writer)
        writer.endObject()
    }

    @Throws(IOException::class)
    internal fun writeItems(writer: JsonWriter) {
        writer.beginArray()
        for (i in items.indices) {
            writer.beginObject()
            writer.name("id").value(i.toLong())
            writer.name("type").value(items[i].type())
            writer.name("description").value(items[i].description())
            writer.endObject()
        }
        writer.endArray()
    }

    @Throws(IOException::class)
    internal fun writeMeasures(writer: JsonWriter) {
        val measures = HashMap<String, Set<Measure>>()
        items.forEach { it -> (measures.putIfAbsent(it.type(), it.measures())) }
        writer.beginArray()
        for ((key, value) in measures) {
            writeDeviceMeasures(writer, key, value)
        }
        writer.endArray()
    }

    @Throws(IOException::class)
    internal fun writeDeviceMeasures(writer: JsonWriter, type: String, measures: Set<Measure>) {
        writer.beginObject()
        writer.name("deviceType").value(type)
        writer.name("deviceMeasures")
        writer.beginArray()
        for (m in measures) {
            writeMeasure(writer, m)
        }
        writer.endArray()
        writer.endObject()
    }

    @Throws(IOException::class)
    internal fun writeMeasure(writer: JsonWriter, measure: Measure) {
        writer.beginObject()
        writer.name("id").value(measure.name)
        writer.name("description").value(measure.description)
        writer.endObject()
    }

    @Throws(IOException::class)
    override fun toJson(sink: BufferedSink) {
        val writer = JsonWriter.of(sink)
        writer.indent = "  "
        writer.beginArray()
        for (item in items) {
            item.toJson(writer)
        }
        writer.endArray()
    }

    override fun toString(): String {
        return "AbstractInventory{" + "items=" + items + '}'.toString()
    }
}
