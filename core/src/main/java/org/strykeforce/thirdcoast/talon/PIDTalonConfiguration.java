package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.jetbrains.annotations.NotNull;

public class PIDTalonConfiguration extends TalonConfiguration {

  private final Double closedLoopRampRate;
  private final Double forwardOutputVoltagePeak; // FIXME: these are no longer voltage
  private final Double reverseOutputVoltagePeak;
  private final Double forwardOutputVoltageNominal;
  private final Double reverseOutputVoltageNominal;
  private final Integer allowableClosedLoopError;
  private final Double nominalClosedLoopVoltage;
  private final Double pGain;
  private final Double iGain;
  private final Double dGain;
  private final Double fGain;
  private final Integer iZone;

  PIDTalonConfiguration(
      @NotNull String name,
      @NotNull TalonControlMode mode,
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
      Double openLoopRampRate,
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
        mode,
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
        openLoopRampRate,
        voltageCompSaturation);
    this.closedLoopRampRate = closedLoopRampRate;
    this.forwardOutputVoltagePeak = forwardOutputVoltagePeak;
    this.reverseOutputVoltagePeak = reverseOutputVoltagePeak;
    this.forwardOutputVoltageNominal = forwardOutputVoltageNominal;
    this.reverseOutputVoltageNominal = reverseOutputVoltageNominal;
    this.allowableClosedLoopError = allowableClosedLoopError;
    this.nominalClosedLoopVoltage = nominalClosedLoopVoltage;
    this.pGain = pGain;
    this.iGain = iGain;
    this.dGain = dGain;
    this.fGain = fGain;
    this.iZone = iZone;
  }

  @Override
  public void configure(@NotNull TalonSRX talon) {
    talon.configClosedloopRamp(closedLoopRampRate != null ? closedLoopRampRate : 0, TIMEOUT_MS);

    // TODO: remove voltage from names
    talon.configPeakOutputForward(valueOrElseZero(forwardOutputVoltagePeak, 12), TIMEOUT_MS);
    talon.configPeakOutputReverse(valueOrElseZero(reverseOutputVoltagePeak, -12), TIMEOUT_MS);

    talon.configNominalOutputForward(valueOrElseZero(forwardOutputVoltageNominal, 0), TIMEOUT_MS);
    talon.configNominalOutputReverse(valueOrElseZero(reverseOutputVoltageNominal, 0), TIMEOUT_MS);

    talon.configAllowableClosedloopError(0, valueOrElseZero(allowableClosedLoopError), TIMEOUT_MS);

    talon.config_kP(0, valueOrElseZero(pGain, 0), TIMEOUT_MS);
    talon.config_kI(0, valueOrElseZero(iGain, 0), TIMEOUT_MS);
    talon.config_kD(0, valueOrElseZero(dGain, 0), TIMEOUT_MS);
    talon.config_kF(0, valueOrElseZero(fGain, 0), TIMEOUT_MS);

    talon.config_IntegralZone(0, valueOrElseZero(iZone), TIMEOUT_MS);
    super.configure(talon);
  }

  public Double getClosedLoopRampRate() {
    return closedLoopRampRate;
  }

  public Double getForwardOutputVoltagePeak() {
    return forwardOutputVoltagePeak;
  }

  public Double getReverseOutputVoltagePeak() {
    return reverseOutputVoltagePeak;
  }

  public Double getForwardOutputVoltageNominal() {
    return forwardOutputVoltageNominal;
  }

  public Double getReverseOutputVoltageNominal() {
    return reverseOutputVoltageNominal;
  }

  public Integer getAllowableClosedLoopError() {
    return allowableClosedLoopError;
  }

  public Double getNominalClosedLoopVoltage() {
    return nominalClosedLoopVoltage;
  }

  public Double getPGain() {
    return pGain;
  }

  public Double getIGain() {
    return iGain;
  }

  public Double getDGain() {
    return dGain;
  }

  public Double getFGain() {
    return fGain;
  }

  public Integer getIZone() {
    return iZone;
  }

  // TODO: generate toString
}
