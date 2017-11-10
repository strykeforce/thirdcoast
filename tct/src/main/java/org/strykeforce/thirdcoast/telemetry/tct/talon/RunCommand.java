package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;

@ModeScoped
@ParametersAreNonnullByDefault
public class RunCommand extends AbstractCommand {

  public final static String NAME = "Run Selected Talons";
  private final TalonSet talonSet;

  @Inject
  RunCommand(TalonSet talonSet, LineReader reader) {
    super(NAME, reader);
    this.talonSet = talonSet;
  }

  @Override
  public void perform() {
    if (talonSet.selected().isEmpty()) {
      terminal.writer().println(bold("no talons selected"));
      return;
    }
    terminal.writer().println(bold("Enter motor setpoint, press <enter> to go back"));
    while (true) {
      String line;
      try {
        line = reader.readLine(boldYellow("setpoint> ")).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        continue;
      }

      if (line.isEmpty()) {
        return;
      }
      double setpoint;
      try {
        setpoint = Double.valueOf(line);
      } catch (NumberFormatException nfe) {
        terminal.writer().println(bold("please enter a number"));
        continue;
      }
      terminal.writer().print(bold(String.format("setting talons to %.2f%n", setpoint)));
      for (CANTalon talon : talonSet.selected()) {
        talon.set(setpoint);
      }
    }
  }
}
