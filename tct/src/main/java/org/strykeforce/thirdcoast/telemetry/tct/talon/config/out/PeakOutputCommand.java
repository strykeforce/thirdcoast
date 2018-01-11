package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractFwdRevDoubleConfigCommand;

public class PeakOutputCommand extends AbstractFwdRevDoubleConfigCommand {

  public static final String NAME = "Peak Output Percentage: fwd, rev";

  @Inject
  public PeakOutputCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet, true);
  }

  @Override
  protected void saveConfig(double forward, double reverse) {
    talonSet.talonConfigurationBuilder().outputVoltagePeak(forward, reverse);
  }

  @Override
  protected void config(ThirdCoastTalon talon, double forward, double reverse) {
    talon.configPeakOutputForward(forward, TIMEOUT_MS);
    talon.configPeakOutputReverse(reverse, TIMEOUT_MS);
  }
}
