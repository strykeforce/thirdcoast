package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import java.io.PrintWriter;
import java.util.Formatter;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.Encoder;
import org.strykeforce.thirdcoast.talon.LimitSwitch;
import org.strykeforce.thirdcoast.talon.MotionMagicTalonConfiguration;
import org.strykeforce.thirdcoast.talon.PIDTalonConfiguration;
import org.strykeforce.thirdcoast.talon.SoftLimit;
import org.strykeforce.thirdcoast.talon.TalonConfiguration;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;

@ParametersAreNonnullByDefault
public class InspectCommand extends AbstractCommand {

  public static final String NAME = "Inspect Selected Talons Configuration Settings";
  private static final String FORMAT_DESCRIPTION = "%-24s";
  private static final String FORMAT_STRING = "%12s";
  private static final String FORMAT_INTEGER = "%12d";
  private static final String FORMAT_DOUBLE = "%12.3f";
  private final TalonSet talonSet;

  @Inject
  InspectCommand(TalonSet talonSet, LineReader reader) {
    super(NAME, reader);
    this.talonSet = talonSet;
  }

  @Override
  public void perform() {
    PrintWriter writer = terminal.writer();
    TalonConfiguration config = talonSet.getActiveTalonConfiguration();
    writer.println();

    stringLine("Mode:", config.getMode().name());
    doubleLine("Max Setpoint:", config.getSetpointMax());
    Encoder encoder = config.getEncoder();
    if (encoder != null) {
      writer.println();
      stringLine("Encoder:", encoder.getDevice().name());
      booleanLine("  Reversed:", encoder.isReversed());
      writer.println();
    } else {
      stringLine("Encoder:", "DEFAULT");
    }

    NeutralMode neutralMode = config.getBrakeInNeutral();
    stringLine("Neutral Mode:", neutralMode != null ? neutralMode.name() : "DEFAULT");
    booleanLine("Output Reversed:", config.getOutputReversed());
    VelocityMeasPeriod period = config.getVelocityMeasurementPeriod();
    stringLine("Vel. Meas. Period:", period != null ? period.name() : "DEFAULT");
    intLine("Vel. Meas. Window:", config.getVelocityMeasurementWindow());

    LimitSwitch limit = config.getForwardLimitSwitch();
    if (limit != null) {
      writer.println();
      booleanLine("Fwd Limit Switch:", limit.isEnabled());
      booleanLine("  Normally Open :", limit.isNormallyOpen());
    } else {
      stringLine("Fwd Limit Switch:", "DEFAULT");
    }

    limit = config.getReverseLimitSwitch();
    if (limit != null) {
      writer.println();
      booleanLine("Rev Limit Switch:", limit.isEnabled());
      booleanLine("  Normally Open :", limit.isNormallyOpen());
      writer.println();
    } else {
      stringLine("Rev Limit Switch:", "DEFAULT");
    }

    SoftLimit soft = config.getForwardSoftLimit();
    if (soft != null) {
      writer.println();
      booleanLine("Fwd Soft Limit:", soft.isEnabled());
      doubleLine("  Position:", soft.getPosition());
      writer.println();
    } else {
      stringLine("Fwd Soft Limit:", "DEFAULT");
    }

    soft = config.getReverseSoftLimit();
    if (soft != null) {
      writer.println();
      booleanLine("Rev Soft Limit:", soft.isEnabled());
      doubleLine("  Position:", soft.getPosition());
      writer.println();
    } else {
      stringLine("Rev Soft Limit:", "DEFAULT");
    }

    intLine("Current Limit:", config.getContinuousCurrentLimit());
    doubleLine("Voltage Ramp Rate:", config.getOpenLoopRampTime());

    if (config.getMode() == ControlMode.PercentOutput) {
      return;
    }
    PIDTalonConfiguration pid = (PIDTalonConfiguration) config;
    writer.println();
    doubleLine("P:", pid.getPGain());
    doubleLine("I:", pid.getIGain());
    doubleLine("D:", pid.getDGain());
    doubleLine("F:", pid.getFGain());
    intLine("I-zone:", pid.getIZone());
    if (config.getMode() == ControlMode.MotionMagic) {
      writer.println();
      MotionMagicTalonConfiguration mmtc = (MotionMagicTalonConfiguration) pid;
      intLine("MM Cruise Velocity:", mmtc.getMotionMagicCruiseVelocity());
      intLine("MM Acceleration:", mmtc.getMotionMagicAcceleration());
    }
    writer.println();
    intLine("Allowable CL Error:", pid.getAllowableClosedLoopError());
    doubleLine("Nominal CL Voltage:", pid.getNominalClosedLoopVoltage());
    doubleLine("Output Voltage Max:", pid.getVoltageCompSaturation());
    doubleLine("Fwd Peak Output Voltage:", pid.getForwardOutputVoltagePeak());
    doubleLine("Rev Peak Output Voltage:", pid.getReverseOutputVoltagePeak());
    doubleLine("Fwd Nom. Output Voltage:", pid.getForwardOutputVoltageNominal());
    doubleLine("Rev Nom. Output Voltage:", pid.getReverseOutputVoltageNominal());

    writer.println();
  }

  private void stringLine(String description, String value) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    formatter.format(FORMAT_STRING, value);
    terminal.writer().println(sb.toString());
  }

  private void booleanLine(String description, @Nullable Boolean value) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    formatter.format(FORMAT_STRING, value == null ? "DEFAULT" : (value ? "YES" : "NO"));
    terminal.writer().println(sb.toString());
  }

  private void intLine(String description, @Nullable Integer value) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    if (value != null) {
      formatter.format(FORMAT_INTEGER, value);
    } else {
      formatter.format(FORMAT_STRING, "DEFAULT");
    }
    terminal.writer().println(sb.toString());
  }

  private void doubleLine(String description, @Nullable Double value) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    if (value != null) {
      formatter.format(FORMAT_DOUBLE, value);
    } else {
      formatter.format(FORMAT_STRING, "DEFAULT");
    }
    terminal.writer().println(sb.toString());
  }
}
