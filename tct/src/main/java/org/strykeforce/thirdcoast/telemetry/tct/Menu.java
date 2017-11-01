package org.strykeforce.thirdcoast.telemetry.tct;

import edu.wpi.first.wpilibj.DriverStation;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 * Displays a menu of {@link Command} choices and
 * performs action for selected choice.
 */
public class Menu {

  private final static String ENABLED = new AttributedStringBuilder()
      .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)).append("[enabled]")
      .toAnsi();
  private final static String DISABLED = new AttributedStringBuilder()
      .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).append("[disabled]")
      .toAnsi();

  private final Terminal terminal;
  private final LineReader reader;
  private final CommandAdapter commandsAdapter;

  @Inject
  public Menu(CommandAdapter commandsAdapter, Terminal terminal) {
    this.commandsAdapter = commandsAdapter;
    this.terminal = terminal;
    reader = LineReaderBuilder.builder().terminal(terminal).build();
  }

  private String rightPrompt() {
    boolean enabled = DriverStation.getInstance().isEnabled();
    return enabled ? ENABLED : DISABLED;
  }

  public void display() {
    int menuCount = commandsAdapter.getCount();
    while (true) {
      for (int i = 0; i < menuCount; i++) {
        terminal.writer().printf("%d - %s%n", i+1, commandsAdapter.getMenuText(i));
      }
      String line = reader.readLine("> ", rightPrompt(), (Character) null, null).trim();
      if (line.isEmpty()) {
        break;
      }

      int choice;
      try {
        choice = Integer.valueOf(line) - 1;
      } catch (NumberFormatException e) {
        terminal.writer().println("please enter a number");
        continue;
      }
      if (choice < 0 || choice >= menuCount) {
        terminal.writer().printf("please enter a number between 1 - %d%n", menuCount);
        continue;
      }
      commandsAdapter.perform(choice);
    }
  }
}
