package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import java.util.OptionalDouble;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

/**
 * Configure P.
 */
public class PCommand extends AbstactTalonConfigCommand {

  public final static String NAME = "P";

  @Inject
  public PCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, terminal, talonSet);
  }

  @Override
  public void perform() {
    OptionalDouble opt = getDoubleValue();
    if (!opt.isPresent()) {
      return;
    }
    for (CANTalon talon : talonSet.selected()) {
      talon.setP(opt.getAsDouble());
      logConfig(talon, talon.getP());
    }
  }
}
