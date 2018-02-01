package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out;

import static com.ctre.phoenix.motorcontrol.NeutralMode.Brake;
import static com.ctre.phoenix.motorcontrol.NeutralMode.Coast;

import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractBooleanConfigCommand;

public class BrakeInNeutralCommand extends AbstractBooleanConfigCommand {

  public static final String NAME = "Enable Brake in Neutral";

  @Inject
  BrakeInNeutralCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(ThirdCoastTalon talon, boolean value) {
    talon.setNeutralMode(value ? Brake : Coast);
  }

  @Override
  protected void saveConfig(boolean value) {
    talonSet.talonConfigurationBuilder().brakeInNeutral(value);
  }
}
