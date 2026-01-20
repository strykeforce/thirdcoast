package org.strykeforce.json

import com.ctre.phoenix6.configs.MotorOutputConfigs
import com.ctre.phoenix6.signals.InvertedValue
import com.ctre.phoenix6.signals.NeutralModeValue
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MotorOutput(
  @Json val controlTimesyncFrequencyHz: Double = 0.0,
  @Json val dutyCycleNeutralDeadband: Double = 0.0,
  @Json val inverted: String = "CounterClockwise_Positive",
  @Json val neutralMode: String = "Coast",
  @Json val peakForwardDutyCycle: Double = 1.0,
  @Json val peakReverseDutyCycle: Double = -1.0,
) {

  fun getMotorOutputConfigs(): MotorOutputConfigs {
    var invertValue: InvertedValue = InvertedValue.CounterClockwise_Positive
    if (inverted == "Clockwise_Positive") invertValue = InvertedValue.Clockwise_Positive

    var neutralType: NeutralModeValue = NeutralModeValue.Coast
    if (neutralMode == "Brake") neutralType = NeutralModeValue.Brake

    return MotorOutputConfigs()
      .withControlTimesyncFreqHz(controlTimesyncFrequencyHz)
      .withDutyCycleNeutralDeadband(dutyCycleNeutralDeadband)
      .withInverted(invertValue)
      .withNeutralMode(neutralType)
      .withPeakForwardDutyCycle(peakForwardDutyCycle)
      .withPeakReverseDutyCycle(peakReverseDutyCycle)
  }
}
