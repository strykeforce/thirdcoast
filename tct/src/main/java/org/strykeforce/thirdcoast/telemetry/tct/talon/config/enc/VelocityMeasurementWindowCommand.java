package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.IntConfigCommand;

public class VelocityMeasurementWindowCommand extends IntConfigCommand {

  public final static String NAME = "Velocity Measurement Window";

  @Inject
  public VelocityMeasurementWindowCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, EncoderMenuModule.MENU_ORDER.indexOf(NAME), terminal, talonSet);
  }

  @Override
  protected void config(CANTalon talon, int value) {
    talon.SetVelocityMeasurementWindow(value);
  }

}
