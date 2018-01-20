package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.phoenix.motorcontrol.StatusFrame;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class FeedbackFrameRateCommand extends AbstractIntConfigCommand {

  public static final String NAME = VERIFY + "Feedback Status Frame Rate";

  @Inject
  FeedbackFrameRateCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(ThirdCoastTalon talon, int value) {
    talon.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, value, TIMEOUT_MS);
    talon.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, value, TIMEOUT_MS);
  }

  @Override
  protected void saveConfig(int value) {}
}
