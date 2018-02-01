package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand;

/** Configure F. */
public class FCommand extends AbstractDoubleConfigCommand {

  public static final String NAME = "F";

  @Inject
  public FCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(double value) {
    talonSet.talonConfigurationBuilder().F(value);
  }

  @Override
  protected void config(ThirdCoastTalon talon, double value) {
    talon.config_kF(0, value, TIMEOUT_MS);
  }
}
