package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import java.util.OptionalDouble;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

/**
 * Configure P.
 */
public class DCommand extends AbstactTalonConfigCommand {

  public final static String NAME = "D";

  @Inject
  public DCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, terminal, talonSet);
  }

  @Override
  public void perform() {
    OptionalDouble opt = getDoubleValue();
    if (!opt.isPresent()) {
      return;
    }
    for (CANTalon talon : talonSet.selected()) {
      talon.setD(opt.getAsDouble());
      logConfig(talon, talon.getD());
    }
  }
}
