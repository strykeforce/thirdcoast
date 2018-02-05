package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.phoenix.motorcontrol.StatusFrame;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class AnalogTempVbatFrameRateCommand extends AbstractIntConfigCommand {

  public static final String NAME = VERIFY + "AnalogTempVbat Status Frame Rate";

  @Inject
  AnalogTempVbatFrameRateCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(ThirdCoastTalon talon, int value) {
    talon.setStatusFramePeriod(StatusFrame.Status_4_AinTempVbat, value, TIMEOUT_MS);
  }

  @Override
  protected void saveConfig(int value) {}
}
