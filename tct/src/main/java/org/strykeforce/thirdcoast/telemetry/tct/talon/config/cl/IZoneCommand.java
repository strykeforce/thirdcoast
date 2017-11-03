package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.IntConfigCommand;

/**
 * Configure F.
 */
public class IZoneCommand extends IntConfigCommand {

  public final static String NAME = "I Zone";

  @Inject
  public IZoneCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, ClosedLoopMenuModule.MENU_ORDER.indexOf(NAME), terminal, talonSet);
  }

  @Override
  protected void config(CANTalon talon, int value) {
    talon.setIZone(value);
  }
}
