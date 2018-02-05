package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand;

public class OpenLoopRampRateCommand extends AbstractDoubleConfigCommand {

  public static final String NAME = VERIFY + "Open Loop Ramp Rate";

  @Inject
  public OpenLoopRampRateCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(double value) {
    talonSet.talonConfigurationBuilder().voltageRampRate(value);
  }

  @Override
  protected void config(ThirdCoastTalon talon, double value) {
    talon.configOpenloopRamp(value, TIMEOUT_MS);
  }
}
