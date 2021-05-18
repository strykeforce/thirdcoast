package org.strykeforce.trapper

import okhttp3.HttpUrl
import okhttp3.RequestBody
import okio.BufferedSource

interface Postable {

    fun endpoint(baseUrl: String): HttpUrl

    fun asRequestBody(): RequestBody

    fun <T : Postable> fromJson(source: BufferedSource): T
}