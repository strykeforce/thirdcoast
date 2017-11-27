package org.strykeforce.thirdcoast.telemetry.tct.dio;

import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;

public class PulseDigitalOutputCommand extends AbstractCommand {

  public final static String NAME = "Pulse Selected Digital Output";
  private final DioSet dioSet;

  @Inject
  public PulseDigitalOutputCommand(LineReader reader, DioSet dioSet) {
    super(NAME, reader);
    this.dioSet = dioSet;
  }

  @Override
  public void perform() {
    if (dioSet.getDigitalOutput() == null) {
      terminal.writer().println(Messages.boldRed("no digital output selected selected"));
      return;
    }
    terminal.writer().println(Messages.bold("Enter pulse length, press <enter> to go back"));
    while (true) {
      String line;
      try {
        line = reader.readLine(Messages.prompt("number or <return> to exit> ")).trim();
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
        help();
        continue;
      }
      terminal.writer().print(Messages.bold(String.format("pulsing for %.2f%n", setpoint)));
      dioSet.getDigitalOutput().pulse(setpoint);
    }
  }

  private void help() {
    terminal.writer().println(Messages.boldRed("please enter a number"));
  }

}
