package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class QuadEncoderFrameRateCommand extends AbstractIntConfigCommand {

  public static final String NAME = UNAVAILABLE + "QuadEncoder Status Frame Rate";

  @Inject
  public QuadEncoderFrameRateCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(ThirdCoastTalon talon, int value) {
    //    talon.setStatusFrameRateMs(StatusFrame., value);
  }

  @Override
  protected void saveConfig(int value) {}
}
