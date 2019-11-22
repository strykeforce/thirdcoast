package org.strykeforce.deadeye

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CenterTargetData(
  id: String = "NA",
  sn: Int = 0,
  valid: Boolean = false,
  val x: Double = 0.0,
  val y: Double = 0.0
) : TargetData(id, sn, valid)
