package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.CANTalon;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommand;

public class VelocityMeasurementWindowCommand extends AbstractTalonConfigCommand {

  public final static String NAME = "Velocity Measurement Window";

  @Inject
  public VelocityMeasurementWindowCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, EncoderMenuModule.MENU_ORDER.indexOf(NAME), reader, talonSet);
  }

  @Override
  public void perform() {
    int[] windows = {1, 2, 4, 8, 16, 32, 64};
    for (int i = 0; i < windows.length; i++) {
      terminal.writer().printf("%2d - %3d ms%n", i + 1, windows[i]);
    }
    boolean done = false;
    while (!done) {
      String line = null;
      try {
        line = reader.readLine(prompt()).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        break;
      }

      if (line.isEmpty()) {
        logger.info("no value entered");
        break;
      }

      int choice;
      try {
        choice = Integer.valueOf(line);
      } catch (NumberFormatException nfe) {
        terminal.writer().println("please enter an integer");
        continue;
      }

      if (choice < 1 || choice > windows.length) {
        terminal.writer()
            .printf(bold(String.format("please enter a number between 1-%d%n", windows.length)));
        continue;
      }

      int setpoint = 1 << (choice - 1);
      done = true;

      for (CANTalon talon : talonSet.selected()) {
        talon.SetVelocityMeasurementWindow(setpoint);
        logger.info("set {} for {} to {}", name(), talon.getDescription(), setpoint);
      }
    }
  }
}
