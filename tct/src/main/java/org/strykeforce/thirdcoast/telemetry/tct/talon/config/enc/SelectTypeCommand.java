package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import static com.ctre.phoenix.motorcontrol.FeedbackDevice.Analog;
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.CTRE_MagEncoder_Absolute;
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.CTRE_MagEncoder_Relative;
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.None;
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.PulseWidthEncodedPosition;
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder;
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.RemoteSensor0;
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.RemoteSensor1;
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.SensorDifference;
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.SensorSum;
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.SoftwareEmulatedSensor;
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.Tachometer;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon;
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
      Analog.name(),
      CTRE_MagEncoder_Absolute.name(),
      CTRE_MagEncoder_Relative.name(),
      None.name(),
      PulseWidthEncodedPosition.name(),
      QuadEncoder.name(),
      RemoteSensor0.name(),
      RemoteSensor1.name(),
      SensorDifference.name(),
      SensorSum.name(),
      SoftwareEmulatedSensor.name(),
      Tachometer.name()
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
      FeedbackDevice device;
      done = true;
      switch (choice) {
        case 1:
          device = Analog;
          break;
        case 2:
          device = CTRE_MagEncoder_Absolute;
          break;
        case 3:
          device = CTRE_MagEncoder_Relative;
          break;
        case 4:
          device = None;
          break;
        case 5:
          device = PulseWidthEncodedPosition;
          break;
        case 6:
          device = QuadEncoder;
          break;
        case 7:
          device = RemoteSensor0;
          break;
        case 8:
          device = RemoteSensor1;
          break;
        case 9:
          device = SensorDifference;
          break;
        case 10:
          device = SensorSum;
          break;
        case 11:
          device = SoftwareEmulatedSensor;
          break;
        case 12:
          device = Tachometer;
          break;
        default:
          continue;
      }
      talonSet.talonConfigurationBuilder().encoder(device);
      for (ThirdCoastTalon talon : talonSet.selected()) {
        talon.configSelectedFeedbackSensor(device, 0, TIMEOUT_MS);
        logger.info("set {} for {} to {}", name(), talon.getDescription(), device);
      }
    }
  }
}
