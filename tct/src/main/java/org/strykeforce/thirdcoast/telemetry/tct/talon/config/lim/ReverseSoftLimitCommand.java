package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.SoftLimit;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand;

public class ReverseSoftLimitCommand extends AbstractDoubleConfigCommand {

  public static final String NAME = "Reverse Soft Limit";

  @Inject
  public ReverseSoftLimitCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(TalonSRX talon, double value) {
    talon.setReverseSoftLimit(value);
  }

  @Override
  protected void saveConfig(double value) {
    SoftLimit limit = talonSet.talonConfigurationBuilder().getReverseSoftLimit();
    if (limit == null) {
      limit = SoftLimit.DEFAULT;
    }
    talonSet.talonConfigurationBuilder().setReverseSoftLimit(limit.copyWithPosition(value));
  }
}
