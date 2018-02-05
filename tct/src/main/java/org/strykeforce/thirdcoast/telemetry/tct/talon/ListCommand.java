package org.strykeforce.thirdcoast.telemetry.tct.talon;

import static org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommand.VERIFY;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import java.util.Formatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;

/** Display a list of all Talons. */
@ParametersAreNonnullByDefault
public class ListCommand extends AbstractCommand {

  public static final String NAME = VERIFY + "List Selected Talons with Current Register Values";
  private static final String FORMAT_DESCRIPTION = "%12s";
  private static final String FORMAT_DOUBLE = "%12.3f";
  private static final String FORMAT_INTEGER = "%12d";
  private static final String FORMAT_STRING = "%12s";

  private final TalonSet talonSet;

  @Inject
  ListCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader);
    this.talonSet = talonSet;
  }

  private static int intValue(@Nullable VelocityMeasPeriod period) {
    if (period == null) {
      return 0;
    }
    switch (period) {
      case Period_1Ms:
        return 1;
      case Period_2Ms:
        return 2;
      case Period_5Ms:
        return 5;
      case Period_10Ms:
        return 10;
      case Period_20Ms:
        return 20;
      case Period_25Ms:
        return 25;
      case Period_50Ms:
        return 50;
      case Period_100Ms:
        return 100;
      default:
        return 0;
    }
  }

  @Override
  public void perform() {
    Set<ThirdCoastTalon> talons = talonSet.selected();
    terminal.writer().println();
    if (talons.size() == 0) {
      terminal.writer().println(Messages.NO_TALONS);
      return;
    }
    terminal.writer().print(header());
    outputString(
        "Mode:",
        talons
            .stream()
            .map(ThirdCoastTalon::getControlMode)
            .map(Enum::name)
            .collect(Collectors.toList()));
    outputParameter("P:", ParamEnum.eProfileParamSlot_P);
    outputParameter("I:", ParamEnum.eProfileParamSlot_I);
    outputParameter("D:", ParamEnum.eProfileParamSlot_D);
    outputParameter("F:", ParamEnum.eProfileParamSlot_F);
    outputParameter("IZone:", ParamEnum.eProfileParamSlot_IZone);
    outputParameter("Max I Accum:", ParamEnum.eProfileParamSlot_MaxIAccum);
    outputParameter("Allowed Err:", ParamEnum.eProfileParamSlot_AllowableErr);

    outputInteger(
        "Position:",
        talons.stream().map(t -> t.getSelectedSensorPosition(0)).collect(Collectors.toList()));
    outputInteger(
        "Pulse Width:",
        talons
            .stream()
            .map(t -> t.getSensorCollection().getPulseWidthPosition())
            .map(pos -> pos & 0xFFF)
            .collect(Collectors.toList()));

    outputParameter("Fwd Soft:", ParamEnum.eForwardSoftLimitThreshold);
    outputParameter("Rev Soft:", ParamEnum.eReverseSoftLimitThreshold);

    outputBoolean(
        "Fwd LS Clsd:",
        talons
            .stream()
            .map(t -> t.getSensorCollection().isFwdLimitSwitchClosed())
            .collect(Collectors.toList()));
    outputBoolean(
        "Rev LS Clsd:",
        talons
            .stream()
            .map(t -> t.getSensorCollection().isRevLimitSwitchClosed())
            .collect(Collectors.toList()));

    outputInteger(
        "Analog:",
        talons
            .stream()
            .map(t -> t.getSensorCollection().getAnalogInRaw())
            .collect(Collectors.toList()));

    outputParameter("VM Period:", ParamEnum.eSampleVelocityPeriod);
    outputParameter("VM Window:", ParamEnum.eSampleVelocityWindow);
    terminal.writer().println();
  }

  private void outputParameter(String description, ParamEnum param) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));

    talonSet
        .selected()
        .stream()
        .mapToDouble(t -> t.configGetParameter(param, 0, 0))
        .forEach(d -> formatter.format(FORMAT_DOUBLE, d));

    terminal.writer().println(sb.toString());
  }

  private void outputString(
      @SuppressWarnings("SameParameterValue") String description, List<String> values) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    for (String val : values) {
      formatter.format(FORMAT_STRING, val);
    }
    terminal.writer().println(sb.toString());
  }

  private void outputBoolean(String description, List<Boolean> values) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    for (Boolean val : values) {
      formatter.format(FORMAT_STRING, val ? "YES" : "NO");
    }
    terminal.writer().println(sb.toString());
  }

  private void outputInteger(String description, List<Integer> values) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    for (Integer val : values) {
      formatter.format(FORMAT_INTEGER, val);
    }
    terminal.writer().println(sb.toString());
  }

  private void outputDouble(String description, List<Double> values) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    for (Double val : values) {
      formatter.format(FORMAT_DOUBLE, val);
    }
    terminal.writer().println(sb.toString());
  }

  private String header() {
    // desc(12) value(10) * 6
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    formatter.format(FORMAT_DESCRIPTION, "Talon:");
    for (ThirdCoastTalon talon : talonSet.selected()) {
      formatter.format(FORMAT_INTEGER, talon.getDeviceID());
    }
    sb.append("\n");
    return Messages.bold(sb.toString());
  }
}
