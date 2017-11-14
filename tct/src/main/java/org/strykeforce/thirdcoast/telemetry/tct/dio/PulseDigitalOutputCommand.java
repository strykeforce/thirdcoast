package org.strykeforce.thirdcoast.telemetry.tct.dio;

import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;

public class PulseDigitalOutputCommand extends AbstractCommand {

  public final static String NAME = "Pulse Selected Digital Output";
  private final DigitalOutputSet digitalOutputSet;

  @Inject
  public PulseDigitalOutputCommand(LineReader reader, DigitalOutputSet digitalOutputSet) {
    super(NAME, reader);
    this.digitalOutputSet = digitalOutputSet;
  }

  @Override
  public void perform() {
    if (digitalOutputSet.getDigitalOutput() == null) {
      terminal.writer().println(bold("no digital output selected selected"));
      return;
    }
    terminal.writer().println(bold("Enter pulse length, press <enter> to go back"));
    while (true) {
      String line;
      try {
        line = reader.readLine(boldYellow("number or <return> to exit> ")).trim();
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
      terminal.writer().print(bold(String.format("pulsing for %.2f%n", setpoint)));
      digitalOutputSet.getDigitalOutput().pulse(setpoint);
    }
  }

  private void help() {
    terminal.writer().println(bold("please enter a number"));
  }

}
