package org.strykeforce.json

import com.ctre.phoenix6.configs.Slot0Configs
import com.ctre.phoenix6.signals.GainSchedBehaviorValue
import com.ctre.phoenix6.signals.GravityTypeValue
import com.ctre.phoenix6.signals.StaticFeedforwardSignValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Slot0Configs(
  @Json val slot0GravityType: String = "Elevator_Static",
  @Json val slot0kA: Double = 0.0,
  @Json val slot0kD: Double = 0.0,
  @Json val slot0kG: Double = 0.0,
  @Json val slot0kI: Double = 0.0,
  @Json val slot0kP: Double = 0.0,
  @Json val slot0kS: Double = 0.0,
  @Json val slot0kV: Double = 0.0,
  @Json val slot0StaticFeedForwardSign: String = "UseVelocitySgn",
  @Json val slot0ArmPositionOffset: Double = 0.0,
  @Json val slot0GainSchedBehavior: String = "Inactive",
) {
  fun getSlot0Configs(): Slot0Configs {
    var gravity: GravityTypeValue = GravityTypeValue.Elevator_Static
    if (slot0GravityType == "Arm_Cosine") gravity = GravityTypeValue.Arm_Cosine

    var FFsign: StaticFeedforwardSignValue = StaticFeedforwardSignValue.UseVelocitySign
    if (slot0StaticFeedForwardSign == "UseClosedLoopSign")
      FFsign = StaticFeedforwardSignValue.UseClosedLoopSign

    var gainSched: GainSchedBehaviorValue = GainSchedBehaviorValue.Inactive
    if (slot0GainSchedBehavior == "UseSlot0") gainSched = GainSchedBehaviorValue.UseSlot0
    else if (slot0GainSchedBehavior == "UseSlot1") gainSched = GainSchedBehaviorValue.UseSlot1
    else if (slot0GainSchedBehavior == "UseSlot2") gainSched = GainSchedBehaviorValue.UseSlot2
    else if (slot0GainSchedBehavior == "ZeroOutput") gainSched = GainSchedBehaviorValue.ZeroOutput

    return Slot0Configs()
      .withGravityType(gravity)
      .withKA(slot0kA)
      .withKD(slot0kD)
      .withKG(slot0kG)
      .withKI(slot0kI)
      .withKP(slot0kP)
      .withKS(slot0kS)
      .withKV(slot0kV)
      .withStaticFeedforwardSign(FFsign)
      .withGravityArmPositionOffset(slot0ArmPositionOffset)
      .withGainSchedBehavior(gainSched)
  }
}
