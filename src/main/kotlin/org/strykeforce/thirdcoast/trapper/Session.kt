package org.strykeforce.thirdcoast.trapper

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import java.util.*

val JSON = "application/json; charset=utf-8".toMediaType()

object Session {
    val client = OkHttpClient()

    var baseUrl = "http://example.com:5000"
        set(value) {
            field = value
            activityEndpoint = "$value/activities".toHttpUrl()
            actionEndpoint = "$value/actions".toHttpUrl()
        }
    var activityEndpoint: HttpUrl = "$baseUrl/activities".toHttpUrl()
    var actionEndpoint: HttpUrl = "$baseUrl/actions".toHttpUrl()


    fun post(url: HttpUrl, json: String) {
        val body = json.toRequestBody(JSON)
        val request = Request.Builder().url(url).post(body).build()

        client.newCall(request).execute().use { println("response: ${it.body?.string()}") }
    }

    fun post(url: HttpUrl, body: RequestBody) {
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).execute().use { println("response: ${it.body?.string()}") }
    }

}

private val moshi = Moshi.Builder().add(UUIDAdapter()).add(ActivityAdapter()).build()
private val activityJsonAdapter = ActivityJsonAdapter(moshi)

/** Post an Activity to the web server specified in URL. */
fun Activity.post() {
    val body = object : RequestBody() {
        override fun contentType() = JSON
        override fun writeTo(sink: BufferedSink) = activityJsonAdapter.toJson(sink, this@post)
    }
    Session.post(Session.activityEndpoint, body)
}

private val actionJsonAdapter = ActionJsonAdapter(moshi).serializeNulls()

/** Post an Action to the web server specified in URL. The parent activity must be posted first or this will fail. */
fun Action.post() {
    val body = object : RequestBody() {
        override fun contentType() = JSON
        override fun writeTo(sink: BufferedSink) = actionJsonAdapter.toJson(sink, this@post)
    }
    Session.post(Session.actionEndpoint, body)
}

internal class UUIDAdapter {

    @ToJson
    fun toJson(uuid: UUID): String = uuid.toString()

    @FromJson
    fun fromJson(uuid: String) = UUID.fromString(uuid)
}

internal class ActivityAdapter {
    @ToJson
    fun toJson(activity: Activity): UUID = activity.id

    @FromJson
    fun fromJson(uuid: UUID) = Activity(id = uuid, name = "UNKNOWN")
}