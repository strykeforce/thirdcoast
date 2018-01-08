package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.talon.StatusFrameRate;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractBooleanConfigCommand;

public class GrapherFrameRatesCommand extends AbstractBooleanConfigCommand {

  public static final String NAME = "Grapher (high speed) Status Frame Rates";

  @Inject
  public GrapherFrameRatesCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(boolean value) {}

  @Override
  protected void config(TalonSRX talon, boolean value) {
    if (value) {
      StatusFrameRate.GRAPHER.configure(talon);
      terminal.writer().println(StatusFrameRate.GRAPHER);
    }
  }
}
