package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

/** Configure F. */
public class CurrentLimitCommand extends AbstractIntConfigCommand {

  public static final String NAME = "Output Current Limit";

  @Inject
  public CurrentLimitCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(int value) {
    talonSet.talonConfigurationBuilder().currentLimit(value);
  }

  @Override
  protected void config(TalonSRX talon, int value) {
    if (value > 0) {
      talon.setCurrentLimit(value);
      talon.EnableCurrentLimit(true);
      return;
    }
    talon.EnableCurrentLimit(false);
  }
}
