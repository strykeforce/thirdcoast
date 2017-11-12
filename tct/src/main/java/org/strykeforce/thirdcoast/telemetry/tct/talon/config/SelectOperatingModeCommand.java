package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

public class SelectOperatingModeCommand extends AbstractTalonConfigCommand {

  public final static String NAME = "Control Mode";

  @Inject
  public SelectOperatingModeCommand(TalonSet talonSet, LineReader reader) {
    super(NAME, reader, talonSet);
  }

  @Override
  public void perform() {
    String[] types = {"Voltage", "Speed", "Position", "Current", "Percent Vbus", "Motion Magic",
        "Motion Profile", "Follower", "Disabled"};
    for (int i = 0; i < types.length; i++) {
      terminal.writer().printf("%2d - %s%n", i + 1, types[i]);
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
        help(types.length);
        continue;
      }
      CANTalon.TalonControlMode mode;
      done = true;
      switch (choice) {
        case 1:
          mode = TalonControlMode.Voltage;
          break;
        case 2:
          mode = TalonControlMode.Speed;
          break;
        case 3:
          mode = TalonControlMode.Position;
          break;
        case 4:
          mode = TalonControlMode.Current;
          break;
        case 5:
          mode = TalonControlMode.PercentVbus;
          break;
        case 6:
          mode = TalonControlMode.MotionMagic;
          break;
        case 7:
          mode = TalonControlMode.MotionProfile;
          break;
        case 8:
          mode = TalonControlMode.Follower;
          break;
        case 9:
          mode = TalonControlMode.Disabled;
          break;
        default:
          continue;
      }
      for (CANTalon talon : talonSet.selected()) {
        talon.changeControlMode(mode);
        logger.info("set {} for {} to {}", name(), talon.getDescription(), mode);
      }
      talonSet.talonConfigurationBuilder().mode(mode);

    }
  }

  private void help(int size) {
    String msg = String.format("please enter a number between 1-%d%n", size);
    terminal.writer().print(bold(msg));
  }

}
