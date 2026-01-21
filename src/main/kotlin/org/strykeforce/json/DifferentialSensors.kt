package org.strykeforce.json

import com.ctre.phoenix6.configs.DifferentialSensorsConfigs
import com.ctre.phoenix6.signals.DifferentialSensorSourceValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class DifferentialSensors(
  @Json(name = "differentialSensorSource") val differentialSensorSource: String = "Disabled",
  @Json(name = "differentialTalonFXSensorID") val differentialTalonFXSensorID: Int = 0,
  @Json(name = "differentialRemoteSensorID") val differentialRemoteSensorID: Int = 0,
  @Json(name = "sensorToDiffRatio") val sensorToDiffRatio: Double = 1.0,
) {

  fun getDifferentialSensorConfig(): DifferentialSensorsConfigs {
    var sourceValue: DifferentialSensorSourceValue = DifferentialSensorSourceValue.Disabled
    if (differentialSensorSource == "RemoteTalonFX_HalfDiff")
      sourceValue = DifferentialSensorSourceValue.RemoteTalonFX_HalfDiff
    else if (differentialSensorSource == "RemotePigeon2Yaw")
      sourceValue = DifferentialSensorSourceValue.RemotePigeon2Yaw
    else if (differentialSensorSource == "RemotePigeon2Pitch")
      sourceValue = DifferentialSensorSourceValue.RemotePigeon2Pitch
    else if (differentialSensorSource == "RemotePigeon2Roll")
      sourceValue = DifferentialSensorSourceValue.RemotePigeon2Roll
    else if (differentialSensorSource == "RemoteCANcoder")
      sourceValue = DifferentialSensorSourceValue.RemoteCANcoder
    else if (differentialSensorSource == "RemoteCANdiPWM1")
      sourceValue = DifferentialSensorSourceValue.RemoteCANdiPWM1
    else if (differentialSensorSource == "RemoteCANdiPWM2")
      sourceValue = DifferentialSensorSourceValue.RemoteCANdiPWM2
    else if (differentialSensorSource == "RemoteCANdiQuadrature")
      sourceValue = DifferentialSensorSourceValue.RemoteCANdiQuadrature
    return DifferentialSensorsConfigs()
      .withDifferentialSensorSource(sourceValue)
      .withDifferentialTalonFXSensorID(differentialTalonFXSensorID)
      .withDifferentialRemoteSensorID(differentialRemoteSensorID)
      .withSensorToDifferentialRatio(sensorToDiffRatio)
  }
}
