package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.jetbrains.annotations.NotNull;

class SpeedTalonConfiguration extends PIDTalonConfiguration {

  SpeedTalonConfiguration(
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
      Integer iZone,
      Integer profileSlot) {
    super(
        name,
        ControlMode.Position,
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
        iZone,
        profileSlot);
  }

  @Override
  public void configure(@NotNull TalonSRX talon) {
    super.configure(talon);
    if (talon instanceof ThirdCoastTalon) {
      ((ThirdCoastTalon) talon).changeControlMode(ControlMode.Velocity);
    }
  }

  @Override
  public String toString() {
    return "SpeedTalonConfiguration{} " + super.toString();
  }
}
