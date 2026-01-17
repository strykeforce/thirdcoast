package org.strykeforce.json

import com.ctre.phoenix6.configs.CurrentLimitsConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CurrentLimit(
  @Json(name = "statorCurrentLimit") val statorCurrentLimit: Double = 120.0,
  @Json(name = "statorCurrentLimitEnable") val statorCurrentLimitEnable: Boolean = true,
  @Json(name = "supplyCurrentLimit") val supplyCurrentLimit: Double = 70.0,
  @Json(name = "supplyCurrentLimitEnable") val supplyCurrentLimitEnable: Boolean = true,
  @Json(name = "supplyCurrentLowerLimit") val supplyCurrentLowerLimit: Double = 70.0,
  @Json(name = "supplyCurrentLowerTime") val supplyCurrentLowerTime: Double = 1.0,
) {

  fun getCurrentLimitConfig(): CurrentLimitsConfigs {
    return CurrentLimitsConfigs()
      .withStatorCurrentLimit(statorCurrentLimit)
      .withStatorCurrentLimitEnable(statorCurrentLimitEnable)
      .withSupplyCurrentLimit(supplyCurrentLimit)
      .withSupplyCurrentLowerLimit(supplyCurrentLowerLimit)
      .withSupplyCurrentLowerTime(supplyCurrentLowerTime)
  }
}
