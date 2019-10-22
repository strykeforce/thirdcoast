package org.strykeforce.deadeye

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import okio.Buffer

interface Camera<T : TargetData> {
  val id: String
  var enabled: Boolean
  val error: Boolean
  val config: Config
  val stream: Stream
  var targetDataListener: TargetDataListener?
  var jsonAdapter: JsonAdapter<T>
  fun parse(buffer: Buffer)

  @JsonClass(generateAdapter = true)
  data class Config(
    @Json(name = "sn") val serial: Int, val exposure: Double,
    val hue: List<Int>, @Json(name = "sat") val saturation: List<Int>, @Json(name = "val") val value: List<Int>
  )

  @JsonClass(generateAdapter = true)
  data class Stream(@Json(name = "sn") val serial: Int, val contour: String, val view: String, val url: String)

}
