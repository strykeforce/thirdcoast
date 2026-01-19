package org.strykeforce.json

import com.ctre.phoenix6.configs.CustomBrushlessMotorConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CustomBrushless(
  @Json val hallCCWselect: Int = 0,
  @Json val hallDirection: Int = 0,
  @Json val hallduringAB: Int = 0,
  @Json val hallDuringAC: Int = 0,
  @Json val motorkV: Double = 500.0,
  @Json val polePairCount: Int = 1,
) {
  fun getCustomBrushlessConfig(): CustomBrushlessMotorConfigs {
    var hallCCW: Boolean = false
    if (hallCCWselect == 1) hallCCW = true

    var hallDir: Boolean = false
    if (hallDirection == 1) hallDir = true

    return CustomBrushlessMotorConfigs()
      .withHallCCWSelect(hallCCW)
      .withHallDirection(hallDir)
      .withHallDuringAB(hallduringAB)
      .withHallDuringAC(hallDuringAC)
      .withMotorKv(motorkV)
      .withPolePairCount(polePairCount)
  }
}
