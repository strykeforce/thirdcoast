package org.strykeforce.thirdcoast.telemetry.tct.command;

import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

public class StatusCommand extends AbstractCommand {

  @Inject
  public StatusCommand(Terminal terminal) {
    super(terminal);
  }

  @Override
  public AttributedString prompt() {
    return new AttributedString("status:>",
        AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
  }

  @Override
  public void run() {
    LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();
    while (true) {
      String line = null;
      try {
        line = reader.readLine(prompt().toAnsi(terminal));
      } catch (UserInterruptException e) {
        // Ignore
      } catch (EndOfFileException e) {
        return;
      }

      if (line == null) {
        continue;
      }

      line = line.trim();

      terminal.writer().println("=> \"" + line + "\"");
      terminal.flush();
    }

  }}
