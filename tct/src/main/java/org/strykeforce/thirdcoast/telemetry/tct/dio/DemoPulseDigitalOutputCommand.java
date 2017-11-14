package org.strykeforce.thirdcoast.telemetry.tct.dio;

import edu.wpi.first.wpilibj.DigitalOutput;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;

public class DemoPulseDigitalOutputCommand extends AbstractCommand {

  public final static String NAME = "Demo Pulse Chain";
  private final DigitalOutputSet digitalOutputSet;

  @Inject
  public DemoPulseDigitalOutputCommand(LineReader reader, DigitalOutputSet digitalOutputSet) {
    super(NAME, reader);
    this.digitalOutputSet = digitalOutputSet;
  }

  @Override
  public void perform() {
    terminal.writer().println(bold(NAME));
    terminal.writer().println("pulseLength = 0.25, pulse width = 144 µsec");
    terminal.writer().println("pulseLength = 0.50, pulse width =  32 µsec");
    terminal.writer().println("pulseLength = 1.00, pulse width =  64 µsec");
    terminal.writer().println("pulseLength = 2.00, pulse width = 128 µsec");
    terminal.writer().println("pulseLength = 4.00, pulse width = 256 µsec");
    terminal.writer().println("pulseLength = 8.00, pulse width = 256 µsec");
    terminal.writer().println();
    DigitalOutput digitalOutput = digitalOutputSet.getDigitalOutput();
    if (digitalOutput == null) {
      terminal.writer().println(bold("no digital output selected selected"));
      return;
    }
    pulse(digitalOutput,0.25);
    pulse(digitalOutput,0.5);
    pulse(digitalOutput,1);
    pulse(digitalOutput,2);
    pulse(digitalOutput,4);
    pulse(digitalOutput,8);
  }

  private static void pulse(DigitalOutput digitalOutput, double length) {
    while (digitalOutput.isPulsing()) {
      try {
        Thread.sleep(0,500_000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    digitalOutput.pulse(length);
  }
}
