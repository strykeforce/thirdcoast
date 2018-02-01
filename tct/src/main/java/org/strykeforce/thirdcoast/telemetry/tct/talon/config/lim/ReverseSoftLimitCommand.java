package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.config.SoftLimits;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class ReverseSoftLimitCommand extends AbstractIntConfigCommand {

  public static final String NAME = "Reverse Soft Limit";

  @Inject
  public ReverseSoftLimitCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(ThirdCoastTalon talon, int value) {
    talon.configReverseSoftLimitThreshold(value, TIMEOUT_MS);
  }

  @Override
  protected void saveConfig(int value) {
    SoftLimits limit = talonSet.talonConfigurationBuilder().getReverseSoftLimit();
    if (limit == null) {
      limit = SoftLimits.DEFAULT;
    }
    talonSet.talonConfigurationBuilder().setReverseSoftLimit(limit.copyWithPosition(value));
  }
}
