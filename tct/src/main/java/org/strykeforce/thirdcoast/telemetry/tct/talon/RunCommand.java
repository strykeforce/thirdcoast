package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Timer;
import java.util.Arrays;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;

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
      terminal.writer().println(Messages.NO_TALONS);
      return;
    }
    terminal.writer().println(Messages.bold("enter motor setpoint, press <enter> to go back"));
    terminal.writer().println(Messages.bold("optionally, enter setpoint, duration to pulse"));
    while (true) {
      String line;
      try {
        line = reader.readLine(Messages.prompt("setpoint or <return> or b/B to exit> ")).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        continue;
      }

      if (line.isEmpty() || line.equalsIgnoreCase("b")) {
        return;
      }
      double setpoint;
      double duration;
      List<String> entries = Arrays.asList(line.split(","));
      try {
        if (entries.size() > 0) {
          setpoint = Double.valueOf(entries.get(0));
        } else {
          help();
          continue;
        }
        duration = entries.size() > 1 ? Double.valueOf(entries.get(1)) : -1;
      } catch (NumberFormatException nfe) {
        help();
        continue;
      }

      if (duration > 0) {
        terminal.writer().print(Messages
            .bold(String.format("setting talons to %.2f for %.2f sec%n", setpoint, duration)));
        terminal.flush();
        double old = talonSet.selected().stream().findFirst().map(CANTalon::get).orElse(0.0);
        setTalons(setpoint);
        Timer.delay(duration);
        setTalons(old);
      } else {
        terminal.writer().print(Messages.bold(String.format("setting talons to %.2f%n", setpoint)));
        terminal.flush();
        setTalons(setpoint);
      }
    }
  }

  protected void setTalons(double setpoint) {
    for (CANTalon talon : talonSet.selected()) {
      talon.set(setpoint);
    }
  }

  protected void help() {
    terminal.writer()
        .println(Messages.boldRed("please enter a number or two numbers separated by a commma"));
  }

}
