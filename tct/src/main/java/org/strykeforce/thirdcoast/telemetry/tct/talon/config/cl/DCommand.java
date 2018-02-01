package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand;

public class DCommand extends AbstractDoubleConfigCommand {

  public static final String NAME = "D";

  @Inject
  public DCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(double value) {
    talonSet.talonConfigurationBuilder().D(value);
  }

  @Override
  protected void config(ThirdCoastTalon talon, double value) {
    talon.config_kD(0, value, TIMEOUT_MS);
  }
}
