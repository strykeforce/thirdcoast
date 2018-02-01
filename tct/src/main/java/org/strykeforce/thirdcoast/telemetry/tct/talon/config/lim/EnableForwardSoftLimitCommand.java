package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.config.SoftLimits;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractBooleanConfigCommand;

public class EnableForwardSoftLimitCommand extends AbstractBooleanConfigCommand {

  public static final String NAME = "Enable Forward Soft Limit";

  @Inject
  EnableForwardSoftLimitCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(ThirdCoastTalon talon, boolean value) {
    talon.configForwardSoftLimitEnable(value, TIMEOUT_MS);
  }

  @Override
  protected void saveConfig(boolean value) {
    SoftLimits limit = talonSet.talonConfigurationBuilder().getForwardSoftLimit();
    if (limit == null) {
      limit = SoftLimits.DEFAULT;
    }
    talonSet.talonConfigurationBuilder().setForwardSoftLimit(limit.copyWithEnabled(value));
  }
}
