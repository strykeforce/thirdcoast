package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import org.jetbrains.annotations.NotNull;

class VoltageTalonConfiguration extends TalonConfiguration {

  VoltageTalonConfiguration(@NotNull String name, double setpointMax,
      Encoder encoder, Boolean isBrakeInNeutral, Boolean isOutputReversed,
      VelocityMeasurementPeriod velocityMeasurementPeriod, Integer velocityMeasurementWindow,
      LimitSwitch forwardLimitSwitch, LimitSwitch reverseLimitSwitch,
      SoftLimit forwardSoftLimit, SoftLimit reverseSoftLimit, Integer currentLimit) {
    super(name, TalonControlMode.Voltage, setpointMax, encoder, isBrakeInNeutral, isOutputReversed, velocityMeasurementPeriod,
        velocityMeasurementWindow, forwardLimitSwitch, reverseLimitSwitch, forwardSoftLimit,
        reverseSoftLimit, currentLimit);
  }

  @Override
  public void configure(@NotNull CANTalon talon) {
    super.configure(talon);
    talon.changeControlMode(TalonControlMode.Voltage);
  }

  @Override
  @NotNull
  public String toString() {
    return "VoltageTalonParameters{} " + super.toString();
  }
}
