package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.IntConfigCommand;

/**
 * Configure F.
 */
public class AllowableClosedLoopErrorCommand extends IntConfigCommand {

  public final static String NAME = "Allowable Closed Loop Error";

  @Inject
  public AllowableClosedLoopErrorCommand(TalonSet talonSet, LineReader reader) {
    super(NAME, ClosedLoopMenuModule.MENU_ORDER.indexOf(NAME), reader, talonSet);
  }

  @Override
  protected void config(CANTalon talon, int value) {
    talon.setAllowableClosedLoopErr(value);
  }
}
