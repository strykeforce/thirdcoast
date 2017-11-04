package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import java.util.Optional;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

public abstract class BooleanConfigCommand extends TalonConfigCommand {

  public BooleanConfigCommand(String name, int weight, LineReader reader, TalonSet talonSet) {
    super(name, weight, reader, talonSet);
  }

  public BooleanConfigCommand(String name, LineReader reader, TalonSet talonSet) {
    this(name, 0, reader, talonSet);
  }

  protected abstract void config(CANTalon talon, boolean value);

  @Override
  public void perform() {
    Optional<Boolean> opt = getBooleanValue();
    if (!opt.isPresent()) {
      return;
    }
    boolean value = opt.get().booleanValue();
    for (CANTalon talon : talonSet.selected()) {
      config(talon, value);
      logger.info("set {} for {} to {}", name(), talon.getDescription(), value);
    }
  }


  protected Optional<Boolean> getBooleanValue() {
    Optional<Boolean> value = Optional.empty();

    while (!value.isPresent()) {
      String line = null;
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
      value = Optional.of(setpoint);
    }
    return value;
  }

  @Override
  protected String prompt() {
    return new AttributedStringBuilder()
        .style(AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
        .append("configure " + name() + " (Y/N)> ")
        .toAnsi();
  }
}

