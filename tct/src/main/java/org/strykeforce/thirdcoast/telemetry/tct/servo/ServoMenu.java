package org.strykeforce.thirdcoast.telemetry.tct.servo;

import edu.wpi.first.wpilibj.Servo;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;

public class ServoMenu extends Menu {

  private final ServoSet servoSet;

  public ServoMenu(CommandAdapter commandsAdapter, LineReader reader, ServoSet servoSet) {
    super(commandsAdapter, reader);
    this.servoSet = servoSet;
  }

  @Override
  protected String header() {
    Servo servo = servoSet.getServo();
    String id = servo != null ? String.valueOf(servo.getChannel()) : "";
    return boldGreem("Servo: " + id + "\n");
  }

}
