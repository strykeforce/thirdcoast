package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import java.util.Optional;
import javax.inject.Inject;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

/**
 * Configure F.
 */
public class NominalOutputVoltageCommand extends AbstactTalonConfigCommand {

  public final static String NAME = "Nominal Ouput Voltage";

  @Inject
  public NominalOutputVoltageCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, terminal, talonSet);
  }

  @Override
  public void perform() {
    Optional<double[]> opt = getFwdRevDoubles();
    if (!opt.isPresent()) {
      return;
    }
    double[] fr = opt.get();
    for (CANTalon talon : talonSet.selected()) {
      talon.configNominalOutputVoltage(fr[0], fr[1]);
      logFwdRevConfig(talon, fr);
    }
  }
}
