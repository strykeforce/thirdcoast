package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractBooleanConfigCommand;

public class ReverseSensorCommand extends AbstractBooleanConfigCommand {

  public static final String NAME = "Sensor Reversed";

  @Inject
  public ReverseSensorCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(boolean value) {
    talonSet.talonConfigurationBuilder().encoderReversed(value);
  }

  @Override
  protected void config(CANTalon talon, boolean value) {
    talon.reverseSensor(value);
  }
}
