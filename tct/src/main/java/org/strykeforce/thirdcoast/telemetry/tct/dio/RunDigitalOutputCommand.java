package org.strykeforce.thirdcoast.telemetry.tct.dio;

import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;

public class RunDigitalOutputCommand extends AbstractCommand {

  public final static String NAME = "Set Selected Digital Output";
  private final DioSet dioSet;

  @Inject
  public RunDigitalOutputCommand(LineReader reader, DioSet dioSet) {
    super(NAME, reader);
    this.dioSet = dioSet;
  }

  @Override
  public void perform() {
    if (dioSet.getDigitalOutput() == null) {
      terminal.writer().println(Messages.boldRed("no digital output selected selected"));
      return;
    }
    terminal.writer().println(Messages.bold("Enter 0 for off or 1 for on, press <enter> to go back"));
    while (true) {
      String line;
      try {
        line = reader.readLine(Messages.prompt("0/1 or <return> to exit> ")).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        continue;
      }

      if (line.isEmpty()) {
        return;
      }
      int setpoint;
      try {
        setpoint = Integer.valueOf(line);
      } catch (NumberFormatException nfe) {
        help();
        continue;
      }
      if (setpoint != 0 && setpoint != 1) {
        help();
        continue;
      }
      terminal.writer().print(Messages.bold(String.format("setting dio to %d%n", setpoint)));
      dioSet.getDigitalOutput().set(setpoint == 1);
    }
  }

  private void help() {
    terminal.writer().println(Messages.boldRed("please enter 0 or 1"));
  }
}

