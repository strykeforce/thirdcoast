package org.strykeforce.json

import com.ctre.phoenix6.configs.ExternalTempConfigs
import com.ctre.phoenix6.signals.TempSensorRequiredValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ExternalTemperature(
  @Json val tempSensorRequired: String = "Required",
  @Json val thermistorBeta: Double = 0.0,
  @Json val thermistorMaxTemperature: Double = 0.0,
  @Json val thermistorR0: Double = 0.0,
) {
  fun getExternalTempConfigs(): ExternalTempConfigs {
    var required: TempSensorRequiredValue = TempSensorRequiredValue.Required
    if (tempSensorRequired == "Not_Required") required = TempSensorRequiredValue.Not_Required

    return ExternalTempConfigs()
      .withTempSensorRequired(required)
      .withThermistorBeta(thermistorBeta)
      .withThermistorMaxTemperature(thermistorMaxTemperature)
      .withThermistorR0(thermistorR0)
  }
}
