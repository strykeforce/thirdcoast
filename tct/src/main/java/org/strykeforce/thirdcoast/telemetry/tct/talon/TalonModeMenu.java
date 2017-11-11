package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import java.util.StringJoiner;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;

public class TalonModeMenu extends Menu {

  private final TalonSet talonSet;

  @Inject
  public TalonModeMenu(CommandAdapter commandsAdapter, LineReader reader, TalonSet talonSet) {
    super(commandsAdapter, reader);
    this.talonSet = talonSet;
  }

  @Override
  protected String header() {
    StringJoiner joiner = new StringJoiner(", ");
    for (CANTalon talon : talonSet.selected()) {
      joiner.add(String.valueOf(talon.getDeviceID()));
    }
    return boldGreem("Talons: " + joiner.toString() + "\n");
  }
}
