package org.strykeforce.json

import com.ctre.phoenix6.configs.ExternalFeedbackConfigs
import com.ctre.phoenix6.signals.ExternalFeedbackSensorSourceValue
import com.ctre.phoenix6.signals.SensorPhaseValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ExternalFeedback(
  @Json val absSensorDiscontinuity: Double = 0.5,
  @Json val absSensorOffset: Double = 0.0,
  @Json val externalFeedbackSource: String = "Commutation",
  @Json val feedbackRemoteSensorID: Int = 0,
  @Json val quadEdgesPerRot: Int = 4096,
  @Json val rotorToSensorRatio: Double = 1.0,
  @Json val sensorPhase: String = "Aligned",
  @Json val sensorToMechanismRatio: Double = 1.0,
  @Json val velocityFilterTimeConstant: Double = 0.0,
) {

  fun getExternalFeedbackConfigs(): ExternalFeedbackConfigs {
    var sourceValue: ExternalFeedbackSensorSourceValue =
      ExternalFeedbackSensorSourceValue.Commutation
    if (externalFeedbackSource == "FusedCANcoder")
      sourceValue = ExternalFeedbackSensorSourceValue.FusedCANcoder
    else if (externalFeedbackSource == "FusedCANdiPWM1")
      sourceValue = ExternalFeedbackSensorSourceValue.FusedCANdiPWM1
    else if (externalFeedbackSource == "FusedCANdiPWM2")
      sourceValue = ExternalFeedbackSensorSourceValue.FusedCANdiPWM2
    else if (externalFeedbackSource == "FusedCANdiQuadrature")
      sourceValue = ExternalFeedbackSensorSourceValue.FusedCANdiQuadrature
    else if (externalFeedbackSource == "PulseWidth")
      sourceValue = ExternalFeedbackSensorSourceValue.PulseWidth
    else if (externalFeedbackSource == "Quadrature")
      sourceValue = ExternalFeedbackSensorSourceValue.Quadrature
    else if (externalFeedbackSource == "RemoteCANcoder")
      sourceValue = ExternalFeedbackSensorSourceValue.RemoteCANcoder
    else if (externalFeedbackSource == "RemoteCANdiPWM1")
      sourceValue = ExternalFeedbackSensorSourceValue.RemoteCANdiPWM1
    else if (externalFeedbackSource == "RemoteCANdiPWM2")
      sourceValue = ExternalFeedbackSensorSourceValue.RemoteCANdiPWM2
    else if (externalFeedbackSource == "RemoteCANdiQuadrature")
      sourceValue = ExternalFeedbackSensorSourceValue.RemoteCANdiQuadrature
    else if (externalFeedbackSource == "RemotePigeon2Roll")
      sourceValue = ExternalFeedbackSensorSourceValue.RemotePigeon2Roll
    else if (externalFeedbackSource == "RemotePigeon2Pitch")
      sourceValue = ExternalFeedbackSensorSourceValue.RemotePigeon2Pitch
    else if (externalFeedbackSource == "RemotePigeon2Yaw")
      sourceValue = ExternalFeedbackSensorSourceValue.RemotePigeon2Yaw
    else if (externalFeedbackSource == "SyncCANcoder")
      sourceValue = ExternalFeedbackSensorSourceValue.SyncCANcoder
    else if (externalFeedbackSource == "SyncCANdiPWM1")
      sourceValue = ExternalFeedbackSensorSourceValue.SyncCANdiPWM1
    else if (externalFeedbackSource == "SyncCANdiPWM2")
      sourceValue = ExternalFeedbackSensorSourceValue.SyncCANdiPWM2

    var phase: SensorPhaseValue = SensorPhaseValue.Aligned
    if (sensorPhase == "Opposed") phase = SensorPhaseValue.Opposed

    return ExternalFeedbackConfigs()
      .withAbsoluteSensorDiscontinuityPoint(absSensorDiscontinuity)
      .withAbsoluteSensorOffset(absSensorOffset)
      .withExternalFeedbackSensorSource(sourceValue)
      .withFeedbackRemoteSensorID(feedbackRemoteSensorID)
      .withQuadratureEdgesPerRotation(quadEdgesPerRot)
      .withRotorToSensorRatio(rotorToSensorRatio)
      .withSensorPhase(phase)
      .withSensorToMechanismRatio(sensorToMechanismRatio)
      .withVelocityFilterTimeConstant(velocityFilterTimeConstant)
  }
}
