package org.strykeforce.json

import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SoftwareLimitSwitch(
  @Json val forwardSoftLimitEnable: Int = 0,
  @Json val forwardSoftLimitThreshold: Double = 0.0,
  @Json val reverseSoftLimitEnable: Int = 0,
  @Json val reverseSoftLimitThreshold: Double = 0.0,
) {
  fun getSoftwareLimitSwitchConfigs(): SoftwareLimitSwitchConfigs {
    var fwdEn: Boolean = false
    if (forwardSoftLimitEnable == 1) fwdEn = true

    var revEn: Boolean = false
    if (reverseSoftLimitEnable == 1) revEn = true

    return SoftwareLimitSwitchConfigs()
      .withForwardSoftLimitEnable(fwdEn)
      .withForwardSoftLimitThreshold(forwardSoftLimitThreshold)
      .withReverseSoftLimitEnable(revEn)
      .withReverseSoftLimitThreshold(reverseSoftLimitThreshold)
  }
}
