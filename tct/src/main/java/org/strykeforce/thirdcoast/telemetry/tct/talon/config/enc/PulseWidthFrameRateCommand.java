package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
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
  protected void config(ThirdCoastTalon talon, int value) {
    talon.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, value, TIMEOUT_MS);
  }

  @Override
  protected void saveConfig(int value) {}
}
