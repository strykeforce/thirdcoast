package org.strykeforce.thirdcoast.telemetry.tct.talon.config.limit;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.DoubleConfigCommand;

public class ForwardSoftLimitCommand extends DoubleConfigCommand {
  public final static String NAME = "Forward Soft Limit";

  @Inject
  public ForwardSoftLimitCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, terminal, talonSet);
  }

  @Override
  protected void config(CANTalon talon, double value) {
    talon.setForwardSoftLimit(value);
  }

}
