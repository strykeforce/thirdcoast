package org.strykeforce.thirdcoast.telemetry.tct.dio;

import edu.wpi.first.wpilibj.DigitalOutput;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;

public class PwmDigitalOutputCommand extends AbstractCommand {

  public static String NAME = "PWM Digital Output";
  private final DigitalOutputSet digitalOutputSet;

  @Inject
  public PwmDigitalOutputCommand(LineReader reader, DigitalOutputSet digitalOutputSet) {
    super(NAME, reader);
    this.digitalOutputSet = digitalOutputSet;
  }

  @Override
  public void perform() {
    if (digitalOutputSet.getDigitalOutput() == null) {
      terminal.writer().println(bold("no digital output selected selected"));
      return;
    }
    terminal.writer().println(bold("Enter duty cycle, press <enter> to go back"));
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
      DigitalOutput digitalOutput = digitalOutputSet.getDigitalOutput();
      digitalOutput.disablePWM();
      digitalOutput.enablePWM(setpoint);
    }

  }

  private void help() {
    terminal.writer().println(bold("please enter a number"));
  }
}
