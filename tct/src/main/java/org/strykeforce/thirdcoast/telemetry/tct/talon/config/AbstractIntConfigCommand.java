package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import java.util.OptionalInt;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

public abstract class AbstractIntConfigCommand extends AbstractTalonConfigCommand {

  public AbstractIntConfigCommand(String name, int weight, LineReader reader, TalonSet talonSet) {
    super(name, weight, reader, talonSet);
  }

  public AbstractIntConfigCommand(String name, LineReader reader, TalonSet talonSet) {
    super(name, reader, talonSet);
  }

  protected abstract void config(CANTalon talon, int value);

  protected abstract void saveConfig(int value);

  @Override
  public void perform() {
    Integer value = getIntValue();
    if (value == null) {
      return;
    }
    saveConfig(value);
    for (CANTalon talon : talonSet.selected()) {
      config(talon, value);
      logger.info("set {} for {} to {}", name(), talon.getDescription(), value);
    }
  }

  protected Integer getIntValue() {
    Integer value = null;

    while (value == null) {
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
      try {
        value = Integer.valueOf(line);
      } catch (NumberFormatException nfe) {
        terminal.writer().println("please enter an integer");
        continue;
      }
    }
    return value;
  }
}
