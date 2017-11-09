package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand;

public class ReverseSoftLimitCommand  extends AbstractDoubleConfigCommand {

  public final static String NAME = "Reverse Soft Limit";

  @Inject
  public ReverseSoftLimitCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void saveConfig(double value) {
    talonSet.talonConfigurationBuilder().reverseSoftLimit(value);
  }

  @Override
  protected void config(CANTalon talon, double value) {
    if (value == 0) {
      talon.enableReverseSoftLimit(false);
      return;
    }
    talon.setReverseSoftLimit(value);
    talon.enableReverseSoftLimit(true);
  }

}
