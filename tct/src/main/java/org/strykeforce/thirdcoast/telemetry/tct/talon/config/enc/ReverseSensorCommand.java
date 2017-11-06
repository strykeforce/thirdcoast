package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractBooleanConfigCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.di.EncoderMenuModule;

public class ReverseSensorCommand extends AbstractBooleanConfigCommand {

  public final static String NAME = "Sensor Reversed";

  @Inject
  public ReverseSensorCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, EncoderMenuModule.MENU_ORDER.indexOf(NAME), reader, talonSet);
  }

  @Override
  protected void config(CANTalon talon, boolean value) {
    talon.reverseSensor(value);
  }
}
