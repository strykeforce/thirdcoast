package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.StatusFrameRate;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractBooleanConfigCommand;

public class DefaultFrameRatesCommand extends AbstractBooleanConfigCommand {

  public static final String NAME = "Default Status Frame Rates";

  @Inject
  public DefaultFrameRatesCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(boolean value) {}

  @Override
  protected void config(CANTalon talon, boolean value) {
    if (value) {
      StatusFrameRate.DEFAULT.configure(talon);
      terminal.writer().println(StatusFrameRate.DEFAULT);
    }
  }
}
