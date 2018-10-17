package org.strykeforce.thirdcoast.telemetry.grapher

import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import mu.KotlinLogging
import okio.BufferedSink
import org.strykeforce.thirdcoast.telemetry.Inventory
import java.io.IOException
import java.util.*
import java.util.function.DoubleSupplier

private val logger = KotlinLogging.logger {}

/** Represents a subscription request for streaming data.  */
class Subscription(inventory: Inventory, private val client: String, requestJson: String) {
    private val measurements = ArrayList<DoubleSupplier>(16)
    private val descriptions = ArrayList<String>(16)

    init {
        var request: RequestJson? = RequestJson.EMPTY
        try {
            request = RequestJson.fromJson(requestJson)
        } catch (e: IOException) {
            logger.error("Exception parsing request JSON", e)
        }

        for (jsonItem in request!!.subscription) {
            val item = inventory.itemForId(jsonItem.itemId)
            val measure = Measure.valueOf(jsonItem.measurementId!!)
            measurements.add(item.measurementFor(measure))
            descriptions.add(item.description() + ": " + measure.description)
        }
    }

    fun client(): String {
        return client
    }

    @Throws(IOException::class)
    fun measurementsToJson(sink: BufferedSink) {
        val ts = System.currentTimeMillis()
        val writer = JsonWriter.of(sink)
        writer.beginObject()
        writer.name("timestamp").value(ts)
        writer.name("data")
        writer.beginArray()
        for (m in measurements) {
            writer.value(m.asDouble)
        }
        writer.endArray()
        writer.endObject()
    }

    @Throws(IOException::class)
    fun toJson(sink: BufferedSink) {
        val ts = System.currentTimeMillis()
        val writer = JsonWriter.of(sink)
        writer.beginObject()
        writer.name("type").value("subscription")
        writer.name("timestamp").value(ts)
        writer.name("descriptions")
        writer.beginArray()
        for (d in descriptions) {
            writer.value(d)
        }
        writer.endArray()
        writer.endObject()
    }

    internal class RequestJson {

        var type: String = "start"
        var subscription: List<Item> = emptyList()

        override fun toString(): String {
            return "RequestJson{" + "type='" + type + '\''.toString() + ", subscription=" + subscription + '}'.toString()
        }

        internal class Item {

            var itemId: Int = 0
            var measurementId: String? = null

            override fun toString(): String {
                return "Item{" + "itemId=" + itemId + ", measurementId=" + measurementId + '}'.toString()
            }
        }

        companion object {

            val EMPTY: RequestJson = RequestJson()

            @JvmStatic
            @Throws(IOException::class)
            fun fromJson(json: String): RequestJson? {
                // TODO: verify type=start
                val moshi = Moshi.Builder().build()
                val adapter = moshi.adapter(RequestJson::class.java)
                return adapter.fromJson(json)
            }
        }
    }
}
