package org.strykeforce.thirdcoast.telemetry.tct.command;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

public class MainCommand extends AbstractCommand {

  private final List<Command> subCommands = new ArrayList<>();

  @Inject
  public MainCommand(Terminal terminal) {
    super(terminal);
    subCommands.add(new StatusCommand(this.terminal));
  }


  static AttributedString aString() {
    AttributedStringBuilder builder = new AttributedStringBuilder();
    builder.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));
    builder.append("Hi There!");
    return builder.toAttributedString();
  }

  private AttributedString boldWhite(String string) {
    return new AttributedString(string,
        AttributedStyle.BOLD);
  }

  private void list() {
    PrintWriter writer = terminal.writer();
    writer.printf("%n%s%n", boldWhite("*** Commands ***").toAnsi());

  }

  @Override
  public void run() {
    LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();
    while (true) {
      String line = null;
      try {
        list();
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
      terminal.writer().println(aString().toAnsi(terminal));
      terminal.flush();
      subCommands.get(0).run();
    }

  }

}
