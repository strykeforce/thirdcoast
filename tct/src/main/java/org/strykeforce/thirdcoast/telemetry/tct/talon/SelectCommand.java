package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Command;

/**
 * Select Talons to work with.
 */
@TalonScope
public class SelectCommand extends AbstractCommand {

  public final static String NAME = "Select Talons";
  final static Logger logger = LoggerFactory.getLogger(SelectCommand.class);
  private final TalonSet talonSet;
  private final LineReader reader;
  private final Optional<Command> listCommand;

  @Inject
  public SelectCommand(TalonSet talonSet, Terminal terminal, ListCommand listCommand) {
    super(NAME, TalonMenuModule.MENU_ORDER.indexOf(NAME), terminal);
    this.talonSet = talonSet;
    this.listCommand = Optional.of(listCommand);
    reader = LineReaderBuilder.builder().terminal(terminal).build();
  }

  @Override
  public void perform() {
    terminal.writer().println(bold("enter comma-separated list of Talon IDs"));
    String line = null;
    try {
      line = reader.readLine(boldYellow("talon ids> ")).trim();
    } catch (EndOfFileException | UserInterruptException e) {
      return;
    }

    talonSet.selected.clear();
    if (line.isEmpty()) {
      logger.info("no talons selected");
      return;
    }

    List<String> ids = Arrays.asList(line.split(","));
    for (String s : ids) {
      int id;
      try {
        id = Integer.valueOf(s);
      } catch (NumberFormatException e) {
        terminal.writer().print(bold(String.format("%s is not a number, ignoring%n", s)));
        continue;
      }

      for (CANTalon talon : talonSet.all) {
        if (talon.getDeviceID() == id) {
          talonSet.selected.add(talon);
          logger.info("added talon id {}", id);
          break;
        }
      }
    }
  }

  @Override
  public Optional<Command> post() {
    return listCommand;
  }
}
