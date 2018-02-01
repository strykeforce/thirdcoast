package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import com.ctre.phoenix.motorcontrol.ControlMode;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.Messages;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

public class SelectOperatingModeCommand extends AbstractTalonConfigCommand {

  public static final String NAME = "Control Mode";

  @Inject
  SelectOperatingModeCommand(TalonSet talonSet, LineReader reader) {
    super(NAME, reader, talonSet);
  }

  @Override
  public void perform() {
    String[] types = {
      "Voltage",
      "Speed",
      "Position",
      "Current",
      "Motion Magic",
      "Motion Profile",
      "Follower",
      "Disabled"
    };
    terminal.writer().println();
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
      ControlMode mode;
      done = true;
      switch (choice) {
        case 1:
          mode = ControlMode.PercentOutput;
          break;
        case 2:
          mode = ControlMode.Velocity;
          break;
        case 3:
          mode = ControlMode.Position;
          break;
        case 4:
          mode = ControlMode.Current;
          break;
        case 6:
          mode = ControlMode.MotionMagic;
          break;
        case 7:
          mode = ControlMode.MotionProfile;
          break;
        case 8:
          mode = ControlMode.Follower;
          break;
        case 9:
          mode = ControlMode.Disabled;
          break;
        default:
          continue;
      }
      for (ThirdCoastTalon talon : talonSet.selected()) {
        talon.changeControlMode(mode);
        logger.info("set {} for {} to {}", name(), talon.getDescription(), mode);
      }
      talonSet.talonConfigurationBuilder().mode(mode);
    }
  }

  private void help(int size) {
    terminal.writer().print(Messages.menuHelp(size));
  }
}
