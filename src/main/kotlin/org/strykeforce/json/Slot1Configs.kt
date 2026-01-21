package org.strykeforce.json

import com.ctre.phoenix6.configs.Slot1Configs
import com.ctre.phoenix6.signals.GainSchedBehaviorValue
import com.ctre.phoenix6.signals.GravityTypeValue
import com.ctre.phoenix6.signals.StaticFeedforwardSignValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Slot1Configs(
  @Json val slot1GravityType: String = "Elevator_Static",
  @Json val slot1kA: Double = 0.0,
  @Json val slot1kD: Double = 0.0,
  @Json val slot1kG: Double = 0.0,
  @Json val slot1kI: Double = 0.0,
  @Json val slot1kP: Double = 0.0,
  @Json val slot1kS: Double = 0.0,
  @Json val slot1kV: Double = 0.0,
  @Json val slot1StaticFeedForwardSign: String = "UseVelocitySgn",
  @Json val slot1ArmPositionOffset: Double = 0.0,
  @Json val slot1GainSchedBehavior: String = "Inactive",
) {
  fun getSlot1Configs(): Slot1Configs {
    var gravity: GravityTypeValue = GravityTypeValue.Elevator_Static
    if (slot1GravityType == "Arm_Cosine") gravity = GravityTypeValue.Arm_Cosine

    var FFsign: StaticFeedforwardSignValue = StaticFeedforwardSignValue.UseVelocitySign
    if (slot1StaticFeedForwardSign == "UseClosedLoopSign")
      FFsign = StaticFeedforwardSignValue.UseClosedLoopSign

    var gainSched: GainSchedBehaviorValue = GainSchedBehaviorValue.Inactive
    if (slot1GainSchedBehavior == "UseSlot0") gainSched = GainSchedBehaviorValue.UseSlot0
    else if (slot1GainSchedBehavior == "UseSlot1") gainSched = GainSchedBehaviorValue.UseSlot1
    else if (slot1GainSchedBehavior == "UseSlot2") gainSched = GainSchedBehaviorValue.UseSlot2
    else if (slot1GainSchedBehavior == "ZeroOutput") gainSched = GainSchedBehaviorValue.ZeroOutput

    return Slot1Configs()
      .withGravityType(gravity)
      .withKA(slot1kA)
      .withKD(slot1kD)
      .withKG(slot1kG)
      .withKI(slot1kI)
      .withKP(slot1kP)
      .withKS(slot1kS)
      .withKV(slot1kV)
      .withStaticFeedforwardSign(FFsign)
      .withGravityArmPositionOffset(slot1ArmPositionOffset)
      .withGainSchedBehavior(gainSched)
  }
}
