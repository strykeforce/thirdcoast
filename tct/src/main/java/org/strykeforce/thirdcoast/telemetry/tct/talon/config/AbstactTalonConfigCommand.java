package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
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
        terminal.writer().println("please enter a number");
        continue;
      }
      value = OptionalDouble.of(setpoint);
    }

    return value;
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

  protected OptionalInt getIntValue() {
    OptionalInt value = OptionalInt.empty();

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
      int setpoint = 0;
      try {
        setpoint = Integer.valueOf(line);
      } catch (NumberFormatException nfe) {
        terminal.writer().println("please enter an integer");
        continue;
      }
      value = OptionalInt.of(setpoint);
    }

    return value;
  }

  protected String prompt() {
    return new AttributedStringBuilder()
        .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW))
        .append(name() + "> ").toAnsi();
  }

  protected void logConfig(CANTalon talon, double value) {
    logger.info("set {} for {} to {}", name(), talon.getDescription(), value);
  }

  protected void logFwdRevConfig(CANTalon talon, double[] fr) {
    logger.info("set {} for {} to {}/{}", name(), talon.getDescription(), fr[0], fr[1]);
  }
}
