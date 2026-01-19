package org.strykeforce.json

import com.ctre.phoenix6.configs.ClosedLoopGeneralConfigs
import com.ctre.phoenix6.signals.GainSchedKpBehaviorValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ClosedLoopGeneral(
  @Json(name = "continuousWrap") val continuousWrap: Int = 0,
  @Json(name = "diffContinuousWrap") val diffContinuousWrap: Int = 0,
  @Json(name = "gainSchedErrorThres") val gainSchedErrorThres: Double = 0.0,
  @Json(name = "gainSchedKpBehavior") val gainSchedKpBehavior: String = "Continuous",
) {

  fun getClosedLoopGenConfigs(): ClosedLoopGeneralConfigs {
    var kPBehave = GainSchedKpBehaviorValue.Continuous
    if (gainSchedKpBehavior == "Discontinuous") kPBehave = GainSchedKpBehaviorValue.Discontinuous

    var contWrap: Boolean = false
    if (continuousWrap == 1) contWrap = true

    var diffContWrap: Boolean = false
    if (diffContinuousWrap == 1) diffContWrap = true

    return ClosedLoopGeneralConfigs()
      .withContinuousWrap(contWrap)
      .withDifferentialContinuousWrap(diffContWrap)
      .withGainSchedErrorThreshold(gainSchedErrorThres)
      .withGainSchedKpBehavior(kPBehave)
  }
}
