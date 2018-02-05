package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommand;

public class VelocityMeasurementPeriodCommand extends AbstractTalonConfigCommand {

  public static final String NAME = "Velocity Measurement Period";

  @Inject
  public VelocityMeasurementPeriodCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  public void perform() {
    int[] periods = {1, 2, 5, 10, 20, 25, 50, 100};
    terminal.writer().println();
    for (int i = 0; i < periods.length; i++) {
      terminal.writer().printf("%2d - %3d ms%n", i + 1, periods[i]);
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
      VelocityMeasPeriod setpoint;
      done = true;
      switch (choice) {
        case 1:
          setpoint = VelocityMeasPeriod.Period_1Ms;
          break;
        case 2:
          setpoint = VelocityMeasPeriod.Period_2Ms;
          break;
        case 3:
          setpoint = VelocityMeasPeriod.Period_5Ms;
          break;
        case 4:
          setpoint = VelocityMeasPeriod.Period_10Ms;
          break;
        case 5:
          setpoint = VelocityMeasPeriod.Period_20Ms;
          break;
        case 6:
          setpoint = VelocityMeasPeriod.Period_25Ms;
          break;
        case 7:
          setpoint = VelocityMeasPeriod.Period_50Ms;
          break;
        case 8:
          setpoint = VelocityMeasPeriod.Period_100Ms;
          break;
        default:
          continue;
      }
      talonSet.talonConfigurationBuilder().velocityMeasurementPeriod(setpoint);
      for (ThirdCoastTalon talon : talonSet.selected()) {
        talon.configVelocityMeasurementPeriod(setpoint, TIMEOUT_MS);
        logger.info("set {} for {} to {}", name(), talon.getDescription(), setpoint);
      }
    }
  }
}
