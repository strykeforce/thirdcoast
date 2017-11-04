package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import org.jline.reader.LineReader;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

public abstract class AbstractTalonConfigCommand extends AbstractCommand {

  protected final static Logger logger = LoggerFactory.getLogger(AbstractTalonConfigCommand.class);
  protected final TalonSet talonSet;

  protected AbstractTalonConfigCommand(String name, int weight, LineReader reader, TalonSet talonSet) {
    super(name, weight, reader);
    this.talonSet = talonSet;
  }

  protected AbstractTalonConfigCommand(String name, LineReader reader, TalonSet talonSet) {
    this(name, ConfigMenuModule.MENU_ORDER.indexOf(name), reader, talonSet);
  }

  protected String prompt() {
    return new AttributedStringBuilder()
        .style(AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
        .append("configure " + name() + "> ").toAnsi();
  }
}
