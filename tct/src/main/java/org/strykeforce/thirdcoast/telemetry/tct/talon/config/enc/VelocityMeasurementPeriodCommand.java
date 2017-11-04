package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.CANTalon;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.ConfigCommand;

public class VelocityMeasurementPeriodCommand extends ConfigCommand {

  public final static String NAME = "Velocity Measurement Period";

  @Inject
  public VelocityMeasurementPeriodCommand(TalonSet talonSet, LineReader reader) {
    super(NAME, EncoderMenuModule.MENU_ORDER.indexOf(NAME), reader, talonSet);
  }

  @Override
  public void perform() {
    int[] periods = {1, 2, 5, 10, 20, 25, 50, 100};
    for (int i = 0; i < periods.length; i++) {
      terminal.writer().printf("%2d - %3d ms%n", i + 1, periods[i]);
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
      CANTalon.VelocityMeasurementPeriod setpoint;
      done = true;
      switch (choice) {
        case 1:
          setpoint = VelocityMeasurementPeriod.Period_1Ms;
          break;
        case 2:
          setpoint = VelocityMeasurementPeriod.Period_2Ms;
          break;
        case 3:
          setpoint = VelocityMeasurementPeriod.Period_5Ms;
          break;
        case 4:
          setpoint = VelocityMeasurementPeriod.Period_10Ms;
          break;
        case 5:
          setpoint = VelocityMeasurementPeriod.Period_20Ms;
          break;
        case 6:
          setpoint = VelocityMeasurementPeriod.Period_25Ms;
          break;
        case 7:
          setpoint = VelocityMeasurementPeriod.Period_50Ms;
          break;
        case 8:
          setpoint = VelocityMeasurementPeriod.Period_100Ms;
          break;
        default:
          continue;
      }
      for (CANTalon talon : talonSet.selected()) {
        talon.SetVelocityMeasurementPeriod(setpoint);
        logger.info("set {} for {} to {}", name(), talon.getDescription(), setpoint);
      }
    }
  }
}
