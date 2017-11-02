package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

/**
 * Configure P.
 */
public class DCommand extends DoubleConfigCommand {

  public final static String NAME = "D";

  @Inject
  public DCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, terminal, talonSet);
  }

  @Override
  protected void config(CANTalon talon, double value) {
    talon.setD(value);
  }
}
