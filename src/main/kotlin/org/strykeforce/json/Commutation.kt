package org.strykeforce.json

import com.ctre.phoenix6.configs.CommutationConfigs
import com.ctre.phoenix6.signals.AdvancedHallSupportValue
import com.ctre.phoenix6.signals.BrushedMotorWiringValue
import com.ctre.phoenix6.signals.MotorArrangementValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Commutation(
  @Json val advancedHallSupport: Boolean = false,
  @Json val brushedMotorWiring: String = "Leads_A_and_B",
  @Json val motorArrangement: String = "Disabled",
) {
  fun getCommutationConfigs(): CommutationConfigs {
    var wiring: BrushedMotorWiringValue = BrushedMotorWiringValue.Leads_A_and_B
    if (brushedMotorWiring == "Leads_A_and_C") wiring = BrushedMotorWiringValue.Leads_A_and_C
    else if (brushedMotorWiring == "Leads_B_and_C") wiring = BrushedMotorWiringValue.Leads_B_and_C

    var arrangement: MotorArrangementValue = MotorArrangementValue.Disabled
    if (motorArrangement == "Minion_JST") arrangement = MotorArrangementValue.Minion_JST
    else if (motorArrangement == "Brushed_DC") arrangement = MotorArrangementValue.Brushed_DC
    else if (motorArrangement == "NEO_JST") arrangement = MotorArrangementValue.NEO_JST
    else if (motorArrangement == "NEO550_JST") arrangement = MotorArrangementValue.NEO550_JST
    else if (motorArrangement == "VORTEX_JST") arrangement = MotorArrangementValue.VORTEX_JST
    else if (motorArrangement == "CustomBrushless")
      arrangement = MotorArrangementValue.CustomBrushless

    var support: AdvancedHallSupportValue = AdvancedHallSupportValue.Disabled
    if (advancedHallSupport) support = AdvancedHallSupportValue.Enabled

    return CommutationConfigs()
      .withAdvancedHallSupport(support)
      .withBrushedMotorWiring(wiring)
      .withMotorArrangement(arrangement)
  }
}
