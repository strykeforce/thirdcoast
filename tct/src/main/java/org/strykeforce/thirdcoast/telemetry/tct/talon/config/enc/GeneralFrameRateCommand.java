package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class GeneralFrameRateCommand extends AbstractIntConfigCommand {

  public static final String NAME = "General Status Frame Rate";

  @Inject
  GeneralFrameRateCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(ThirdCoastTalon talon, int value) {
    talon.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, value, TIMEOUT_MS);
  }

  @Override
  protected void saveConfig(int value) {}
}
