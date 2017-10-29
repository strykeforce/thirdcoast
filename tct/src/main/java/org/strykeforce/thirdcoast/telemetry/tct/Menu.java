package org.strykeforce.thirdcoast.telemetry.tct;

import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;

public class Menu implements Runnable {

  private final Terminal terminal;
  private final LineReader reader;
  private final Prompts prompts;
  private final Talons talons;

  @Inject
  public Menu(Terminal terminal, Prompts prompts, Talons talons) {
    this.terminal = terminal;
    this.prompts = prompts;
    this.talons = talons;
    this.reader = LineReaderBuilder.builder().terminal(terminal).build();
  }

  @Override
  public void run() {
    display();
  }

//  public void refreshState() {
//    reader.callWidget(LineReader.REDRAW_LINE);
//    reader.callWidget(LineReader.REDRAW_LINE);
//    reader.callWidget(LineReader.REDISPLAY);
//  }

  public void display() {
    terminal.puts(Capability.clear_screen);
    terminal.flush();
    while (true) {
      terminal.writer().println("1 - load");
      terminal.writer().println("2 - list");
      terminal.writer().println("3 - select");
      terminal.writer().println("4 - run");
      terminal.writer().println("5 - quit");
      terminal.flush();

      String line = null;
      try {
        line = reader.readLine("> ", prompts.rightPrompt(), (Character) null, null).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        continue;
      }

      if (line.isEmpty()) {
        continue;
      }

      int choice = 0;
      try {
        choice = Integer.valueOf(line);
      } catch (NumberFormatException nfe) {
        terminal.writer().println("please enter a number");
        continue;
      }
      switch (choice) {
        case 1:
          talons.load();
          break;
        case 2:
          talons.list();
          break;
        case 3:
          talons.select();
          break;
        case 4:
          talons.start();
          break;
        case 5:
          System.exit(0);
          break;
        default:
          terminal.writer().println("nope!");
      }
    }
  }


}
