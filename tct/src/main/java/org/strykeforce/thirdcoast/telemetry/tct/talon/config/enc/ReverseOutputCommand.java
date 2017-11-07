package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractBooleanConfigCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.di.EncoderMenuModule;

public class ReverseOutputCommand extends AbstractBooleanConfigCommand {


  public final static String NAME = "Output Reversed";

  @Inject
  public ReverseOutputCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, EncoderMenuModule.MENU_ORDER.indexOf(NAME), reader, talonSet);
  }

  @Override
  protected void saveConfig(boolean value) {
    talonSet.talonConfigurationBuilder().outputReversed(value);
  }

  @Override
  protected void config(CANTalon talon, boolean value) {
    talon.reverseOutput(value);
  }

}
