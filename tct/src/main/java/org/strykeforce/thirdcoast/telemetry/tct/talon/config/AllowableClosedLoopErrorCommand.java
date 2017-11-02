package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

/**
 * Configure F.
 */
public class AllowableClosedLoopErrorCommand extends IntConfigCommand {

  public final static String NAME = "Allowable Closed Loop Error";

  @Inject
  public AllowableClosedLoopErrorCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, terminal, talonSet);
  }

  @Override
  protected void config(CANTalon talon, int value) {
    talon.setAllowableClosedLoopErr(value);
  }
}
