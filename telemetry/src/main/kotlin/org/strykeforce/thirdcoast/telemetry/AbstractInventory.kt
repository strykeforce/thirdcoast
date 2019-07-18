package org.strykeforce.thirdcoast.telemetry

import com.squareup.moshi.JsonWriter
import okio.BufferedSink
import org.strykeforce.thirdcoast.telemetry.graphable.Graphable
import java.io.IOException

/**
 * An abstract base class intended to be subclassed by concrete implmentations of [Inventory].
 */
abstract class AbstractInventory(graphables: Collection<Graphable>) : Inventory {

    protected val items = graphables.sorted()

    override fun graphableForId(id: Int) = items[id]

    @Throws(IOException::class)
    override fun writeInventory(sink: BufferedSink) {
        val writer = JsonWriter.of(sink)
        writer.indent = "  "
        writer.beginObject()
            .name("graphables")
            .writeItems(items)
            .name("measures")
            .writeMeasures(items)
            .endObject()
    }

    override fun toString() = "AbstractInventory(graphables=$items)"
}

private fun JsonWriter.writeItems(graphables: List<Graphable>): JsonWriter {
    beginArray()
    graphables.forEachIndexed { index, item ->
        beginObject()
        name("id").value(index)
        name("type").value(item.type)
        name("description").value(item.description)
        endObject()
    }
    return endArray()
}

private fun JsonWriter.writeMeasures(graphables: List<Graphable>): JsonWriter {
    beginArray()
    graphables.associateBy({ it.type }, { it.measures }).forEach { type, measures ->
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

