package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.LimitSwitch;
import org.strykeforce.thirdcoast.talon.TalonConfigurationBuilder;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractFwdRevBooleanConfigCommand;

public class LimitSwitchEnabled extends AbstractFwdRevBooleanConfigCommand {

  public static final String NAME = "Enable Limit Switches";

  @Inject
  public LimitSwitchEnabled(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(CANTalon talon, boolean forward, boolean reverse) {
    talon.enableLimitSwitch(forward, reverse);
  }

  @Override
  protected void saveConfig(boolean forward, boolean reverse) {
    TalonConfigurationBuilder tcb = talonSet.talonConfigurationBuilder();
    LimitSwitch ls = talonSet.talonConfigurationBuilder().getForwardLimitSwitch();
    if (ls == null) {
      ls = LimitSwitch.DEFAULT;
    }
    tcb.setForwardLimitSwitch(ls.copyWithEnabled(forward));

    ls = talonSet.talonConfigurationBuilder().getReverseLimitSwitch();
    if (ls == null) {
      ls = LimitSwitch.DEFAULT;
    }
    tcb.setReverseLimitSwitch(ls.copyWithEnabled(reverse));
  }
}
