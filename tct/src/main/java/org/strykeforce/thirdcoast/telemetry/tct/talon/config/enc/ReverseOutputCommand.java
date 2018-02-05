package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractBooleanConfigCommand;

public class ReverseOutputCommand extends AbstractBooleanConfigCommand {

  public static final String NAME = "Output Reversed";

  @Inject
  public ReverseOutputCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(boolean value) {
    talonSet.talonConfigurationBuilder().outputReversed(value);
  }

  @Override
  protected void config(ThirdCoastTalon talon, boolean value) {
    talon.setInverted(value);
  }
}
