package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractIntConfigCommand;

/** Configure F. */
public class IZoneCommand extends AbstractIntConfigCommand {

  public static final String NAME = "I Zone";

  @Inject
  public IZoneCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(int value) {
    talonSet.talonConfigurationBuilder().iZone(value);
  }

  @Override
  protected void config(CANTalon talon, int value) {
    talon.setIZone(value);
  }
}
