package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.CANTalon;
import com.ctre.CANTalon.StatusFrameRate;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class AnalogTempVbatFrameRateCommand extends AbstractIntConfigCommand {

  public static final String NAME = "AnalogTempVbat Status Frame Rate";

  @Inject
  public AnalogTempVbatFrameRateCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(CANTalon talon, int value) {
    talon.setStatusFrameRateMs(StatusFrameRate.AnalogTempVbat, value);
  }

  @Override
  protected void saveConfig(int value) {}
}
