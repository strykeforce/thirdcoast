package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.StatusFrameRate;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractBooleanConfigCommand;

public class DefaultFrameRatesCommand extends AbstractBooleanConfigCommand {

  public static final String NAME = "Default Status Frame Rates";

  @Inject
  DefaultFrameRatesCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(boolean value) {}

  @Override
  protected void config(ThirdCoastTalon talon, boolean value) {
    if (value) {
      StatusFrameRate.DEFAULT.configure(talon);
      terminal.writer().println(StatusFrameRate.DEFAULT);
    }
  }
}
