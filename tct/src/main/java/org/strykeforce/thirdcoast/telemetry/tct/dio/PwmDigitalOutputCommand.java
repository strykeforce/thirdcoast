package org.strykeforce.thirdcoast.telemetry.tct.dio;

import edu.wpi.first.wpilibj.DigitalOutput;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;

public class PwmDigitalOutputCommand extends AbstractCommand {

  public static String NAME = "PWM Digital Output";
  private final DioSet dioSet;

  @Inject
  public PwmDigitalOutputCommand(LineReader reader, DioSet dioSet) {
    super(NAME, reader);
    this.dioSet = dioSet;
  }

  @Override
  public void perform() {
    if (dioSet.getDigitalOutput() == null) {
      terminal.writer().println(Messages.boldRed("no digital output selected selected"));
      return;
    }
    terminal.writer().println(Messages.bold("Enter duty cycle, press <enter> to go back"));
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
      DigitalOutput digitalOutput = dioSet.getDigitalOutput();
      digitalOutput.disablePWM();
      digitalOutput.enablePWM(setpoint);
    }

  }

  private void help() {
    terminal.writer().println(Messages.boldRed("please enter a number"));
  }
}
