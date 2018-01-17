package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand;

public class SetPositionCommand extends AbstractDoubleConfigCommand {

  public static final String NAME = "Set Encoder Position";

  @Inject
  public SetPositionCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(double value) {
    // not a configuration value
  }

  @Override
  protected void config(CANTalon talon, double value) {
    talon.setPosition(value);
  }
}
