package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl;

import com.ctre.CANTalon;
import javax.inject.Inject;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.di.ClosedLoopMenuModule;

/**
 * Configure I.
 */
public class ICommand extends AbstractDoubleConfigCommand {

  public final static String NAME = "I";

  @Inject
  public ICommand(LineReader reader, TalonSet talonSet) {
    super(NAME, ClosedLoopMenuModule.MENU_ORDER.indexOf(NAME), reader, talonSet);
  }

  @Override
  protected void config(CANTalon talon, double value) {
    talon.setI(value);
  }
}
