package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.jetbrains.annotations.NotNull;

class PositionTalonConfiguration extends PIDTalonConfiguration {

  PositionTalonConfiguration(
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
      Integer currentLimit,
      Double voltageRampRate,
      Double voltageCompSaturation,
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
        neutralMode,
        isOutputReversed,
        velocityMeasurementPeriod,
        velocityMeasurementWindow,
        forwardLimitSwitch,
        reverseLimitSwitch,
        forwardSoftLimit,
        reverseSoftLimit,
        currentLimit,
        voltageRampRate,
        voltageCompSaturation,
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
  public void configure(@NotNull TalonSRX talon) {
    if (talon instanceof ThirdCoastTalon) {
      ((ThirdCoastTalon) talon).changeControlMode(TalonControlMode.Position);
    }

    super.configure(talon);
  }

  @Override
  @NotNull
  public String toString() {
    return "PositionTalonParameters{} " + super.toString();
  }
}
