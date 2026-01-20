package org.strykeforce.json

import com.ctre.phoenix6.configs.HardwareLimitSwitchConfigs
import com.ctre.phoenix6.signals.ForwardLimitSourceValue
import com.ctre.phoenix6.signals.ForwardLimitTypeValue
import com.ctre.phoenix6.signals.ReverseLimitSourceValue
import com.ctre.phoenix6.signals.ReverseLimitTypeValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class HardwareLimitSwitch(
  @Json val forwardLimitAutosetPositionEnable: Int = 0,
  @Json val forwardLimitAutosetPositionValue: Double = 0.0,
  @Json val forwardLimitEnable: Int = 1,
  @Json val forwardLimitRemoteSensorID: Int = 0,
  @Json val forwardLimitSource: String = "LimitSwitchPin",
  @Json val forwardLimitType: String = "NormallyOpen",
  @Json val reverseLimitAutosetPositionEnable: Int = 0,
  @Json val reverseLimitAutosetPositionValue: Double = 0.0,
  @Json val reverseLimitEnable: Int = 1,
  @Json val reverseLimitRemoteSensorID: Int = 0,
  @Json val reverseLimitSource: String = "LimitSwitchPin",
  @Json val reverseLimitType: String = "NormallyOpen",
) {

  fun getHardwareLimitSwitchConfigs(): HardwareLimitSwitchConfigs {
    var fwdLimSource: ForwardLimitSourceValue = ForwardLimitSourceValue.LimitSwitchPin
    if (forwardLimitSource == "RemoteTalonFX") fwdLimSource = ForwardLimitSourceValue.RemoteTalonFX
    else if (forwardLimitSource == "RemoteCANifier")
      fwdLimSource = ForwardLimitSourceValue.RemoteCANifier
    else if (forwardLimitSource == "RemoteCANcoder")
      fwdLimSource = ForwardLimitSourceValue.RemoteCANcoder
    else if (forwardLimitSource == "RemoteCANrange")
      fwdLimSource = ForwardLimitSourceValue.RemoteCANrange
    else if (forwardLimitSource == "RemoteCANdiS1")
      fwdLimSource = ForwardLimitSourceValue.RemoteCANdiS1
    else if (forwardLimitSource == "RemoteCANdiS2")
      fwdLimSource = ForwardLimitSourceValue.RemoteCANdiS2

    var revLimSource: ReverseLimitSourceValue = ReverseLimitSourceValue.LimitSwitchPin
    if (reverseLimitSource == "RemoteTalonFX") revLimSource = ReverseLimitSourceValue.RemoteTalonFX
    else if (reverseLimitSource == "RemoteCANifier")
      revLimSource = ReverseLimitSourceValue.RemoteCANifier
    else if (reverseLimitSource == "RemoteCANcoder")
      revLimSource = ReverseLimitSourceValue.RemoteCANcoder
    else if (reverseLimitSource == "RemoteCANrange")
      revLimSource = ReverseLimitSourceValue.RemoteCANrange
    else if (reverseLimitSource == "RemoteCANdiS1")
      revLimSource = ReverseLimitSourceValue.RemoteCANdiS1
    else if (reverseLimitSource == "RemoteCANdiS2")
      revLimSource = ReverseLimitSourceValue.RemoteCANdiS2

    var fwdLimType: ForwardLimitTypeValue = ForwardLimitTypeValue.NormallyOpen
    if (forwardLimitType == "NormallyClosed") fwdLimType = ForwardLimitTypeValue.NormallyClosed

    var revLimType: ReverseLimitTypeValue = ReverseLimitTypeValue.NormallyOpen
    if (reverseLimitType == "NormallyClosed") revLimType = ReverseLimitTypeValue.NormallyClosed

    var fwdEn: Boolean = true
    if (forwardLimitEnable == 0) fwdEn = false

    var revEn: Boolean = true
    if (reverseLimitEnable == 0) revEn = false

    var fwdAutoSetEn: Boolean = false
    if (forwardLimitAutosetPositionEnable == 1) fwdAutoSetEn = true

    var revAutoSetEn: Boolean = false
    if (reverseLimitAutosetPositionEnable == 1) revAutoSetEn = true

    return HardwareLimitSwitchConfigs()
      .withForwardLimitAutosetPositionEnable(fwdAutoSetEn)
      .withForwardLimitAutosetPositionValue(forwardLimitAutosetPositionValue)
      .withForwardLimitEnable(fwdEn)
      .withForwardLimitRemoteSensorID(forwardLimitRemoteSensorID)
      .withForwardLimitSource(fwdLimSource)
      .withForwardLimitType(fwdLimType)
      .withReverseLimitAutosetPositionEnable(revAutoSetEn)
      .withReverseLimitAutosetPositionValue(reverseLimitAutosetPositionValue)
      .withReverseLimitEnable(revEn)
      .withReverseLimitRemoteSensorID(reverseLimitRemoteSensorID)
      .withReverseLimitSource(revLimSource)
      .withReverseLimitType(revLimType)
  }
}
