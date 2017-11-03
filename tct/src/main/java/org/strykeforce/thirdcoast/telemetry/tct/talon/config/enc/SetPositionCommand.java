package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.DoubleConfigCommand;

public class SetPositionCommand extends DoubleConfigCommand {

  public final static String NAME = "Set Encoder Position";

  @Inject
  public SetPositionCommand(Terminal terminal, TalonSet talonSet) {
    super(NAME, EncoderMenuModule.MENU_ORDER.indexOf(NAME), terminal, talonSet);
  }

  @Override
  protected void config(CANTalon talon, double value) {
    talon.setPosition(value);
  }
}
