package org.strykeforce.trapper

import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import okio.BufferedSource

private val moshi = Moshi.Builder().build()
private val traceJsonAdapter = TraceJsonAdapter(moshi)

@JsonClass(generateAdapter = true)
data class Trace(val time: Int) {
    var id: Int? = null
    var action: Int? = null
    var data: List<Double> = mutableListOf()

    fun asRequestBody(): RequestBody {
        val buffer = Buffer()
        traceJsonAdapter.toJson(buffer, this)
        return buffer.readUtf8().toRequestBody(MEDIA_TYPE_JSON)
    }
}

internal fun requestBodyFromList(traces: List<Trace>): RequestBody {
    val buffer = Buffer()
    val writer: JsonWriter = JsonWriter.of(buffer)
    writer.beginArray()
    traces.forEach { traceJsonAdapter.toJson(writer, it) }
    writer.endArray()
    return buffer.readUtf8().toRequestBody(MEDIA_TYPE_JSON)
}

internal fun traceFromJson(source: BufferedSource) = traceJsonAdapter.fromJson(source)
