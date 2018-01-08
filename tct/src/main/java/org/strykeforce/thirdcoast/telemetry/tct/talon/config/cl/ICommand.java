package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand;

/** Configure I. */
public class ICommand extends AbstractDoubleConfigCommand {

  public static final String NAME = "I";

  @Inject
  public ICommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(double value) {
    talonSet.talonConfigurationBuilder().I(value);
  }

  @Override
  protected void config(TalonSRX talon, double value) {
    talon.setI(value);
  }
}
