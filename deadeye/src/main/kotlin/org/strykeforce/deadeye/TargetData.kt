package org.strykeforce.deadeye

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class TargetData(
  val id: String = "NA",
  val sn: Int = 0,
  val valid: Boolean = false
)
