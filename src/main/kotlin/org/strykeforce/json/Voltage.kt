package org.strykeforce.json

import com.ctre.phoenix6.configs.VoltageConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Voltage(
  @Json val peakForwardVoltage: Double = 16.0,
  @Json val peakReverseVoltage: Double = -16.0,
  @Json val supplyVoltageTimeConstant: Double = 0.0,
) {

  fun getVoltageConfigs(): VoltageConfigs {
    return VoltageConfigs()
      .withPeakForwardVoltage(peakForwardVoltage)
      .withPeakReverseVoltage(peakReverseVoltage)
      .withSupplyVoltageTimeConstant(supplyVoltageTimeConstant)
  }
}
