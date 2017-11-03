package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.DoubleConfigCommand;

/**
 * Configure I.
 */
public class ICommand extends DoubleConfigCommand {

  public final static String NAME = "I";

  @Inject
  public ICommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, ClosedLoopMenuModule.MENU_ORDER.indexOf(NAME), terminal, talonSet);
  }

  @Override
  protected void config(CANTalon talon, double value) {
    talon.setI(value);
  }
}
