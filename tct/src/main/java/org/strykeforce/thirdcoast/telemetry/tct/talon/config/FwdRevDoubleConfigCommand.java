package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

public abstract class FwdRevDoubleConfigCommand extends ConfigCommand {

  public FwdRevDoubleConfigCommand(String name, int weight, Terminal terminal,
      TalonSet talonSet) {
    super(name, weight, terminal, talonSet);
  }

  public FwdRevDoubleConfigCommand(String name, Terminal terminal,
      TalonSet talonSet) {
    super(name, terminal, talonSet);
  }

  protected abstract void config(CANTalon talon, double foward, double reverse);

  @Override
  public void perform() {
    Optional<double[]> opt = getFwdRevDoubles();
    if (!opt.isPresent()) {
      return;
    }
    double[] fr = opt.get();
    for (CANTalon talon : talonSet.selected()) {
      config(talon, fr[0], fr[1]);
      logger.info("set {} for {} to {}/{}", name(), talon.getDescription(), fr[0], fr[1]);
    }
  }

  protected Optional<double[]> getFwdRevDoubles() {
    terminal.writer().println("enter <forward>,<reverse> or a single number for both");
    Optional<double[]> values = Optional.empty();
    while (!values.isPresent()) {
      String line = null;
      try {
        line = reader.readLine(prompt()).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        break;
      }

      if (line.isEmpty()) {
        logger.info("{}: no value entered", name());
        break;
      }

      List<String> entries = Arrays.asList(line.split(","));
      double[] doubles = new double[2];
      try {
        if (entries.size() > 0) {
          doubles[0] = Double.valueOf(entries.get(0));
        } else {
          throw new AssertionError("entries was zero-length");
        }
        if (entries.size() > 1) {
          doubles[1] = Double.valueOf(entries.get(1));
        } else {
          doubles[1] = doubles[0];
        }
      } catch (NumberFormatException nfe) {
        terminal.writer().println("please enter a number or two numbers separated by a commma");
        continue;
      }
      values = Optional.of(doubles);
    }
    return values;
  }

}
