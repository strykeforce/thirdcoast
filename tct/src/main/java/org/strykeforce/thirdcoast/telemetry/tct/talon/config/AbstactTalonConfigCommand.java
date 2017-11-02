package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import java.util.OptionalDouble;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

abstract class AbstactTalonConfigCommand extends AbstractCommand {

  final static Logger logger = LoggerFactory.getLogger(AbstactTalonConfigCommand.class);
  protected final TalonSet talonSet;
  protected final LineReader reader;

  protected AbstactTalonConfigCommand(String name, int weight, Terminal terminal,
      TalonSet talonSet) {
    super(name, weight, terminal);
    this.talonSet = talonSet;
    reader = LineReaderBuilder.builder().terminal(terminal).build();
  }

  protected AbstactTalonConfigCommand(String name, Terminal terminal, TalonSet talonSet) {
    this(name, ConfigMenuModule.MENU_ORDER.indexOf(name), terminal, talonSet);
  }

  protected OptionalDouble getDoubleValue() {
    OptionalDouble value = OptionalDouble.empty();

    while (!value.isPresent()) {
      String line = null;
      try {
        line = reader.readLine(name() + "> ").trim();
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
        terminal.writer().println("please enter a number");
        continue;
      }
      value = OptionalDouble.of(setpoint);
    }

    return value;
  }
  protected void logConfig(CANTalon talon, double value) {
    logger.info("set {} for {} to {}", name(), talon.getDescription(), value);
  }

}
