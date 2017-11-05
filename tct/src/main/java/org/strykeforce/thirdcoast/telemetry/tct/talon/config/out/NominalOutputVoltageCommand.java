package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractFwdRevDoubleConfigCommand;

/**
 * Configure F.
 */
public class NominalOutputVoltageCommand extends AbstractFwdRevDoubleConfigCommand {

  public final static String NAME = "Nominal Output Voltage";

  @Inject
  public NominalOutputVoltageCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(CANTalon talon, double foward, double reverse) {
    talon.configNominalOutputVoltage(foward, reverse);
  }
}
