package org.strykeforce.json

import com.ctre.phoenix6.configs.ClosedLoopRampsConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ClosedLoopRamp(
  @Json(name = "dutyCycleClosedLoopRampPeriod") val dutyCycleClosedLoopRampPeriod: Double = 0.0,
  @Json(name = "voltageClosedLoopRampPeriod") val voltageClosedLoopRampPeriod: Double = 0.0,
  @Json(name = "torqueCurrentClosedLoopRampPeriod")
  val torqueCurrentClosedLoopRampPeriod: Double = 0.0,
) {

  fun getClosedLoopRampConfig(): ClosedLoopRampsConfigs {
    return ClosedLoopRampsConfigs()
      .withDutyCycleClosedLoopRampPeriod(dutyCycleClosedLoopRampPeriod)
      .withVoltageClosedLoopRampPeriod(voltageClosedLoopRampPeriod)
      .withTorqueClosedLoopRampPeriod(torqueCurrentClosedLoopRampPeriod)
  }
}
