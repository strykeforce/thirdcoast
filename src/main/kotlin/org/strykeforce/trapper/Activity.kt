package org.strykeforce.trapper

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import okio.BufferedSource
import java.time.LocalDateTime

private val moshi = Moshi.Builder().build()
private val activityJsonAdapter = ActivityJsonAdapter(moshi)

@JsonClass(generateAdapter = true)
data class Activity(
    val name: String = "Activity ${LocalDateTime.now()}",
) {
    var id: Int? = null
    var url: String? = null
    var meta: MutableMap<String, Any> = mutableMapOf()

    fun asRequestBody(): RequestBody {
        val buffer = Buffer()
        activityJsonAdapter.toJson(buffer, this)
        return buffer.readUtf8().toRequestBody(MEDIA_TYPE_JSON)
    }
}

internal fun activityFromJson(source: BufferedSource) = activityJsonAdapter.fromJson(source)