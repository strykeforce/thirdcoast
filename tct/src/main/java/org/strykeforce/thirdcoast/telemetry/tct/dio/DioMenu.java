package org.strykeforce.thirdcoast.telemetry.tct.dio;

import edu.wpi.first.wpilibj.DigitalOutput;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;

public class DioMenu extends Menu {

  private final DioSet dioSet;

  public DioMenu(CommandAdapter commandsAdapter, LineReader reader,
      DioSet dioSet) {
    super(commandsAdapter, reader);
    this.dioSet = dioSet;
  }

  @Override
  protected String header() {
    DigitalOutput digitalOutput = dioSet.getDigitalOutput();
    String id = digitalOutput != null ? String.valueOf(digitalOutput.getChannel()) : "";
    return Messages.boldGreen("Digital Output: " + id + "\n");
  }
}
