package org.strykeforce.thirdcoast.telemetry.app.command;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

public abstract class AbstractCommand implements Command {

  protected Terminal terminal;

  public AbstractCommand(Terminal terminal) {
    this.terminal = terminal;
  }

  @Override
  public AttributedString prompt() {
    return new AttributedString("tct:>",
        AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
  }


}
