package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX.StatusFrameRate;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class PulseWidthFrameRateCommand extends AbstractIntConfigCommand {

  public static final String NAME = "PulseWidth Status Frame Rate";

  @Inject
  public PulseWidthFrameRateCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(TalonSRX talon, int value) {
    talon.setStatusFrameRateMs(StatusFrameRate.PulseWidth, value);
  }

  @Override
  protected void saveConfig(int value) {}
}
