package org.strykeforce.json

import com.ctre.phoenix6.configs.CustomBrushlessMotorConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CustomBrushless(
  @Json val hallCCWselect: Boolean = false,
  @Json val hallDirection: Boolean = false,
  @Json val hallduringAB: Int = 0,
  @Json val hallDuringAC: Int = 0,
  @Json val motorkV: Double = 500.0,
  @Json val polePairCount: Int = 1,
) {
  fun getCustomBrushlessConfig(): CustomBrushlessMotorConfigs {
    return CustomBrushlessMotorConfigs()
      .withHallCCWSelect(hallCCWselect)
      .withHallDirection(hallDirection)
      .withHallDuringAB(hallduringAB)
      .withHallDuringAC(hallDuringAC)
      .withMotorKv(motorkV)
      .withPolePairCount(polePairCount)
  }
}
