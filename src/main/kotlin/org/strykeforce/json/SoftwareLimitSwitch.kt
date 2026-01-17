package org.strykeforce.json

import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SoftwareLimitSwitch(
  @Json val forwardSoftLimitEnable: Boolean = false,
  @Json val forwardSoftLimitThreshold: Double = 0.0,
  @Json val reverseSoftLimitEnable: Boolean = false,
  @Json val reverseSoftLimitThreshold: Double = 0.0,
) {
  fun getSoftwareLimitSwitchConfigs(): SoftwareLimitSwitchConfigs {
    return SoftwareLimitSwitchConfigs()
      .withForwardSoftLimitEnable(forwardSoftLimitEnable)
      .withForwardSoftLimitThreshold(forwardSoftLimitThreshold)
      .withReverseSoftLimitEnable(reverseSoftLimitEnable)
      .withReverseSoftLimitThreshold(reverseSoftLimitThreshold)
  }
}
