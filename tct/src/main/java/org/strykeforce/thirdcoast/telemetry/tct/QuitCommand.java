package org.strykeforce.thirdcoast.telemetry.tct;

import javax.inject.Inject;
import org.jline.terminal.Terminal;

public class QuitCommand extends AbstractCommand {

  public final static String NAME = "Quit";

  @Inject
  public QuitCommand(Terminal terminal) {
    super(NAME, terminal);
  }

  @Override
  public int weight() {
    return 100; // push to bottom of menu
  }

  @Override
  public void perform() {
    terminal.writer().println("Bye.");
    terminal.flush();
    System.exit(0);
  }
}
