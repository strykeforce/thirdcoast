package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.DoubleConfigCommand;

/**
 * Configure P.
 */
public class VoltageRampRateCommand extends DoubleConfigCommand {

  public final static String NAME = "Voltage Ramp Rate";

  @Inject
  public VoltageRampRateCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, terminal, talonSet);
  }

  @Override
  protected void config(CANTalon talon, double value) {
    talon.setVoltageRampRate(value);
  }
}
