package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.jetbrains.annotations.NotNull;

public class MotionMagicTalonConfiguration extends PIDTalonConfiguration {

  private final Integer motionMagicAcceleration;
  private final Integer motionMagicCruiseVelocity;

  MotionMagicTalonConfiguration(
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
      Integer motionMagicAcceleration,
      Integer motionMagicCruiseVelocity) {
    super(
        name,
        ControlMode.MotionMagic,
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
        iZone);
    this.motionMagicAcceleration = motionMagicAcceleration;
    this.motionMagicCruiseVelocity = motionMagicCruiseVelocity;
  }

  @Override
  public void configure(@NotNull TalonSRX talon) {
    super.configure(talon);
    if (talon instanceof ThirdCoastTalon) {
      ((ThirdCoastTalon) talon).changeControlMode(ControlMode.MotionMagic);
    }

    talon.configMotionAcceleration(valueOrElseZero(motionMagicAcceleration), TIMEOUT_MS);
    talon.configMotionCruiseVelocity(valueOrElseZero(motionMagicCruiseVelocity), TIMEOUT_MS);
  }

  public Integer getMotionMagicAcceleration() {
    return motionMagicAcceleration;
  }

  public Integer getMotionMagicCruiseVelocity() {
    return motionMagicCruiseVelocity;
  }

  @Override
  public String toString() {
    return "MotionMagicTalonConfiguration{"
        + "motionMagicAcceleration="
        + motionMagicAcceleration
        + ", motionMagicCruiseVelocity="
        + motionMagicCruiseVelocity
        + "} "
        + super.toString();
  }
}
