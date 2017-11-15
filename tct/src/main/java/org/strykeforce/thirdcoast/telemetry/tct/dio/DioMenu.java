package org.strykeforce.thirdcoast.telemetry.tct.dio;

import edu.wpi.first.wpilibj.DigitalOutput;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;

public class DioMenu extends Menu {

  private final DigitalOutputSet digitalOutputSet;

  public DioMenu(CommandAdapter commandsAdapter, LineReader reader,
      DigitalOutputSet digitalOutputSet) {
    super(commandsAdapter, reader);
    this.digitalOutputSet = digitalOutputSet;
  }

  @Override
  protected String header() {
    DigitalOutput digitalOutput = digitalOutputSet.getDigitalOutput();
    String id = digitalOutput != null ? String.valueOf(digitalOutput.getChannel()) : "";
    return boldGreem("Digital Output: " + id + "\n");
  }
}
