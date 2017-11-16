package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

public abstract class AbstractBooleanConfigCommand extends AbstractTalonConfigCommand {

  public AbstractBooleanConfigCommand(String name, LineReader reader, TalonSet talonSet) {
    super(name, reader, talonSet);
  }

  protected abstract void config(CANTalon talon, boolean value);

  protected abstract void saveConfig(boolean value);

  @Override
  public void perform() {
    Boolean value = getBooleanValue();
    if (value == null) {
      return;
    }
    saveConfig(value);
    for (CANTalon talon : talonSet.selected()) {
      config(talon, value);
      logger.info("set {} for {} to {}", name(), talon.getDescription(), value);
    }
  }


  protected Boolean getBooleanValue() {
    Boolean value = null;

    while (value == null) {
      String line;
      try {
        line = reader.readLine(Messages.prompt("configure " + name() + " (Y/N)> ")).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        break;
      }

      if (line.isEmpty()) {
        logger.info("no value entered");
        break;
      }
      boolean setpoint;
      if (line.equalsIgnoreCase("Y")) {
        setpoint = true;
      } else if (line.equalsIgnoreCase("N")) {
        setpoint = false;
      } else {
        terminal.writer().println(Messages.boldRed("enter <Y>, <N> or <enter> to go back"));
        continue;
      }
      value = setpoint;
    }
    return value;
  }
}

