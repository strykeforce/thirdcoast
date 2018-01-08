package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractFwdRevDoubleConfigCommand;

/** Configure F. */
public class NominalOutputVoltageCommand extends AbstractFwdRevDoubleConfigCommand {

  public static final String NAME = "Nominal Output Voltage: Vfwd, Vrev";

  @Inject
  public NominalOutputVoltageCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet, true);
  }

  @Override
  protected void saveConfig(double forward, double reverse) {
    talonSet.talonConfigurationBuilder().outputVoltageNominal(forward, reverse);
  }

  @Override
  protected void config(TalonSRX talon, double foward, double reverse) {
    talon.configNominalOutputVoltage(foward, reverse);
  }
}
