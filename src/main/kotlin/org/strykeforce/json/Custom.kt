package org.strykeforce.json

import com.ctre.phoenix6.configs.CustomParamsConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Custom(
  @Json(name = "customParam0") val customParam0: Int = 0,
  @Json(name = "customParam1") val customParam1: Int = 0,
) {

  fun getCustomConfigs(): CustomParamsConfigs {
    return CustomParamsConfigs().withCustomParam0(customParam0).withCustomParam1(customParam1)
  }
}
