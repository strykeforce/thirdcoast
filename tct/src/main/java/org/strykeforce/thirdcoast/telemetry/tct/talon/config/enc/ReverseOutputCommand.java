package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.BooleanConfigCommand;

public class ReverseOutputCommand extends BooleanConfigCommand {


  public final static String NAME = "Output Reversed";

  @Inject
  public ReverseOutputCommand(Terminal terminal, TalonSet talonSet) {
    super(NAME, EncoderMenuModule.MENU_ORDER.indexOf(NAME), terminal, talonSet);
  }

  @Override
  protected void config(CANTalon talon, boolean value) {
    talon.reverseOutput(value);
  }

}
