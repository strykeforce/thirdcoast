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

abstract class ConfigCommand extends AbstractCommand {

  final static Logger logger = LoggerFactory.getLogger(ConfigCommand.class);
  protected final TalonSet talonSet;
  protected final LineReader reader;

  protected ConfigCommand(String name, int weight, Terminal terminal,
      TalonSet talonSet) {
    super(name, weight, terminal);
    this.talonSet = talonSet;
    reader = LineReaderBuilder.builder().terminal(terminal).build();
  }

  protected ConfigCommand(String name, Terminal terminal, TalonSet talonSet) {
    this(name, ConfigMenuModule.MENU_ORDER.indexOf(name), terminal, talonSet);
  }



  protected String prompt() {
    return new AttributedStringBuilder()
        .style(AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
        .append("configure " + name() + "> ").toAnsi();
  }
}
