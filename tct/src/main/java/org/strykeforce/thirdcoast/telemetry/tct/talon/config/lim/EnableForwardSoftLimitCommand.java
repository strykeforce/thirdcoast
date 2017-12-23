package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.SoftLimit;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractBooleanConfigCommand;

public class EnableForwardSoftLimitCommand extends AbstractBooleanConfigCommand {

  public static final String NAME = "Enable Forward Soft Limit";

  @Inject
  public EnableForwardSoftLimitCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(CANTalon talon, boolean value) {
    talon.enableForwardSoftLimit(value);
  }

  @Override
  protected void saveConfig(boolean value) {
    SoftLimit limit = talonSet.talonConfigurationBuilder().getForwardSoftLimit();
    if (limit == null) {
      limit = SoftLimit.DEFAULT;
    }
    talonSet.talonConfigurationBuilder().setForwardSoftLimit(limit.copyWithEnabled(value));
  }
}
