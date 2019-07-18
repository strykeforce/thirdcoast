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
class Subscription(inventory: Inventory, val client: String, requestJson: String) {
    private val measurements = ArrayList<DoubleSupplier>(16)
    private val descriptions = ArrayList<String>(16)

    init {
        val request: RequestJson = RequestJson.fromJson(requestJson) ?: RequestJson.EMPTY
        request.subscription.forEach {
            val item = inventory.graphableForId(it.graphableId)
            val measure = try {
                Measure.valueOf(it.measurementId)
            } catch (e: IllegalArgumentException) {
                logger.error { "no such measure \"${it.measurementId}\", request JSON = \n$requestJson" }
                Measure.UNKNOWN
            }
            measurements += item.measurementFor(measure)
            descriptions += "${item.description}: ${measure.description}"
        }
    }

    @Throws(IOException::class)
    fun measurementsToJson(sink: BufferedSink) {
        val writer = JsonWriter.of(sink)
        writer.beginObject()
            .name("timestamp").value(System.currentTimeMillis())
            .name("data").beginArray()
        measurements.forEach { writer.value(it.asDouble) }
        writer.endArray().endObject()
    }

    @Throws(IOException::class)
    fun toJson(sink: BufferedSink) {
        val writer = JsonWriter.of(sink)
        writer.beginObject()
            .name("type").value("subscription")
            .name("timestamp").value(System.currentTimeMillis())
            .name("descriptions").beginArray()
        descriptions.forEach { writer.value(it) }
        writer.endArray()
            .endObject()
    }

    internal class RequestJson {

        var type: String = "start"
        var subscription: List<Graphable> = emptyList()

        internal class Graphable {

            var graphableId: Int = 0
            lateinit var measurementId: String

            override fun toString() = "Graphable(graphableId=$graphableId, measurementId='$measurementId')"
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

        override fun toString() = "RequestJson(type='$type', subscription=$subscription)"
    }
}
