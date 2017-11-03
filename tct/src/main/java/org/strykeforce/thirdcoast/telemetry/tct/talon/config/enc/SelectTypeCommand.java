package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.ConfigCommand;

public class SelectTypeCommand extends ConfigCommand {

  public final static String NAME = "Encoder Type";

  @Inject
  public SelectTypeCommand(TalonSet talonSet, Terminal terminal) {
    super(NAME, EncoderMenuModule.MENU_ORDER.indexOf(NAME), terminal, talonSet);
  }

//  AnalogEncoder
//AnalogPot
//    CtreMagEncoder_Absolute
//  CtreMagEncoder_Relative
//      EncFalling
//  EncRising
//      PulseWidth
//  QuadEncoder

  @Override
  public void perform() {
    String[] types = {"Analog", "Analog Potentiometer", "CTRE Magnetic Absolute",
        "CTRE Magnetic Relative", "Falling Edge", "Rising Edge", "Pulse Width", "Quadrature"};
    for (int i = 0; i < types.length; i++) {
      terminal.writer().printf("%2d - %s ms%n", i + 1, types[i]);
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
      for (CANTalon talon : talonSet.selected()) {
        talon.setFeedbackDevice(device);
        logger.info("set {} for {} to {}", name(), talon.getDescription(), device);
      }
    }
  }
}
