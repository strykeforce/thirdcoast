package org.strykeforce.thirdcoast.telemetry

import com.squareup.moshi.JsonWriter
import okio.BufferedSink
import org.strykeforce.thirdcoast.telemetry.item.Measurable
import java.io.IOException

/**
 * An abstract base class intended to be subclassed by concrete implementations of [Inventory].
 */
abstract class AbstractInventory(items: Collection<Measurable>) : Inventory {

    protected val items = items.sorted()

    override fun itemForId(id: Int) = items[id]

    @Throws(IOException::class)
    override fun writeInventory(sink: BufferedSink) {
        val writer = JsonWriter.of(sink)
        writer.indent = "  "
        writer.beginObject()
            .name("items")
            .writeItems(items)
            .name("measures")
            .writeMeasures(items)
            .endObject()
    }

    override fun toString() = "AbstractInventory(items=$items)"
}

private fun JsonWriter.writeItems(items: List<Measurable>): JsonWriter {
    beginArray()
    items.forEachIndexed { index, item ->
        beginObject()
        name("id").value(index)
        name("type").value(item.type)
        name("description").value(item.description)
        endObject()
    }
    return endArray()
}

private fun JsonWriter.writeMeasures(items: List<Measurable>): JsonWriter {
    beginArray()
    items.associateBy({ it.type }, { it.measures }).forEach { (type, measures) ->
        beginObject()
        name("deviceType").value(type)
        name("deviceMeasures")
        beginArray()
        measures.forEach {
            beginObject()
            name("id").value(it.name)
            name("description").value(it.description)
            endObject()
        }
        endArray()
        endObject()
    }
    return endArray()
}

