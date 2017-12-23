package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommand;

public class SelectTypeCommand extends AbstractTalonConfigCommand {

  public static final String NAME = "Encoder Type";

  @Inject
  public SelectTypeCommand(LineReader reader, TalonSet talonSet) {
    super(NAME, reader, talonSet);
  }

  @Override
  public void perform() {
    String[] types = {
      "Analog",
      "Analog Potentiometer",
      "CTRE Magnetic Absolute",
      "CTRE Magnetic Relative",
      "Falling Edge",
      "Rising Edge",
      "Pulse Width",
      "Quadrature"
    };
    terminal.writer().println();
    for (int i = 0; i < types.length; i++) {
      terminal.writer().printf("%2d - %s ms%n", i + 1, types[i]);
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
      CANTalon.FeedbackDevice device;
      done = true;
      switch (choice) {
        case 1:
          device = FeedbackDevice.AnalogEncoder;
          break;
        case 2:
          device = FeedbackDevice.AnalogPot;
          break;
        case 3:
          device = FeedbackDevice.CtreMagEncoder_Absolute;
          break;
        case 4:
          device = FeedbackDevice.CtreMagEncoder_Relative;
          break;
        case 5:
          device = FeedbackDevice.EncFalling;
          break;
        case 6:
          device = FeedbackDevice.EncRising;
          break;
        case 7:
          device = FeedbackDevice.PulseWidth;
          break;
        case 8:
          device = FeedbackDevice.QuadEncoder;
          break;
        default:
          continue;
      }
      talonSet.talonConfigurationBuilder().encoder(device);
      for (CANTalon talon : talonSet.selected()) {
        talon.setFeedbackDevice(device);
        logger.info("set {} for {} to {}", name(), talon.getDescription(), device);
      }
    }
  }
}
