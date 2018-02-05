package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.config.SoftLimits;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractBooleanConfigCommand;

public class EnableReverseSoftLimitCommand extends AbstractBooleanConfigCommand {

  public static final String NAME = "Enable Reverse Soft Limit";

  @Inject
  public EnableReverseSoftLimitCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(ThirdCoastTalon talon, boolean value) {
    talon.configReverseSoftLimitEnable(value, TIMEOUT_MS);
  }

  @Override
  protected void saveConfig(boolean value) {
    SoftLimits limit = talonSet.talonConfigurationBuilder().getReverseSoftLimit();
    if (limit == null) {
      limit = SoftLimits.DEFAULT;
    }
    talonSet.talonConfigurationBuilder().setReverseSoftLimit(limit.copyWithEnabled(value));
  }
}
