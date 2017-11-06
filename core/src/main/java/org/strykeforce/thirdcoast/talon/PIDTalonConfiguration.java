package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import com.electronwill.nightconfig.core.UnmodifiableConfig;

class PIDTalonConfiguration extends TalonConfiguration {

  private final Double outputVoltageMax;
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

  public PIDTalonConfiguration(String name, double setpointMax,
      Encoder encoder, Boolean isBrakeInNeutral, Boolean isOutputReversed,
      VelocityMeasurementPeriod velocityMeasurementPeriod,
      Integer velocityMeasurementWindow,
      LimitSwitch forwardLimitSwitch, LimitSwitch reverseLimitSwitch,
      SoftLimit forwardSoftLimit, SoftLimit reverseSoftLimit, Integer currentLimit,
      Double outputVoltageMax, Double forwardOutputVoltagePeak,
      Double reverseOutputVoltagePeak, Double forwardOutputVoltageNominal,
      Double reverseOutputVoltageNominal, Integer allowableClosedLoopError,
      Double nominalClosedLoopVoltage, Double pGain, Double iGain, Double dGain,
      Double fGain, Integer iZone) {
    super(name, setpointMax, encoder, isBrakeInNeutral, isOutputReversed, velocityMeasurementPeriod,
        velocityMeasurementWindow, forwardLimitSwitch, reverseLimitSwitch, forwardSoftLimit,
        reverseSoftLimit, currentLimit);
    this.outputVoltageMax = outputVoltageMax;
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
  public void configure(CANTalon talon) {
    if (outputVoltageMax != null && outputVoltageMax != 0) {
      talon.configMaxOutputVoltage(outputVoltageMax);
    }

    talon.configPeakOutputVoltage(
        valueOrZero(forwardOutputVoltagePeak),
        valueOrZero(reverseOutputVoltagePeak));

    talon.configNominalOutputVoltage(
        valueOrZero(forwardOutputVoltageNominal),
        valueOrZero(reverseOutputVoltageNominal));

    talon.setAllowableClosedLoopErr(valueOrZero(allowableClosedLoopError));

    talon.setNominalClosedLoopVoltage(valueOrZero(nominalClosedLoopVoltage));

    talon.setPID(valueOrZero(pGain), valueOrZero(iGain), valueOrZero(dGain));
    talon.setF(valueOrZero(fGain));

    talon.setIZone(valueOrZero(iZone));
    super.configure(talon);
  }

  double valueOrZero(Double value) {
    if (value != null) {
      return value;
    }
    return 0;
  }

  int valueOrZero(Integer value) {
    if (value != null) {
      return value;
    }
    return 0;
  }

  public Double getOutputVoltageMax() {
    return outputVoltageMax;
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

  public Double getpGain() {
    return pGain;
  }

  public Double getiGain() {
    return iGain;
  }

  public Double getdGain() {
    return dGain;
  }

  public Double getfGain() {
    return fGain;
  }

  public Integer getiZone() {
    return iZone;
  }

  @Override
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
