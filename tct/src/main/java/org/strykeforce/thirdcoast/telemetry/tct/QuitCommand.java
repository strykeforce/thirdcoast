package org.strykeforce.thirdcoast.telemetry.tct;

import javax.inject.Inject;
import org.jline.reader.LineReader;

public class QuitCommand extends AbstractCommand {

  public final static String NAME = "Quit";

  @Inject
  public QuitCommand(LineReader reader) {
    super(NAME, reader);
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
