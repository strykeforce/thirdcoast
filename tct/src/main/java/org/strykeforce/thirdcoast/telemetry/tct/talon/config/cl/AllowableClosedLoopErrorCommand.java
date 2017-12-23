package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

/** Configure allowable closed-loop error. */
public class AllowableClosedLoopErrorCommand extends AbstractIntConfigCommand {

  public static final String NAME = "Allowable Closed Loop Error";

  @Inject
  public AllowableClosedLoopErrorCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(int value) {
    talonSet.talonConfigurationBuilder().allowableClosedLoopError(value);
  }

  @Override
  protected void config(CANTalon talon, int value) {
    talon.setAllowableClosedLoopErr(value);
  }
}
