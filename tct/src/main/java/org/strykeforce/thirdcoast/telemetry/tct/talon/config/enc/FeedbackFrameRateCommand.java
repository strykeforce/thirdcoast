package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX.StatusFrameRate;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

public class FeedbackFrameRateCommand extends AbstractIntConfigCommand {

  public static final String NAME = "Feedback Status Frame Rate";

  @Inject
  public FeedbackFrameRateCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(TalonSRX talon, int value) {
    talon.setStatusFrameRateMs(StatusFrameRate.Feedback, value);
  }

  @Override
  protected void saveConfig(int value) {}
}
