package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommand;

public class VelocityMeasurementWindowCommand extends AbstractTalonConfigCommand {

  public static final String NAME = "Velocity Measurement Window";

  @Inject
  public VelocityMeasurementWindowCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  public void perform() {
    int[] windows = {1, 2, 4, 8, 16, 32, 64};
    terminal.writer().println();
    for (int i = 0; i < windows.length; i++) {
      terminal.writer().printf("%2d - %3d ms%n", i + 1, windows[i]);
    }
    boolean done = false;
    while (!done) {
      String line;
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
        terminal.writer().printf(Messages.menuHelp(windows.length));
        continue;
      }

      int setpoint = 1 << (choice - 1);
      done = true;
      talonSet.talonConfigurationBuilder().velocityMeasurementWindow(setpoint);
      for (ThirdCoastTalon talon : talonSet.selected()) {
        talon.configVelocityMeasurementWindow(setpoint, TIMEOUT_MS);
        logger.info("set {} for {} to {}", name(), talon.getDescription(), setpoint);
      }
    }
  }
}
