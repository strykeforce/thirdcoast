package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
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

/**
 * Display a list of all Talons.
 */
@ParametersAreNonnullByDefault
public class ListCommand extends AbstractCommand {

  public final static String NAME = "List Selected Talons with Current Register Values";
  private final static String FORMAT_DESCRIPTION = "%12s";
  private final static String FORMAT_DOUBLE = "%12.3f";
  private final static String FORMAT_INTEGER = "%12d";
  private final static String FORMAT_STRING = "%12s";

  private final TalonSet talonSet;

  @Inject
  ListCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader);
    this.talonSet = talonSet;
  }

  private static int intValue(@Nullable VelocityMeasurementPeriod period) {
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
    Set<CANTalon> talons = talonSet.selected();
    terminal.writer().println();
    if (talons.size() == 0){
      terminal.writer().println(Messages.NO_TALONS);
      return;
    }
    terminal.writer().print(header());
    stringLine("Mode:", talons.stream().map(CANTalon::getControlMode).map(TalonControlMode::name)
        .collect(Collectors.toList()));
    doubleLine("Setpoint:",
        talons.stream().map(CANTalon::getSetpoint).collect(Collectors.toList()));
    doubleLine("P:", talons.stream().map(CANTalon::getP).collect(Collectors.toList()));
    doubleLine("I:", talons.stream().map(CANTalon::getI).collect(Collectors.toList()));
    doubleLine("D:", talons.stream().map(CANTalon::getD).collect(Collectors.toList()));
    doubleLine("F:", talons.stream().map(CANTalon::getF).collect(Collectors.toList()));
    doubleLine("I-zone:", talons.stream().map(CANTalon::getIZone).collect(Collectors.toList()));

    doubleLine("Position:",
        talons.stream().map(CANTalon::getPosition).collect(Collectors.toList()));
    intLine("Abs Enc:",
        talons.stream().map(CANTalon::getPulseWidthPosition).map(pos -> pos & 0xFFF)
            .collect(Collectors.toList()));

    intLine("Fwd Soft:",
        talons.stream().map(CANTalon::getForwardSoftLimit).collect(Collectors.toList()));
    intLine("Rev Soft:",
        talons.stream().map(CANTalon::getReverseSoftLimit).collect(Collectors.toList()));

    booleanLine("Fwd LS Clsd:",
        talons.stream().map(CANTalon::isFwdLimitSwitchClosed).collect(Collectors.toList()));
    booleanLine("Rev LS Clsd:",
        talons.stream().map(CANTalon::isRevLimitSwitchClosed).collect(Collectors.toList()));

    intLine("Analog:", talons.stream().map(CANTalon::getAnalogInRaw).collect(Collectors.toList()));

    intLine("VM Period:",
        talons.stream().map(CANTalon::GetVelocityMeasurementPeriod).map(ListCommand::intValue)
            .collect(Collectors.toList()));
    intLine("VM Window:",
        talons.stream().map(CANTalon::GetVelocityMeasurementWindow).collect(Collectors.toList()));
    terminal.writer().println();
  }

  private void stringLine(@SuppressWarnings("SameParameterValue") String description,
      List<String> values) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    for (String val : values) {
      formatter.format(FORMAT_STRING, val);
    }
    terminal.writer().println(sb.toString());
  }

  private void booleanLine(String description, List<Boolean> values) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    for (Boolean val : values) {
      formatter.format(FORMAT_STRING, val ? "YES" : "NO");
    }
    terminal.writer().println(sb.toString());
  }

  private void intLine(String description, List<Integer> values) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    sb.append(Messages.bold(String.format(FORMAT_DESCRIPTION, description)));
    for (Integer val : values) {
      formatter.format(FORMAT_INTEGER, val);
    }
    terminal.writer().println(sb.toString());
  }

  private void doubleLine(String description, List<Double> values) {
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
    for (CANTalon talon : talonSet.selected()) {
      formatter.format(FORMAT_INTEGER, talon.getDeviceID());
    }
    sb.append("\n");
    return Messages.bold(sb.toString());
  }
}
