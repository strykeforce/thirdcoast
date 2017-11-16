package org.strykeforce.thirdcoast.telemetry.tct;

import edu.wpi.first.wpilibj.DriverStation;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 * Displays a menu of {@link Command} choices and performs action for selected choice.
 */
public class Menu {

  private final static String ENABLED = Messages.boldGreen("[enabled]");
  private final static String DISABLED = Messages.boldRed("[disabled]");

  private final Terminal terminal;
  private final LineReader reader;
  private final CommandAdapter commandsAdapter;
  private boolean mainMenu = false;

  @Inject
  public Menu(CommandAdapter commandsAdapter, LineReader reader) {
    this.commandsAdapter = commandsAdapter;
    this.reader = reader;
    this.terminal = reader.getTerminal();
  }

  private static String rightPrompt() {
    return DriverStation.getInstance().isEnabled() ? ENABLED : DISABLED;
  }


  protected String header() {
    return "";
  }

  public void display() {
    int menuCount = commandsAdapter.getCount();
    boolean help = false;
    while (true) {
      terminal.writer().print(header());
      for (int i = 0; i < menuCount; i++) {
        terminal.writer().printf(Messages.bold("%2d"), i + 1);
        terminal.writer().printf(" - %s%n", commandsAdapter.getMenuText(i));
      }
      if (help) {
        help();
        help = false;
      }
      String line = null;
      try {
        line = reader.readLine(Messages.prompt("select> "), rightPrompt(), (Character) null, null)
            .trim();
      } catch (EndOfFileException | UserInterruptException e) {
        if (!mainMenu) {
          return; // go up a menu level
        }
      }
      if (line == null || line.isEmpty()) { // redisplay menu and show help message
        help = true;
        continue;
      }

      if (!mainMenu && line.equalsIgnoreCase("B")) { // go up a menu level
        break;
      }

      int choice;
      try {
        choice = Integer.valueOf(line) - 1;
      } catch (NumberFormatException e) {
        choice = -1;
      }
      if (choice < 0 || choice >= menuCount) {
        help = true;
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
    terminal.writer().print(Messages.boldRed(msg));
  }
}
