package org.strykeforce.json

import com.ctre.phoenix6.configs.CurrentLimitsConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CurrentLimit(
  @Json(name = "statorCurrentLimit") val statorCurrentLimit: Double = 120.0,
  @Json(name = "statorCurrentLimitEnable") val statorCurrentLimitEnable: Int = 1,
  @Json(name = "supplyCurrentLimit") val supplyCurrentLimit: Double = 70.0,
  @Json(name = "supplyCurrentLimitEnable") val supplyCurrentLimitEnable: Int = 1,
  @Json(name = "supplyCurrentLowerLimit") val supplyCurrentLowerLimit: Double = 70.0,
  @Json(name = "supplyCurrentLowerTime") val supplyCurrentLowerTime: Double = 1.0,
) {

  fun getCurrentLimitConfig(): CurrentLimitsConfigs {
    var statorEnable: Boolean = true
    if (statorCurrentLimitEnable == 0) statorEnable = false

    var supplyEnable: Boolean = true
    if (supplyCurrentLimitEnable == 0) supplyEnable = false

    return CurrentLimitsConfigs()
      .withStatorCurrentLimit(statorCurrentLimit)
      .withStatorCurrentLimitEnable(statorEnable)
      .withSupplyCurrentLimitEnable(supplyEnable)
      .withSupplyCurrentLimit(supplyCurrentLimit)
      .withSupplyCurrentLowerLimit(supplyCurrentLowerLimit)
      .withSupplyCurrentLowerTime(supplyCurrentLowerTime)
  }
}
