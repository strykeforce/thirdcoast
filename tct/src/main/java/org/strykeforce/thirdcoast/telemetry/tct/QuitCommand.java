package org.strykeforce.thirdcoast.telemetry.tct;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import org.jline.reader.LineReader;

@ParametersAreNonnullByDefault
public class QuitCommand extends AbstractCommand {

  public static final String NAME = "Quit";

  @Inject
  QuitCommand(LineReader reader) {
    super(NAME, reader);
  }

  @Override
  public void perform() {
    terminal.writer().println("Bye.");
    terminal.flush();
    System.exit(0);
  }
}
