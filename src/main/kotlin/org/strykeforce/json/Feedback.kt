package org.strykeforce.json

import com.ctre.phoenix6.configs.FeedbackConfigs
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Feedback(
  @Json val feedbackRotorOffset: Double = 0.0,
  @Json val sensorToMechanismRatio: Double = 1.0,
  @Json val feedbackRemoteSensorID: Int = 0,
  @Json val feedbackSensorSource: String = "RotorSensor",
  @Json val rotorToSensorRatio: Double = 1.0,
  @Json val velocityFilterTimeConstant: Double = 0.0,
) {

  fun getFeedbackConfigs(): FeedbackConfigs {
    var sourceValue: FeedbackSensorSourceValue = FeedbackSensorSourceValue.RotorSensor
    if (feedbackSensorSource == "RemoteCANcoder")
      sourceValue = FeedbackSensorSourceValue.RemoteCANcoder
    else if (feedbackSensorSource == "RemotePigeon2Yaw")
      sourceValue = FeedbackSensorSourceValue.RemotePigeon2Yaw
    else if (feedbackSensorSource == "RemotePigeon2Pitch")
      sourceValue = FeedbackSensorSourceValue.RemotePigeon2Pitch
    else if (feedbackSensorSource == "RemotePigeon2Roll")
      sourceValue = FeedbackSensorSourceValue.RemotePigeon2Roll
    else if (feedbackSensorSource == "FusedCANcoder")
      sourceValue = FeedbackSensorSourceValue.FusedCANcoder
    else if (feedbackSensorSource == "SyncCANcoder")
      sourceValue = FeedbackSensorSourceValue.SyncCANcoder
    else if (feedbackSensorSource == "RemoteCANdiPWM1")
      sourceValue = FeedbackSensorSourceValue.RemoteCANdiPWM1
    else if (feedbackSensorSource == "RemoteCANdiPWM2")
      sourceValue = FeedbackSensorSourceValue.RemoteCANdiPWM2
    else if (feedbackSensorSource == "RemoteCANdiQuadrature")
      sourceValue = FeedbackSensorSourceValue.RemoteCANdiQuadrature
    else if (feedbackSensorSource == "FusedCANdiPWM1")
      sourceValue = FeedbackSensorSourceValue.FusedCANdiPWM1
    else if (feedbackSensorSource == "FusedCANdiPWM2")
      sourceValue = FeedbackSensorSourceValue.FusedCANdiPWM2
    else if (feedbackSensorSource == "FusedCANdiQuadrature")
      sourceValue = FeedbackSensorSourceValue.FusedCANdiQuadrature
    else if (feedbackSensorSource == "SyncCANdiPWM1")
      sourceValue = FeedbackSensorSourceValue.SyncCANdiPWM1
    else if (feedbackSensorSource == "SyncCANdiPWM2")
      sourceValue = FeedbackSensorSourceValue.SyncCANdiPWM2
    return FeedbackConfigs()
      .withFeedbackRotorOffset(feedbackRotorOffset)
      .withSensorToMechanismRatio(sensorToMechanismRatio)
      .withFeedbackRemoteSensorID(feedbackRemoteSensorID)
      .withFeedbackSensorSource(sourceValue)
      .withRotorToSensorRatio(rotorToSensorRatio)
      .withVelocityFilterTimeConstant(velocityFilterTimeConstant)
  }
}
