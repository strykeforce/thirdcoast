package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractBooleanConfigCommand;

public class BrakeInNeutralCommand extends AbstractBooleanConfigCommand {

  public static final String NAME = "Enable Brake in Neutral";

  @Inject
  public BrakeInNeutralCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(TalonSRX talon, boolean value) {
    talon.enableBrakeMode(value); // true for brake
  }

  @Override
  protected void saveConfig(boolean value) {
    talonSet.talonConfigurationBuilder().brakeInNeutral(value);
  }
}
