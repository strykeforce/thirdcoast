package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand;

/** Configure P. */
public class ClosedLoopRampRateCommand extends AbstractDoubleConfigCommand {

  public static final String NAME = "Closed Loop Ramp Rate";

  @Inject
  public ClosedLoopRampRateCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(double value) {
    talonSet.talonConfigurationBuilder().closedLoopRampRate(value);
  }

  @Override
  protected void config(CANTalon talon, double value) {
    talon.setCloseLoopRampRate(value);
  }
}
