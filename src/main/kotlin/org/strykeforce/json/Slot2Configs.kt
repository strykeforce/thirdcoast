package org.strykeforce.json

import com.ctre.phoenix6.configs.Slot2Configs
import com.ctre.phoenix6.signals.GainSchedBehaviorValue
import com.ctre.phoenix6.signals.GravityTypeValue
import com.ctre.phoenix6.signals.StaticFeedforwardSignValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Slot2Configs(
  @Json val slot2GravityType: String = "Elevator_Static",
  @Json val slot2kA: Double = 0.0,
  @Json val slot2kD: Double = 0.0,
  @Json val slot2kG: Double = 0.0,
  @Json val slot2kI: Double = 0.0,
  @Json val slot2kP: Double = 0.0,
  @Json val slot2kS: Double = 0.0,
  @Json val slot2kV: Double = 0.0,
  @Json val slot2StaticFeedForwardSign: String = "UseVelocitySgn",
  @Json val slot2ArmPositionOffset: Double = 0.0,
  @Json val slot2GainSchedBehavior: String = "Inactive",
) {
  fun getSlot2Configs(): Slot2Configs {
    var gravity: GravityTypeValue = GravityTypeValue.Elevator_Static
    if (slot2GravityType == "Arm_Cosine") gravity = GravityTypeValue.Arm_Cosine

    var FFsign: StaticFeedforwardSignValue = StaticFeedforwardSignValue.UseVelocitySign
    if (slot2StaticFeedForwardSign == "UseClosedLoopSign")
      FFsign = StaticFeedforwardSignValue.UseClosedLoopSign

    var gainSched: GainSchedBehaviorValue = GainSchedBehaviorValue.Inactive
    if (slot2GainSchedBehavior == "Useslot0") gainSched = GainSchedBehaviorValue.UseSlot0
    else if (slot2GainSchedBehavior == "UseSlot1") gainSched = GainSchedBehaviorValue.UseSlot1
    else if (slot2GainSchedBehavior == "UseSlot2") gainSched = GainSchedBehaviorValue.UseSlot2
    else if (slot2GainSchedBehavior == "ZeroOutput") gainSched = GainSchedBehaviorValue.ZeroOutput

    return Slot2Configs()
      .withGravityType(gravity)
      .withKA(slot2kA)
      .withKD(slot2kD)
      .withKG(slot2kG)
      .withKI(slot2kI)
      .withKP(slot2kP)
      .withKS(slot2kS)
      .withKV(slot2kV)
      .withStaticFeedforwardSign(FFsign)
      .withGravityArmPositionOffset(slot2ArmPositionOffset)
      .withGainSchedBehavior(gainSched)
  }
}
