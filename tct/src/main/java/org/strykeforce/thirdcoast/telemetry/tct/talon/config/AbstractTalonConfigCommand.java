package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import org.jline.reader.LineReader;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

public abstract class AbstractTalonConfigCommand extends AbstractCommand {

  protected static final String TODO = "TODO FOR 2018 - ";
  protected static final String UNAVAILABLE = "UNAVAILABLE FOR 2018 - ";
  public static final String VERIFY = "VERIFY FOR 2018 - ";
  public static final int TIMEOUT_MS = 10;

  protected static final Logger logger = LoggerFactory.getLogger(AbstractTalonConfigCommand.class);
  protected final TalonSet talonSet;

  protected AbstractTalonConfigCommand(String name, LineReader reader, TalonSet talonSet) {
    super(name, reader);
    this.talonSet = talonSet;
  }

  protected String prompt() {
    return new AttributedStringBuilder()
        .style(AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
        .append("configure ")
        .append(name())
        .append("> ")
        .toAnsi();
  }
}
