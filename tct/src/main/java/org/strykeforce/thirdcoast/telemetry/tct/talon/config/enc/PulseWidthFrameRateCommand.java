package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class PulseWidthFrameRateCommand extends AbstractIntConfigCommand {

  public static final String NAME = UNAVAILABLE + "PulseWidth Status Frame Rate";

  @Inject
  public PulseWidthFrameRateCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(ThirdCoastTalon talon, int value) {
    //    talon.setStatusFrameRateMs(StatusFrameRate.PulseWidth, value);
  }

  @Override
  protected void saveConfig(int value) {}
}
