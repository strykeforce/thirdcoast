package org.strykeforce.json

import com.ctre.phoenix6.configs.ClosedLoopGeneralConfigs
import com.ctre.phoenix6.signals.GainSchedKpBehaviorValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ClosedLoopGeneral(
  @Json(name = "continuousWrap") val continuousWrap: Boolean = false,
  @Json(name = "diffContinuousWrap") val diffContinuousWrap: Boolean = false,
  @Json(name = "gainSchedErrorThres") val gainSchedErrorThres: Double = 0.0,
  @Json(name = "gainSchedKpBehavior") val gainSchedKpBehavior: String = "Continuous",
) {

  fun getClosedLoopGenConfigs(): ClosedLoopGeneralConfigs {
    var kPBehave = GainSchedKpBehaviorValue.Continuous
    if (gainSchedKpBehavior == "Discontinuous") kPBehave = GainSchedKpBehaviorValue.Discontinuous
    return ClosedLoopGeneralConfigs()
      .withContinuousWrap(continuousWrap)
      .withDifferentialContinuousWrap(diffContinuousWrap)
      .withGainSchedErrorThreshold(gainSchedErrorThres)
      .withGainSchedKpBehavior(kPBehave)
  }
}
