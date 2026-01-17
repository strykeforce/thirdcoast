package org.strykeforce.json

import com.ctre.phoenix6.configs.MotionMagicConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MotionMagic(
  @Json val motionMagicAcceleration: Double = 0.0,
  @Json val motionMagicCruiseVelocity: Double = 0.0,
  @Json val motionMagicExpo_kA: Double = 0.1,
  @Json val motionMagicExpo_kV: Double = 0.12,
  @Json val motionMagicJerk: Double = 0.0,
) {

  fun getMotionMagicConfig(): MotionMagicConfigs {
    return MotionMagicConfigs()
      .withMotionMagicAcceleration(motionMagicAcceleration)
      .withMotionMagicCruiseVelocity(motionMagicCruiseVelocity)
      .withMotionMagicJerk(motionMagicJerk)
      .withMotionMagicExpo_kV(motionMagicExpo_kV)
      .withMotionMagicExpo_kA(motionMagicExpo_kA)
  }
}
