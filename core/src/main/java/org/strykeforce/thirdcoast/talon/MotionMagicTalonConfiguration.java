package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import org.jetbrains.annotations.NotNull;

public class MotionMagicTalonConfiguration extends PIDTalonConfiguration {

  private final Double motionMagicAcceleration;
  private final Double motionMagicCruiseVelocity;

  MotionMagicTalonConfiguration(
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
      Integer iZone,
      Double motionMagicAcceleration,
      Double motionMagicCruiseVelocity) {
    super(
        name,
        TalonControlMode.MotionMagic,
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
    this.motionMagicAcceleration = motionMagicAcceleration;
    this.motionMagicCruiseVelocity = motionMagicCruiseVelocity;
  }

  @Override
  public void configure(@NotNull CANTalon talon) {
    super.configure(talon);
    talon.changeControlMode(TalonControlMode.MotionMagic);
    talon.setMotionMagicAcceleration(valueOrElse(motionMagicAcceleration, 0));
    talon.setMotionMagicCruiseVelocity(valueOrElse(motionMagicCruiseVelocity, 0));
  }

  public Double getMotionMagicAcceleration() {
    return motionMagicAcceleration;
  }

  public Double getMotionMagicCruiseVelocity() {
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
