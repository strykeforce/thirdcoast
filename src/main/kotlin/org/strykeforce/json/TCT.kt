package org.strykeforce.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.strykeforce.controller.motorControl.CTRE_ClosedLoopType
import org.strykeforce.controller.motorControl.CTRE_DifferentialType
import org.strykeforce.controller.motorControl.CTRE_FollowerConfig
import org.strykeforce.controller.motorControl.CTRE_FollowerType
import org.strykeforce.controller.motorControl.CTRE_Units
import org.strykeforce.controller.motorControl.MotionMagicType

@JsonClass(generateAdapter = true)
class TCT(
  @Json val openLoopUnits: String = "Percent",
  @Json val closedLoopUnits: String = "Voltage",
  @Json val mmType: String = "Standard",
  @Json val followerType: String = "Strict",
  @Json val followerConfig: String = "Standard",
  @Json val differentialType: String = "Open_Loop",
  @Json val closedLoopType: String = "Velocity",
  @Json val leaderID: Int = 0,
  @Json val activeSlot: Int = 0,
  @Json val differentialSlot: Int = 0,
  @Json val activeFOC: Int = 0,
  @Json val limitFwdMotion: Int = 0,
  @Json val limitRevMotion: Int = 0,
  @Json val ignoreHwLims: Int = 0,
  @Json val ignoreSwLims: Int = 0,
  @Json val useTimesync: Int = 0,
  @Json val opposeMain: Int = 0,
  @Json val torqueCurrentMax: Double = 0.0,
  @Json val overrideNeutral: Int = 0,
) {

  fun getOpenLoopUnits(): CTRE_Units {
    var units: CTRE_Units = CTRE_Units.Percent
    if (openLoopUnits == "Voltage") units = CTRE_Units.Voltage
    else if (openLoopUnits == "Torque_Current") units = CTRE_Units.Torque_Current
    return units
  }

  fun getClosedLoopUnits(): CTRE_Units {
    var units: CTRE_Units = CTRE_Units.Percent
    if (closedLoopUnits == "Voltage") units = CTRE_Units.Voltage
    else if (closedLoopUnits == "Torque_Current") units = CTRE_Units.Torque_Current
    return units
  }

  fun getMMtype(): MotionMagicType {
    var type: MotionMagicType = MotionMagicType.Standard
    if (mmType == "Velocity") type = MotionMagicType.Velocity
    else if (mmType == "Dynamic") type = MotionMagicType.Dynamic
    else if (mmType == "Exponential") type = MotionMagicType.Exponential
    else if (mmType == "DynamicExponential") type = MotionMagicType.DynamicExponential
    return type
  }

  fun getFollowerType(): CTRE_FollowerType {
    var type: CTRE_FollowerType = CTRE_FollowerType.Strict
    if (followerType == "Standard") type = CTRE_FollowerType.Standard
    return type
  }

  fun getFollowerConfig(): CTRE_FollowerConfig {
    var config: CTRE_FollowerConfig = CTRE_FollowerConfig.Normal
    if (followerConfig == "Differential") config = CTRE_FollowerConfig.Differential
    return config
  }

  fun getDifferentialType(): CTRE_DifferentialType {
    var type: CTRE_DifferentialType = CTRE_DifferentialType.Open_Loop
    if (differentialType == "Position") type = CTRE_DifferentialType.Position
    else if (differentialType == "Velocity") type = CTRE_DifferentialType.Velocity
    else if (differentialType == "Motion_Magic") type = CTRE_DifferentialType.Motion_Magic
    else if (differentialType == "Follower") type = CTRE_DifferentialType.Follower
    return type
  }

  fun getClosedLoopType(): CTRE_ClosedLoopType {
    var type: CTRE_ClosedLoopType = CTRE_ClosedLoopType.Velocity
    if (closedLoopType == "Position") type = CTRE_ClosedLoopType.Position
    else if (closedLoopType == "Motion_Magic") type = CTRE_ClosedLoopType.Motion_Magic
    return type
  }

  fun getFOCActive(): Boolean {
    if (activeFOC == 1) return true else return false
  }

  fun getLimFwdMotion(): Boolean {
    if (limitFwdMotion == 1) return true else return false
  }

  fun getLimRevMotion(): Boolean {
    if (limitRevMotion == 1) return true else return false
  }

  fun getIgnoreHwLims(): Boolean {
    if (ignoreHwLims == 1) return true else return false
  }

  fun getIgnoreSwLims(): Boolean {
    if (ignoreSwLims == 1) return true else return false
  }

  fun getUseTimesync(): Boolean {
    if (useTimesync == 1) return true else return false
  }

  fun getOpposeMain(): Boolean {
    if (opposeMain == 1) return true else return false
  }

  fun getOverrideNeutral(): Boolean {
    if (overrideNeutral == 1) return true else return false
  }
}
