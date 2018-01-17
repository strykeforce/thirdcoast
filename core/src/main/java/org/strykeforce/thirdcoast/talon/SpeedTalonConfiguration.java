package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import org.jetbrains.annotations.NotNull;

class SpeedTalonConfiguration extends PIDTalonConfiguration {

  SpeedTalonConfiguration(
      @NotNull String name,
      double setpointMax,
      Encoder encoder,
      Boolean isBrakeInNeutral,
      Boolean isOutputReversed,
      VelocityMeasurementPeriod velocityMeasurementPeriod,
      Integer velocityMeasurementWindow,
      LimitSwitch forwardLimitSwitch,
      LimitSwitch reverseLimitSwitch,
      SoftLimit forwardSoftLimit,
      SoftLimit reverseSoftLimit,
      Integer currentLimit,
      Double voltageRampRate,
      Double outputVoltageMax,
      Double closedLoopRampRate,
      Double forwardOutputVoltagePeak,
      Double reverseOutputVoltagePeak,
      Double forwardOutputVoltageNominal,
      Double reverseOutputVoltageNominal,
      Integer allowableClosedLoopError,
      Double nominalClosedLoopVoltage,
      Double pGain,
      Double iGain,
      Double dGain,
      Double fGain,
      Integer iZone) {
    super(
        name,
        TalonControlMode.Position,
        setpointMax,
        encoder,
        isBrakeInNeutral,
        isOutputReversed,
        velocityMeasurementPeriod,
        velocityMeasurementWindow,
        forwardLimitSwitch,
        reverseLimitSwitch,
        forwardSoftLimit,
        reverseSoftLimit,
        currentLimit,
        voltageRampRate,
        outputVoltageMax,
        closedLoopRampRate,
        forwardOutputVoltagePeak,
        reverseOutputVoltagePeak,
        forwardOutputVoltageNominal,
        reverseOutputVoltageNominal,
        allowableClosedLoopError,
        nominalClosedLoopVoltage,
        pGain,
        iGain,
        dGain,
        fGain,
        iZone);
  }

  @Override
  public void configure(@NotNull CANTalon talon) {
    super.configure(talon);
    talon.changeControlMode(TalonControlMode.Speed);
  }

  @Override
  @NotNull
  public String toString() {
    return "SpeedTalonConfiguration{} " + super.toString();
  }
}
