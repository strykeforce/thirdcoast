package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractFwdRevDoubleConfigCommand;

/**
 * Configure F.
 */
public class PeakOutputVoltageCommand extends AbstractFwdRevDoubleConfigCommand {

  public final static String NAME = "Peak Ouput Voltage";

  @Inject
  public PeakOutputVoltageCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(double forward, double reverse) {
//    talonSet.talonConfigurationBuilder().peakOutputVoltage(forward, reverse);
    // FIXME: not in TalonConfigurationBuilder
    terminal.writer().println(boldYellow("not implemented"));
  }

  @Override
  protected void config(CANTalon talon, double foward, double reverse) {
    talon.configPeakOutputVoltage(foward, reverse);
  }
}
