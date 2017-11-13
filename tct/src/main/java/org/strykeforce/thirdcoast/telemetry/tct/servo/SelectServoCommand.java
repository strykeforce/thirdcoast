package org.strykeforce.thirdcoast.telemetry.tct.servo;

import edu.wpi.first.wpilibj.Servo;
import javax.inject.Inject;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;

public class SelectServoCommand extends AbstractCommand {

  public final static String NAME = "Select Servo";
  private final static Logger logger = LoggerFactory.getLogger(SelectServoCommand.class);
  private final ServoSet servoSet;

  @Inject
  public SelectServoCommand(LineReader reader, ServoSet servoSet) {
    super(NAME, reader);
    this.servoSet = servoSet;
  }

  protected static String prompt() {
    return new AttributedStringBuilder()
        .style(AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW))
        .append("servo ID or <enter> to return> ").toAnsi();
  }


  @Override
  public void perform() {
    terminal.writer().println(bold("enter servo ID"));

    String line;
    while (true) {
      try {
        line = reader.readLine(prompt()).trim();
      } catch (EndOfFileException | UserInterruptException e) {
        return;
      }

      if (line.isEmpty()) {
        String msg = "Servo selection unchanged";
        logger.info(msg);
        terminal.writer().println(bold(msg));
        return;
      }
      int id;
      try {
        id = Integer.valueOf(line);
      } catch (NumberFormatException e) {
        terminal.writer().print(bold(String.format("%s is not a number, ignoring%n", line)));
        continue;
      }
      servoSet.setServo(new Servo(id));
      logger.info("selected servo id {}", id);
      servoSet.restartTelemetryService();
      break;
    }
  }
}

