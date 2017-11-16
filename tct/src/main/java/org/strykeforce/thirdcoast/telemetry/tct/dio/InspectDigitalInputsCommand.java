package org.strykeforce.thirdcoast.telemetry.tct.dio;

import edu.wpi.first.wpilibj.DigitalInput;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.DigitalInputItem;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;

public class InspectDigitalInputsCommand extends AbstractCommand {

  public static String NAME = "Inspect Digital Inputs";
  private final TelemetryService telemetryService;
  private final List<DigitalInput> inputs = new ArrayList<>();

  @Inject
  public InspectDigitalInputsCommand(LineReader reader, TelemetryService telemetryService) {
    super(NAME, reader);
    this.telemetryService = telemetryService;
  }

  @Override
  public void perform() {
    if (inputs.size() == 0) {
      telemetryService.stop();
      for (int i = 0; i < 10; i++) {
        DigitalInput input = new DigitalInput(i);
        inputs.add(input);
        telemetryService.register(new DigitalInputItem(input));
      }
      telemetryService.start();
    }

    PrintWriter writer = terminal.writer();
    writer.println(Messages.bold("Digital Input States"));
    for (int i = 0; i < 10; i++) {
      writer.print(Messages.bold(String.format("%2d  ", i)));
      writer.println(inputs.get(i).get() ? "OFF" : Messages.boldGreen(" ON"));
    }
  }
}
