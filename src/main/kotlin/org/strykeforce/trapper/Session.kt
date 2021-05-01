package org.strykeforce.trapper

import com.squareup.moshi.Moshi
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

val MEDIA_TYPE_JSON: MediaType = "application/json; charset=utf-8".toMediaType()
private val moshi = Moshi.Builder().build()

class Session(var baseUrl: String = "http://localhost:8000") {
    val client = OkHttpClient()

    val activityEndpoint: HttpUrl
        get() = "$baseUrl/activities/".toHttpUrl()

    val actionEndpoint: HttpUrl
        get() = "$baseUrl/actions/".toHttpUrl()

    val traceEndpoint: HttpUrl
        get() = "$baseUrl/traces/".toHttpUrl()

    fun post(activity: Activity): Activity {
        val request = Request.Builder().url(activityEndpoint).post(activity.asRequestBody()).build()
        client.newCall(request).execute().use {
            if (!it.isSuccessful) throw IOException("Unexpected code $it")
            return activityFromJson(it.body!!.source())
                ?: throw IOException("Unable to parse response")
        }
    }

    fun post(action: Action): Action {
        val request = Request.Builder().url(actionEndpoint).post(action.asRequestBody()).build()
        client.newCall(request).execute().use {
            if (!it.isSuccessful) throw IOException("Unexpected code $it")
            return actionFromJson(it.body!!.source())
                ?: throw IOException("Unable to parse response")
        }
    }

    fun post(trace: Trace): Trace {
        val request = Request.Builder().url(traceEndpoint).post(trace.asRequestBody()).build()
        client.newCall(request).execute().use {
            if (!it.isSuccessful) throw IOException("Unexpected code $it")
            return traceFromJson(it.body!!.source())
                ?: throw IOException("Unable to parse response")
        }
    }

    fun post(traces: List<Trace>) {
        val request = Request.Builder().url(traceEndpoint).post(requestBodyFromList(traces)).build()
        client.newCall(request).execute().use {
            if (!it.isSuccessful) throw IOException("Unexpected code $it")
        }

    }
}