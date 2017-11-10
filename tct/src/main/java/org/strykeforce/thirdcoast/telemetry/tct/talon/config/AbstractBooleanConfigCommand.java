package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
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
        line = reader.readLine(prompt()).trim();
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
        terminal.writer().println(bold("enter <Y>, <N> or <enter> to go back"));
        continue;
      }
      value = setpoint;
    }
    return value;
  }

  @Override
  protected String prompt() {
    return new AttributedStringBuilder()
        .style(AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
        .append("configure ").append(name()).append(" (Y/N)> ")
        .toAnsi();
  }
}

