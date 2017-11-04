package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import java.io.PrintWriter;
import java.util.OptionalDouble;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

public abstract class AbstractDoubleConfigCommand extends AbstractTalonConfigCommand {

  public AbstractDoubleConfigCommand(String name, int weight, LineReader reader, TalonSet talonSet) {
    super(name, weight, reader, talonSet);
  }

  public AbstractDoubleConfigCommand(String name, LineReader reader, TalonSet talonSet) {
    super(name, reader, talonSet);
  }

  protected abstract void config(CANTalon talon, double value);

  @Override
  public void perform() {
    OptionalDouble opt = getDoubleValue();
    if (!opt.isPresent()) {
      return;
    }
    double value = opt.getAsDouble();
    for (CANTalon talon : talonSet.selected()) {
      config(talon, value);
      logger.info("set {} for {} to {}", name(), talon.getDescription(), value);
    }
  }

  protected OptionalDouble getDoubleValue() {
    OptionalDouble value = OptionalDouble.empty();

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
      double setpoint = 0;
      try {
        setpoint = Double.valueOf(line);
      } catch (NumberFormatException nfe) {
        PrintWriter writer = terminal.writer();
        writer.println("please enter a number");
        continue;
      }
      value = OptionalDouble.of(setpoint);
    }
    return value;
  }

}
