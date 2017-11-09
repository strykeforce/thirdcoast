package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class PIDTalonConfiguration extends TalonConfiguration {

  private final Double outputVoltageMax;
  private final Double closedLoopRampRate;
  private final Double forwardOutputVoltagePeak;
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

  PIDTalonConfiguration(@NotNull String name,
      @NotNull CANTalon.TalonControlMode mode,
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
      Double pGain, Double iGain, Double dGain, Double fGain, Integer iZone) {
    super(name, mode, setpointMax, encoder, isBrakeInNeutral, isOutputReversed,
        velocityMeasurementPeriod, velocityMeasurementWindow, forwardLimitSwitch,
        reverseLimitSwitch, forwardSoftLimit, reverseSoftLimit, currentLimit, voltageRampRate);
    this.outputVoltageMax = outputVoltageMax;
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
  public void configure(@NotNull CANTalon talon) {
    talon.configMaxOutputVoltage(valueOrElse(outputVoltageMax,12));

    talon.setCloseLoopRampRate(closedLoopRampRate != null ? closedLoopRampRate : 0);

    talon.configPeakOutputVoltage(
        valueOrElse(forwardOutputVoltagePeak, 12),
        valueOrElse(reverseOutputVoltagePeak, -12));

    talon.configNominalOutputVoltage(
        valueOrElse(forwardOutputVoltageNominal, 0),
        valueOrElse(reverseOutputVoltageNominal, 0));

    talon.setAllowableClosedLoopErr(valueOrElse(allowableClosedLoopError, 0));

    talon.setNominalClosedLoopVoltage(valueOrElse(nominalClosedLoopVoltage, 0));

    talon.setPID(valueOrElse(pGain, 0), valueOrElse(iGain, 0), valueOrElse(dGain, 0));
    talon.setF(valueOrElse(fGain, 0));

    talon.setIZone(valueOrElse(iZone, 0));
    super.configure(talon);
  }

  private double valueOrElse(@Nullable Double value, double def) {
    if (value != null) {
      return value;
    }
    return def;
  }

  private int valueOrElse(@Nullable Integer value, int def) {
    if (value != null) {
      return value;
    }
    return def;
  }

  public Double getOutputVoltageMax() {
    return outputVoltageMax;
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
  @NotNull
  public String toString() {
    return "PIDTalonParameters{" +
        "outputVoltageMax=" + outputVoltageMax +
        ", forwardOutputVoltagePeak=" + forwardOutputVoltagePeak +
        ", reverseOutputVoltagePeak=" + reverseOutputVoltagePeak +
        ", forwardOutputVoltageNominal=" + forwardOutputVoltageNominal +
        ", reverseOutputVoltageNominal=" + reverseOutputVoltageNominal +
        ", allowableClosedLoopError=" + allowableClosedLoopError +
        ", nominalClosedLoopVoltage=" + nominalClosedLoopVoltage +
        ", pGain=" + pGain +
        ", iGain=" + iGain +
        ", dGain=" + dGain +
        ", fGain=" + fGain +
        ", iZone=" + iZone +
        "} " + super.toString();
  }
}
