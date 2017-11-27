package org.strykeforce.thirdcoast.telemetry.tct.dio;

import edu.wpi.first.wpilibj.DigitalInput;
import java.io.PrintWriter;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;

public class InspectDigitalInputsCommand extends AbstractCommand {

  public static String NAME = "Inspect Digital Inputs";
  private final TelemetryService telemetryService;
  private final DioSet dioSet;

  @Inject
  public InspectDigitalInputsCommand(LineReader reader, TelemetryService telemetryService,
      DioSet dioSet) {
    super(NAME, reader);
    this.telemetryService = telemetryService;
    this.dioSet = dioSet;
  }

  @Override
  public void perform() {
    PrintWriter writer = terminal.writer();
    writer.println(Messages.bold("Digital Input States"));
    for (int i = 0; i < 10; i++) {
      writer.print(Messages.bold(String.format("%2d  ", i)));
      DigitalInput input = dioSet.getDigitalInputs().get(i);
      if (input != null) {
        writer.println(input.get() ? "OFF" : Messages.boldGreen(" ON"));
      } else {
        writer.println("OUTPUT");
      }
    }


  }
}
