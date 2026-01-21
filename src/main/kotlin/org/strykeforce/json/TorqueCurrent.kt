package org.strykeforce.json

import com.ctre.phoenix6.configs.TorqueCurrentConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class TorqueCurrent(
  @Json val peakForwardTorqueCurrent: Double = 800.0,
  @Json val peakReverseTorqueCurrent: Double = -800.0,
  @Json val torqueCurrentNeutralDeadband: Double = 0.0,
) {
  fun getTorquecCurrentConfigs(): TorqueCurrentConfigs {
    return TorqueCurrentConfigs()
      .withPeakForwardTorqueCurrent(peakForwardTorqueCurrent)
      .withPeakReverseTorqueCurrent(peakReverseTorqueCurrent)
      .withTorqueNeutralDeadband(torqueCurrentNeutralDeadband)
  }
}
