package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
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
      @NotNull ControlMode mode,
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
        continuousCurrentLimit,
        peakCurrentLimit,
        peakCurrentLimitDuration,
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
    super.configure(talon);
    ErrorCode err;
    err = talon.configClosedloopRamp(closedLoopRampRate != null ? closedLoopRampRate : 0, timeout);
    Errors.check(talon, "configClosedloopRamp", err, logger);

    // TODO: remove voltage from names
    err = talon.configPeakOutputForward(valueOrElseZero(forwardOutputVoltagePeak, 1), timeout);
    Errors.check(talon, "configPeakOutputForward", err, logger);
    err = talon.configPeakOutputReverse(valueOrElseZero(reverseOutputVoltagePeak, -1), timeout);
    Errors.check(talon, "configPeakOutputReverse", err, logger);

    err =
        talon.configNominalOutputForward(valueOrElseZero(forwardOutputVoltageNominal, 0), timeout);
    Errors.check(talon, "configNominalOutputForward", err, logger);
    err =
        talon.configNominalOutputReverse(valueOrElseZero(reverseOutputVoltageNominal, 0), timeout);
    Errors.check(talon, "configNominalOutputReverse", err, logger);

    err =
        talon.configAllowableClosedloopError(0, valueOrElseZero(allowableClosedLoopError), timeout);
    Errors.check(talon, "configAllowableClosedloopError", err, logger);

    err = talon.config_kP(0, valueOrElseZero(pGain, 0), timeout);
    Errors.check(talon, "config_kP", err, logger);
    err = talon.config_kI(0, valueOrElseZero(iGain, 0), timeout);
    Errors.check(talon, "config_kI", err, logger);
    err = talon.config_kD(0, valueOrElseZero(dGain, 0), timeout);
    Errors.check(talon, "config_kD", err, logger);
    err = talon.config_kF(0, valueOrElseZero(fGain, 0), timeout);
    Errors.check(talon, "config_kF", err, logger);

    err = talon.config_IntegralZone(0, valueOrElseZero(iZone), timeout);
    Errors.check(talon, "config_IntegralZone", err, logger);
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

  @Override
  public String toString() {
    return "PIDTalonConfiguration{"
        + "closedLoopRampRate="
        + closedLoopRampRate
        + ", forwardOutputVoltagePeak="
        + forwardOutputVoltagePeak
        + ", reverseOutputVoltagePeak="
        + reverseOutputVoltagePeak
        + ", forwardOutputVoltageNominal="
        + forwardOutputVoltageNominal
        + ", reverseOutputVoltageNominal="
        + reverseOutputVoltageNominal
        + ", allowableClosedLoopError="
        + allowableClosedLoopError
        + ", nominalClosedLoopVoltage="
        + nominalClosedLoopVoltage
        + ", pGain="
        + pGain
        + ", iGain="
        + iGain
        + ", dGain="
        + dGain
        + ", fGain="
        + fGain
        + ", iZone="
        + iZone
        + "} "
        + super.toString();
  }
}
