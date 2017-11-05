package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.electronwill.nightconfig.core.UnmodifiableConfig;

class PIDTalonConfiguration extends TalonConfiguration {

  public final static String OUTPUT_VOLTAGE_MAX = "output_voltage_max";
  public final static String FORWARD_OUTPUT_VOLTAGE_PEAK = "forward_output_voltage_peak";
  public final static String REVERSE_OUTPUT_VOLTAGE_PEAK = "reverse_output_voltage_peak";
  public final static String FORWARD_OUTPUT_VOLTAGE_NOMINAL = "forward_output_voltage_nominal";
  public final static String REVERSE_OUTPUT_VOLTAGE_NOMINAL = "reverse_output_voltage_nominal";
  public final static String ALLOWABLE_CLOSED_LOOP_ERROR = "allowable_closed_loop_error";
  public final static String NOMINAL_CLOSED_LOOP_VOLTAGE = "nominal_closed_loop_voltage";
  public final static String K_P = "P";
  public final static String K_I = "I";
  public final static String K_D = "D";
  public final static String K_F = "F";
  public final static String I_ZONE = "I_zone";

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

  PIDTalonConfiguration(UnmodifiableConfig config) {
    super(config);
    outputVoltageMax = (double) config.getOptional(OUTPUT_VOLTAGE_MAX).orElse(0.0);
    forwardOutputVoltagePeak = (double) config.getOptional(FORWARD_OUTPUT_VOLTAGE_PEAK).orElse(0.0);
    reverseOutputVoltagePeak = (double) config.getOptional(REVERSE_OUTPUT_VOLTAGE_PEAK).orElse(0.0);
    forwardOutputVoltageNominal = (double) config.getOptional(FORWARD_OUTPUT_VOLTAGE_NOMINAL)
        .orElse(0.0);
    reverseOutputVoltageNominal = (double) config.getOptional(REVERSE_OUTPUT_VOLTAGE_NOMINAL)
        .orElse(0.0);
    allowableClosedLoopError = (int) config.getOptional(ALLOWABLE_CLOSED_LOOP_ERROR).orElse(0);
    nominalClosedLoopVoltage = (double) config.getOptional(NOMINAL_CLOSED_LOOP_VOLTAGE)
        .orElse(0.0); // DisableNominalClosedLoopVoltage, SetNominalClosedLoopVoltage
    pGain = (double) config.getOptional(K_P).orElse(0.0);
    iGain = (double) config.getOptional(K_I).orElse(0.0);
    dGain = (double) config.getOptional(K_D).orElse(0.0);
    fGain = (double) config.getOptional(K_F).orElse(0.0);
    iZone = (int) config.getOptional(I_ZONE).orElse(0);
  }

  @Override
  public void configure(CANTalon talon) {
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
    super.configure(talon);
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
