package org.strykeforce.json

import com.ctre.phoenix6.configs.OpenLoopRampsConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class OpenLoopRamp(
  @Json val dutyCycleOpenLoopRampPeriod: Double = 0.0,
  @Json val voltageOpenLoopRampPeriod: Double = 0.0,
  @Json val torqueCurrentOpenLoopRampPeriod: Double = 0.0,
) {

  fun getOpenLoopRampConfigs(): OpenLoopRampsConfigs {
    return OpenLoopRampsConfigs()
      .withDutyCycleOpenLoopRampPeriod(dutyCycleOpenLoopRampPeriod)
      .withVoltageOpenLoopRampPeriod(voltageOpenLoopRampPeriod)
      .withTorqueOpenLoopRampPeriod(torqueCurrentOpenLoopRampPeriod)
  }
}
