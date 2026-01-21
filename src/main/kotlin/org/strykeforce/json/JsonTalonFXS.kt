package org.strykeforce.json

import com.ctre.phoenix6.configs.TalonFXSConfiguration
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.strykeforce.controller.motorControl.*

@JsonClass(generateAdapter = true)
class JsonTalonFXS(
  @Json(name = "Audio") var audio: Audio = Audio(),
  @Json(name = "ClosedLoopGen") var closedLoopGen: ClosedLoopGeneral = ClosedLoopGeneral(),
  @Json(name = "ClosedLoopRamp") var closedLoopRamp: ClosedLoopRamp = ClosedLoopRamp(),
  @Json(name = "Commutation") var commutation: Commutation = Commutation(),
  @Json(name = "CurrentLimit") var currentLimit: CurrentLimit = CurrentLimit(),
  @Json(name = "CustomBrushless") var customBrushlles: CustomBrushless = CustomBrushless(),
  @Json(name = "Custom") var custom: Custom = Custom(),
  @Json(name = "Differential") var differential: Differential = Differential(),
  @Json(name = "DifferentialSensors") var diffSensors: DifferentialSensors = DifferentialSensors(),
  @Json(name = "ExternalFeedback") var extFeedback: ExternalFeedback = ExternalFeedback(),
  @Json(name = "ExternalTemperature") var extTemp: ExternalTemperature = ExternalTemperature(),
  @Json(name = "futureProofConfigs") var futureProofConfigs: Boolean = true,
  @Json(name = "HardwareLimitSwitch")
  var hwLimitSwitch: HardwareLimitSwitch = HardwareLimitSwitch(),
  @Json(name = "MotionMagic") var motionMagic: MotionMagic = MotionMagic(),
  @Json(name = "MotorOutput") var motorOutput: MotorOutput = MotorOutput(),
  @Json(name = "OpenLoopRamp") var openLoopRamp: OpenLoopRamp = OpenLoopRamp(),
  @Json(name = "Slot0Configs") var slot0: Slot0Configs = Slot0Configs(),
  @Json(name = "Slot1Configs") var slot1: Slot1Configs = Slot1Configs(),
  @Json(name = "Slot2Configs") var slot2: Slot2Configs = Slot2Configs(),
  @Json(name = "SoftwareLimitSwitch") var swLimSwitch: SoftwareLimitSwitch = SoftwareLimitSwitch(),
  @Json(name = "Voltage") var voltage: Voltage = Voltage(),
  @Json(name = "TCT") var tct: TCT = TCT(),
) {
  fun getTalonFXSConfig(): TalonFXSConfiguration {
    return TalonFXSConfiguration()
      .withAudio(audio.getAudioCOnfig())
      .withClosedLoopGeneral(closedLoopGen.getClosedLoopGenConfigs())
      .withClosedLoopRamps(closedLoopRamp.getClosedLoopRampConfig())
      .withCurrentLimits(currentLimit.getCurrentLimitConfig())
      .withCommutation(commutation.getCommutationConfigs())
      .withCustomBrushlessMotor(customBrushlles.getCustomBrushlessConfig())
      .withCustomParams(custom.getCustomConfigs())
      .withDifferentialConstants(differential.getDifferentialConfigs())
      .withDifferentialSensors(diffSensors.getDifferentialSensorConfig())
      .withExternalFeedback(extFeedback.getExternalFeedbackConfigs())
      .withExternalTemp(extTemp.getExternalTempConfigs())
      .withHardwareLimitSwitch(hwLimitSwitch.getHardwareLimitSwitchConfigs())
      .withMotionMagic(motionMagic.getMotionMagicConfig())
      .withMotorOutput(motorOutput.getMotorOutputConfigs())
      .withOpenLoopRamps(openLoopRamp.getOpenLoopRampConfigs())
      .withSlot0(slot0.getSlot0Configs())
      .withSlot1(slot1.getSlot1Configs())
      .withSlot2(slot2.getSlot2Configs())
      .withSoftwareLimitSwitch(swLimSwitch.getSoftwareLimitSwitchConfigs())
      .withVoltage(voltage.getVoltageConfigs())
  }

  fun getOpenLoopUnits(): CTRE_Units {
    return tct.getOpenLoopUnits()
  }

  fun getClosedLoopUnits(): CTRE_Units {
    return tct.getClosedLoopUnits()
  }

  fun getMotionMagicType(): MotionMagicType {
    return tct.getMMtype()
  }

  fun getFollowerType(): CTRE_FollowerType {
    return tct.getFollowerType()
  }

  fun getFollowerConfig(): CTRE_FollowerConfig {
    return tct.getFollowerConfig()
  }

  fun getDifferentialType(): CTRE_DifferentialType {
    return tct.getDifferentialType()
  }

  fun getClosedLoopType(): CTRE_ClosedLoopType {
    return tct.getClosedLoopType()
  }

  fun getLeaderID(): Int {
    return tct.leaderID
  }

  fun getActiveSlot(): Int {
    return tct.activeSlot
  }

  fun getDifferentialSlot(): Int {
    return tct.differentialSlot
  }

  fun getActiveFOC(): Boolean {
    return tct.getFOCActive()
  }

  fun getLimitFwdMotion(): Boolean {
    return tct.getLimFwdMotion()
  }

  fun getLimitRevMotion(): Boolean {
    return tct.getLimRevMotion()
  }

  fun getIgnoreHwLimits(): Boolean {
    return tct.getIgnoreHwLims()
  }

  fun getIgnoreSwLimits(): Boolean {
    return tct.getIgnoreSwLims()
  }

  fun getUseTimesync(): Boolean {
    return tct.getUseTimesync()
  }

  fun getOpposeMain(): Boolean {
    return tct.getOpposeMain()
  }

  fun getTorqueCurrentMax(): Double {
    return tct.torqueCurrentMax
  }

  fun getOverrideNeutral(): Boolean {
    return tct.getOverrideNeutral()
  }
}
