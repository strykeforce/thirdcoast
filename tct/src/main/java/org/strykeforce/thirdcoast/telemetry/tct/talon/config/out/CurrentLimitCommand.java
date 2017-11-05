package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

/**
 * Configure F.
 */
public class CurrentLimitCommand extends AbstractIntConfigCommand {

  public final static String NAME = "Output Current Limit";

  @Inject
  public CurrentLimitCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, OutputMenuModule.MENU_ORDER.indexOf(NAME), reader, talonSet);
  }

  @Override
  protected void config(CANTalon talon, int value) {
    if (value > 0) {
      talon.setCurrentLimit(value);
      talon.EnableCurrentLimit(true);
      return;
    }
    talon.EnableCurrentLimit(false);
  }
}
