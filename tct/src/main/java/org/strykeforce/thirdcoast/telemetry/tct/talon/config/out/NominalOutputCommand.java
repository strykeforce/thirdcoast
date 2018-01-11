package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractFwdRevDoubleConfigCommand;

public class NominalOutputCommand extends AbstractFwdRevDoubleConfigCommand {

  public static final String NAME = "Nominal Percent Output: fwd, rev";

  @Inject
  public NominalOutputCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet, true);
  }

  @Override
  protected void saveConfig(double forward, double reverse) {
    talonSet.talonConfigurationBuilder().outputVoltageNominal(forward, reverse);
  }

  @Override
  protected void config(ThirdCoastTalon talon, double foward, double reverse) {
    talon.configNominalOutputForward(foward, TIMEOUT_MS);
    talon.configNominalOutputReverse(reverse, TIMEOUT_MS);
  }
}
