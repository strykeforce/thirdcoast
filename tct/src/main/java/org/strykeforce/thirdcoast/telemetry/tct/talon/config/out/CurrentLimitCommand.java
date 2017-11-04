package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.IntConfigCommand;

/**
 * Configure F.
 */
public class CurrentLimitCommand extends IntConfigCommand {

  public final static String NAME = "Output Current Limit";

  @Inject
  public CurrentLimitCommand(TalonSet talonSet, LineReader reader) {
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
