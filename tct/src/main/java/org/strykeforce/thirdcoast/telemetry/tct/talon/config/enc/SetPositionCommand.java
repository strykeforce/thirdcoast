package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class SetPositionCommand extends AbstractIntConfigCommand {

  public static final String NAME = "Set Selected Sensor Position";

  @Inject
  public SetPositionCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(int value) {
    // not a configuration value
  }

  @Override
  protected void config(ThirdCoastTalon talon, int value) {
    talon.setSelectedSensorPosition(value, 0, TIMEOUT_MS);
  }
}
