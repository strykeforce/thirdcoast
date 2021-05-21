package org.strykeforce.trapper

import com.squareup.moshi.Moshi
import mu.KotlinLogging
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okio.IOException
import java.util.function.Consumer

val MEDIA_TYPE_JSON: MediaType = "application/json; charset=utf-8".toMediaType()
val moshi = Moshi.Builder().build()
private val logger = KotlinLogging.logger {}

class Session(var baseUrl: String = "http://localhost:8000") {
    val client = OkHttpClient()

    val traceEndpoint: HttpUrl
        get() = "$baseUrl/traces/".toHttpUrl()

    fun <T : Postable> post(postable: T): T {
        val request =
            Request.Builder().url(postable.endpoint(baseUrl)).post(postable.asRequestBody()).build()
        client.newCall(request).execute().use {
            if (!it.isSuccessful) throw IOException("http response: $it")
            return postable.fromJson(it.body!!.source())
        }
    }

    fun <T : Postable> postAsync(postable: T, postableConsumer: Consumer<T>) {
        val request =
            Request.Builder().url(postable.endpoint(baseUrl)).post(postable.asRequestBody()).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                logger.error(e) { "error posting $postable" }
            }

            override fun onResponse(call: Call, response: Response) {
                postableConsumer.accept(postable.fromJson(response.body!!.source()))
            }
        })
    }

    fun post(traces: List<Trace>) {
        val request = Request.Builder().url(traceEndpoint).post(requestBodyFromList(traces)).build()
        client.newCall(request).execute().use {
            if (!it.isSuccessful) throw IOException("http response: $it")
        }

    }
}