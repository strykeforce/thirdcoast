package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.ModeScope;

@ModeScope
public class RunCommand extends AbstractCommand {

  public final static String NAME = "Run Selected Talons";
  private final TalonSet talonSet;
  private final LineReader reader;

  @Inject
  public RunCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, TalonMenuModule.MENU_ORDER.indexOf(NAME), terminal);
    this.talonSet = talonSet;
    reader = LineReaderBuilder.builder().terminal(terminal).build();
  }

  @Override
  public void perform() {
    if (talonSet.selected.isEmpty()) {
      terminal.writer().println(bold("no talons selected"));
      return;
    }
    terminal.writer().println(bold("Enter motor setpoint, press <enter> to go back"));
    while (true) {
      String line = null;
      try {
        line = reader.readLine(boldYellow("setpoint> ")).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        continue;
      }

      if (line.isEmpty()) {
        return;
      }
      double setpoint = 0;
      try {
        setpoint = Double.valueOf(line);
      } catch (NumberFormatException nfe) {
        terminal.writer().println(bold("please enter a number"));
        continue;
      }
      terminal.writer().print(bold(String.format("setting talons to %.2f%n", setpoint)));
      for (CANTalon talon : talonSet.selected) {
        talon.set(setpoint);
      }
    }
  }
}
