package org.strykeforce.thirdcoast.telemetry.tct.dio;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Timer;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;

public class DemoPwmDigitalOutputCommand extends AbstractCommand {

  public final static String NAME = "Demo PWM Sweep";
  private final static double SLEEP_SEC = 1e-3;
  private final DioSet dioSet;

  @Inject
  public DemoPwmDigitalOutputCommand(LineReader reader, DioSet dioSet) {
    super(NAME, reader);
    this.dioSet = dioSet;
  }

  @Override
  public void perform() {
    terminal.writer().println(Messages.bold(NAME));
    terminal.writer().println();
    DigitalOutput digitalOutput = dioSet.getDigitalOutput();
    if (digitalOutput == null) {
      terminal.writer().println(Messages.boldRed("no digital output selected"));
      return;
    }
    digitalOutput.disablePWM();
    digitalOutput.enablePWM(0.25);
    Timer.delay(SLEEP_SEC);
    digitalOutput.updateDutyCycle(0.5);
    Timer.delay(SLEEP_SEC);
    digitalOutput.updateDutyCycle(0.75);
    Timer.delay(SLEEP_SEC);
    digitalOutput.updateDutyCycle(1.0);

  }

}
