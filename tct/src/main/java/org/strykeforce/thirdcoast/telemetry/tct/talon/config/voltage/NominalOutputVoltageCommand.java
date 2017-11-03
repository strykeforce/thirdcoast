package org.strykeforce.thirdcoast.telemetry.tct.talon.config.voltage;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.FwdRevDoubleConfigCommand;

/**
 * Configure F.
 */
public class NominalOutputVoltageCommand extends FwdRevDoubleConfigCommand {

  public final static String NAME = "Nominal Output Voltage";

  @Inject
  public NominalOutputVoltageCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, terminal, talonSet);
  }

  @Override
  protected void config(CANTalon talon, double foward, double reverse) {
    talon.configNominalOutputVoltage(foward, reverse);
  }
}
