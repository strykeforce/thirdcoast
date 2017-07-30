package org.strykeforce.sidewinder.talon;

import com.ctre.CANTalon;
import com.electronwill.nightconfig.toml.TomlConfig;

class PIDTalonParameters extends TalonParameters {

  private final double outputVoltageMax;
  private final double forwardOutputVoltagePeak;
  private final double reverseOutputVoltagePeak;
  private final double forwardOutputVoltageNominal;
  private final double reverseOutputVoltageNominal;
  private final int allowableClosedLoopError;
  private final double nominalClosedLoopVoltage;
  private final double pGain;
  private final double iGain;
  private final double dGain;
  private final double fGain;
  private final int iZone;

  PIDTalonParameters(TomlConfig toml) {
    super(toml);
    outputVoltageMax = (double) toml.getOptionalValue("output_voltage_max").orElse(0);
    forwardOutputVoltagePeak = (double) toml.getOptionalValue("forward_output_voltage_peak")
        .orElse(0);
    reverseOutputVoltagePeak = (double) toml.getOptionalValue("reverse_output_voltage_peak")
        .orElse(0);
    forwardOutputVoltageNominal = (double) toml.getOptionalValue("forward_output_voltage_nominal")
        .orElse(0);
    reverseOutputVoltageNominal = (double) toml.getOptionalValue("reverse_output_voltage_nominal")
        .orElse(0);
    allowableClosedLoopError = (int) toml.getOptionalValue("allowable_closed_loop_error").orElse(0);
    nominalClosedLoopVoltage = (double) toml.getOptionalValue("nominal_closed_loop_voltage")
        .orElse(0); // DisableNominalClosedLoopVoltage, SetNominalClosedLoopVoltage
    pGain = (double) toml.getOptionalValue("P").orElse(0.0);
    iGain = (double) toml.getOptionalValue("I").orElse(0.0);
    dGain = (double) toml.getOptionalValue("D").orElse(0.0);
    fGain = (double) toml.getOptionalValue("F").orElse(0.0);
    iZone = (int) toml.getOptionalValue("I_zone").orElse(0);
  }

  @Override
  public void configure(CANTalon talon) {
    super.configure(talon);
    if (outputVoltageMax != 0) {
      talon.configMaxOutputVoltage(outputVoltageMax);
    }
    talon.configPeakOutputVoltage(forwardOutputVoltagePeak, reverseOutputVoltagePeak);
    talon.configNominalOutputVoltage(forwardOutputVoltageNominal, reverseOutputVoltageNominal);
    talon.setAllowableClosedLoopErr(allowableClosedLoopError);
    talon.setNominalClosedLoopVoltage(nominalClosedLoopVoltage);
    talon.setPID(pGain, iGain, dGain);
    talon.setF(fGain);
    talon.setIZone(iZone);
  }

  public double getOutputVoltageMax() {
    return outputVoltageMax;
  }

  public double getForwardOutputVoltagePeak() {
    return forwardOutputVoltagePeak;
  }

  public double getReverseOutputVoltagePeak() {
    return reverseOutputVoltagePeak;
  }

  public double getForwardOutputVoltageNominal() {
    return forwardOutputVoltageNominal;
  }

  public double getReverseOutputVoltageNominal() {
    return reverseOutputVoltageNominal;
  }

  public int getAllowableClosedLoopError() {
    return allowableClosedLoopError;
  }

  public double getNominalClosedLoopVoltage() {
    return nominalClosedLoopVoltage;
  }

  public double getpGain() {
    return pGain;
  }

  public double getiGain() {
    return iGain;
  }

  public double getdGain() {
    return dGain;
  }

  public double getfGain() {
    return fGain;
  }

  public int getiZone() {
    return iZone;
  }
}
