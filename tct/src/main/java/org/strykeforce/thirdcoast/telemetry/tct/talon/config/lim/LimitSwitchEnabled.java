package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.config.LimitSwitches;
import org.strykeforce.thirdcoast.talon.TalonConfigurationBuilder;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractFwdRevBooleanConfigCommand;

public class LimitSwitchEnabled extends AbstractFwdRevBooleanConfigCommand {

  public static final String NAME = TODO + "Enable Limit Switches";

  @Inject
  LimitSwitchEnabled(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(ThirdCoastTalon talon, boolean forward, boolean reverse) {
    //    talon.overrideLimitSwitchesEnable(forward, timeout);
  }

  @Override
  protected void saveConfig(boolean forward, boolean reverse) {
    TalonConfigurationBuilder tcb = talonSet.talonConfigurationBuilder();
    LimitSwitches ls = talonSet.talonConfigurationBuilder().getForwardLimitSwitch();
    if (ls == null) {
      ls = LimitSwitches.DEFAULT;
    }
    tcb.setForwardLimitSwitch(ls.copyWithEnabled(forward));

    ls = talonSet.talonConfigurationBuilder().getReverseLimitSwitch();
    if (ls == null) {
      ls = LimitSwitches.DEFAULT;
    }
    tcb.setReverseLimitSwitch(ls.copyWithEnabled(reverse));
  }
}
