package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.DoubleConfigCommand;

/**
 * Configure P.
 */
public class DCommand extends DoubleConfigCommand {

  public final static String NAME = "D";

  @Inject
  public DCommand(TalonSet talonSet, LineReader reader) {
    super(NAME, ClosedLoopMenuModule.MENU_ORDER.indexOf(NAME), reader, talonSet);
  }

  @Override
  protected void config(CANTalon talon, double value) {
    talon.setD(value);
  }
}
