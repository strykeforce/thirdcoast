package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand;

public class ICommand extends AbstractDoubleConfigCommand {

  public static final String NAME = "I";

  @Inject
  public ICommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(double value) {
    talonSet.talonConfigurationBuilder().I(value);
  }

  @Override
  protected void config(ThirdCoastTalon talon, double value) {
    talon.config_kI(0, value, TIMEOUT_MS);
  }
}
