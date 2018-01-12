package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.jetbrains.annotations.NotNull;

class VoltageTalonConfiguration extends TalonConfiguration {

  VoltageTalonConfiguration(
      @NotNull String name,
      double setpointMax,
      Encoder encoder,
      NeutralMode neutralMode,
      Boolean isOutputReversed,
      VelocityMeasPeriod velocityMeasurementPeriod,
      Integer velocityMeasurementWindow,
      LimitSwitch forwardLimitSwitch,
      LimitSwitch reverseLimitSwitch,
      SoftLimit forwardSoftLimit,
      SoftLimit reverseSoftLimit,
      Integer continuousCurrentLimit,
      Integer peakCurrentLimit,
      Integer peakCurrentLimitDuration,
      Double voltageRampRate,
      Double voltageCompSaturation) {
    super(
        name,
        ControlMode.PercentOutput,
        setpointMax,
        encoder,
        neutralMode,
        isOutputReversed,
        velocityMeasurementPeriod,
        velocityMeasurementWindow,
        forwardLimitSwitch,
        reverseLimitSwitch,
        forwardSoftLimit,
        reverseSoftLimit,
        continuousCurrentLimit,
        peakCurrentLimit,
        peakCurrentLimitDuration,
        voltageRampRate,
        voltageCompSaturation);
  }

  @Override
  public void configure(@NotNull TalonSRX talon) {
    super.configure(talon);
    if (talon instanceof ThirdCoastTalon) {
      ((ThirdCoastTalon) talon).changeControlMode(ControlMode.PercentOutput);
    }
  }

  @Override
  public String toString() {
    return "VoltageTalonParameters{} " + super.toString();
  }
}
