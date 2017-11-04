package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand;

public class ForwardSoftLimitCommand extends AbstractDoubleConfigCommand {
  public final static String NAME = "Forward Soft Limit";

  @Inject
  public ForwardSoftLimitCommand(TalonSet talonSet, LineReader reader) {
    super(NAME, reader, talonSet);
  }

  @Override
  protected void config(CANTalon talon, double value) {
    talon.setForwardSoftLimit(value);
  }

}
