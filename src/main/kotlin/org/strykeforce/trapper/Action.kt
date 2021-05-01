package org.strykeforce.trapper

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import okio.BufferedSource
import java.time.LocalDateTime

private val moshi = Moshi.Builder().build()
private val actionJsonAdapter = ActionJsonAdapter(moshi)

@JsonClass(generateAdapter = true)
data class Action(
    val name: String = "Action ${LocalDateTime.now()}"
) {
    var id: Int? = null
    var url: String? = null
    var activity: String? = null
    var meta: MutableMap<String, Any> = mutableMapOf()
    var measures: List<String> = mutableListOf()

    fun asRequestBody(): RequestBody {
        val buffer = Buffer()
        actionJsonAdapter.toJson(buffer, this)
        return buffer.readUtf8().toRequestBody(MEDIA_TYPE_JSON)
    }
}

internal fun actionFromJson(source: BufferedSource) = actionJsonAdapter.fromJson(source)