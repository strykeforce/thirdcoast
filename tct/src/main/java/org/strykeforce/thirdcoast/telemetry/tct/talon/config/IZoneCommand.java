package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import java.util.OptionalInt;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

/**
 * Configure F.
 */
public class IZoneCommand extends AbstactTalonConfigCommand {

  public final static String NAME = "I Zone";

  @Inject
  public IZoneCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, terminal, talonSet);
  }

  @Override
  public void perform() {
    OptionalInt opt = getIntValue();
    if (!opt.isPresent()) {
      return;
    }
    for (CANTalon talon : talonSet.selected()) {
      talon.setIZone(opt.getAsInt());
      logConfig(talon, talon.getIZone());
    }
  }
}
