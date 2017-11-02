package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

/**
 * Configure F.
 */
public class FCommand extends DoubleConfigCommand {

  public final static String NAME = "F";

  @Inject
  public FCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, terminal, talonSet);
  }

  @Override
  protected void config(CANTalon talon, double value) {
    talon.setF(value);
  }
}
