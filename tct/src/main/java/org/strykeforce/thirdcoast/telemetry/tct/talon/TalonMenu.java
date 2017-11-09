package org.strykeforce.thirdcoast.telemetry.tct.talon;

import com.ctre.CANTalon;
import java.util.StringJoiner;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

public class TalonMenu extends Menu {

  private final TalonSet talonSet;

  @Inject
  public TalonMenu(CommandAdapter commandsAdapter, Terminal terminal, TalonSet talonSet) {
    super(commandsAdapter, terminal);
    this.talonSet = talonSet;
  }

  @Override
  protected String header() {
    StringJoiner joiner = new StringJoiner(", ");
    for (CANTalon talon : talonSet.selected()) {
      joiner.add(String.valueOf(talon.getDeviceID()));
    }
    return bold("Talons: " + joiner.toString() + "\n");
  }
}
