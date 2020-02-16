package org.strykeforce.thirdcoast.telemetry.grapher

import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import mu.KotlinLogging
import okio.BufferedSink
import org.strykeforce.thirdcoast.telemetry.Inventory
import org.strykeforce.thirdcoast.telemetry.item.Measure
import java.io.IOException
import java.util.*
import java.util.function.DoubleSupplier

private val logger = KotlinLogging.logger {}

/** Represents a subscription request for streaming data.  */
class Subscription(inventory: Inventory, val client: String, requestJson: String) {
    private val measurements = ArrayList<DoubleSupplier>(16)
    private val descriptions = ArrayList<String>(16)
    private val moshi: Moshi by lazy { Moshi.Builder().build() }

    init {
        val request = Subscription_RequestJsonJsonAdapter(moshi).fromJson(requestJson)
        request?.subscription?.forEach {
            val item = inventory.itemForId(it.itemId)
            val measure = Measure(it.measurementId, it.measurementId)
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

    @JsonClass(generateAdapter = true)
    internal data class Item(val itemId: Int, val measurementId: String)

    @JsonClass(generateAdapter = true)
    internal data class RequestJson(val type:String, val subscription:List<Item>)

}
