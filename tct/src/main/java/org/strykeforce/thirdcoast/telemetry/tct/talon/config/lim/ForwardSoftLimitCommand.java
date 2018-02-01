package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.config.SoftLimits;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class ForwardSoftLimitCommand extends AbstractIntConfigCommand {

  public static final String NAME = "Forward Soft Limit";

  @Inject
  public ForwardSoftLimitCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(ThirdCoastTalon talon, int value) {
    talon.configForwardSoftLimitThreshold(value, TIMEOUT_MS);
  }

  @Override
  protected void saveConfig(int value) {
    SoftLimits limit = talonSet.talonConfigurationBuilder().getForwardSoftLimit();
    if (limit == null) {
      limit = SoftLimits.DEFAULT;
    }
    talonSet.talonConfigurationBuilder().setForwardSoftLimit(limit.copyWithPosition(value));
  }
}
