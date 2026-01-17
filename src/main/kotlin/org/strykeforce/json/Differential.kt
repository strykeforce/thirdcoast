package org.strykeforce.json

import com.ctre.phoenix6.configs.DifferentialConstantsConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Differential(
  @Json(name = "peakDifferentialDutyCycle") val peakDifferentialDutyCycle: Double = 2.0,
  @Json(name = "peakDifferentialVoltage") val peakDifferentialVoltage: Double = 32.0,
  @Json(name = "peakDifferentialTorqueCurrent") val peakDifferentialTorqueCurrent: Double = 1600.0,
) {

  fun getDifferentialConfigs(): DifferentialConstantsConfigs {
    return DifferentialConstantsConfigs()
      .withPeakDifferentialDutyCycle(peakDifferentialDutyCycle)
      .withPeakDifferentialVoltage(peakDifferentialVoltage)
      .withPeakDifferentialTorqueCurrent(peakDifferentialTorqueCurrent)
  }
}
