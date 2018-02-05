package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import java.util.Arrays;
import java.util.List;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

public abstract class AbstractFwdRevDoubleConfigCommand extends AbstractTalonConfigCommand {

  private final boolean flipReverse;

  public AbstractFwdRevDoubleConfigCommand(
      String name, LineReader reader, TalonSet talonSet, boolean flipReverse) {
    super(name, reader, talonSet);
    this.flipReverse = flipReverse;
  }

  // FIXME
  //  public AbstractFwdRevDoubleConfigCommand(String name, LineReader reader, TalonSet talonSet) {
  //    this(name, reader, talonSet, false);
  //  }

  protected abstract void config(ThirdCoastTalon talon, double foward, double reverse);

  protected abstract void saveConfig(double forward, double reverse);

  @Override
  public void perform() {
    double[] values = getFwdRevDoubles();
    if (values == null) {
      return;
    }
    saveConfig(values[0], values[1]);
    for (ThirdCoastTalon talon : talonSet.selected()) {
      config(talon, values[0], values[1]);
      logger.info("set {} for {} to {}/{}", name(), talon.getDescription(), values[0], values[1]);
    }
  }

  private double[] getFwdRevDoubles() {
    terminal
        .writer()
        .println(Messages.bold("\nenter <forward>,<reverse> or a single number for both"));
    double[] values = null;
    while (values == null) {
      String line;
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
          help();
          continue;
        }
        if (entries.size() > 1) {
          doubles[1] = Double.valueOf(entries.get(1));
        } else {
          doubles[1] = doubles[0] * (flipReverse ? -1 : 1);
        }
      } catch (NumberFormatException nfe) {
        help();
        continue;
      }
      values = doubles;
    }
    return values;
  }

  private void help() {
    terminal
        .writer()
        .println(Messages.boldRed("please enter a number or two numbers separated by a commma"));
  }
}
