package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractFwdRevDoubleConfigCommand;

/** Configure F. */
public class PeakOutputVoltageCommand extends AbstractFwdRevDoubleConfigCommand {

  public static final String NAME = "Peak Ouput Voltage: Vfwd, Vrev";

  @Inject
  public PeakOutputVoltageCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet, true);
  }

  @Override
  protected void saveConfig(double forward, double reverse) {
    talonSet.talonConfigurationBuilder().outputVoltagePeak(forward, reverse);
  }

  @Override
  protected void config(TalonSRX talon, double foward, double reverse) {
    talon.configPeakOutputVoltage(foward, reverse);
  }
}
