package org.strykeforce.thirdcoast.telemetry.tct;

import edu.wpi.first.wpilibj.DriverStation;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 * Displays a menu of {@link Command} choices and performs action for selected choice.
 */
public class Menu {

  private final static String ENABLED = new AttributedStringBuilder()
      .style(AttributedStyle.BOLD.foreground(AttributedStyle.GREEN)).append("[enabled]")
      .toAnsi();
  private final static String DISABLED = new AttributedStringBuilder()
      .style(AttributedStyle.BOLD.foreground(AttributedStyle.RED)).append("[disabled]")
      .toAnsi();

  private final Terminal terminal;
  private final LineReader reader;
  private final CommandAdapter commandsAdapter;
  private boolean mainMenu = false;

  @Inject
  public Menu(CommandAdapter commandsAdapter, Terminal terminal) {
    this.commandsAdapter = commandsAdapter;
    this.terminal = terminal;
    reader = LineReaderBuilder.builder().terminal(terminal).build();
  }

  private static String bold(String text) {
    return new AttributedStringBuilder().style(AttributedStyle.BOLD).append(text).toAnsi();
  }

  private static String boldYellow(String text) {
    return new AttributedStringBuilder()
        .style(AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW)).append(text).toAnsi();
  }

  private static String rightPrompt() {
    boolean enabled = DriverStation.getInstance().isEnabled();
    return enabled ? ENABLED : DISABLED;
  }

  public void display() {
    int menuCount = commandsAdapter.getCount();
    while (true) {
      for (int i = 0; i < menuCount; i++) {
        terminal.writer().printf("%2d - %s%n", i + 1, commandsAdapter.getMenuText(i));
      }
      String line = reader.readLine(boldYellow("select> "), rightPrompt(), (Character) null, null)
          .trim();
      if (line.isEmpty()) {
        help();
        continue;
      }

      if (!mainMenu && line.equalsIgnoreCase("B")) {
        break;
      }

      int choice;
      try {
        choice = Integer.valueOf(line) - 1;
      } catch (NumberFormatException e) {
        choice = -1;
      }
      if (choice < 0 || choice >= menuCount) {
        help();
        continue;
      }
      commandsAdapter.perform(choice);
    }
  }

  public void setMainMenu(boolean mainMenu) {
    this.mainMenu = mainMenu;
  }

  private void help() {
    String msg = String.format("please enter a number between 1-%d%s%n", commandsAdapter.getCount(),
        mainMenu ? "" : " or <b> for back");
    terminal.writer().print(bold(msg));
  }
}
