package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import java.util.Arrays;
import java.util.List;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

public abstract class AbstractFwdRevBooleanConfigCommand extends AbstractTalonConfigCommand {

  public AbstractFwdRevBooleanConfigCommand(String name, LineReader reader, TalonSet talonSet) {
    super(name, reader, talonSet);
  }

  protected abstract void config(ThirdCoastTalon talon, boolean foward, boolean reverse);

  protected abstract void saveConfig(boolean forward, boolean reverse);

  @Override
  public void perform() {
    boolean[] values = getFwdRevBooleans();
    if (values == null) {
      return;
    }
    saveConfig(values[0], values[1]);
    for (ThirdCoastTalon talon : talonSet.selected()) {
      config(talon, values[0], values[1]);
      logger.info("set {} for {} to {}/{}", name(), talon.getDescription(), values[0], values[1]);
    }
  }

  private boolean[] getFwdRevBooleans() {
    terminal
        .writer()
        .println(Messages.bold("\nenter <Y>/<N> for forward,reverse or a single value for both"));
    boolean[] values = null;
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
      boolean[] booleans = new boolean[2];
      try {
        if (entries.size() > 0) {
          booleans[0] = fromYN(entries.get(0));
        } else {
          help();
          continue;
        }
        if (entries.size() > 1) {
          booleans[1] = fromYN(entries.get(1));
        } else {
          booleans[1] = booleans[0];
        }
      } catch (IllegalArgumentException e) {
        help();
        continue;
      }
      values = booleans;
    }
    return values;
  }

  private boolean fromYN(String in) {
    boolean setpoint;
    if (in.equalsIgnoreCase("Y")) {
      setpoint = true;
    } else if (in.equalsIgnoreCase("N")) {
      setpoint = false;
    } else {
      throw new IllegalArgumentException();
    }
    return setpoint;
  }

  private void help() {
    terminal
        .writer()
        .println(Messages.boldRed("please enter <Y>, <N> or two values separated by a commma"));
  }
}
