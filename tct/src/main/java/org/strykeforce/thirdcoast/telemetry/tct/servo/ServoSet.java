package org.strykeforce.thirdcoast.telemetry.tct.servo;

import edu.wpi.first.wpilibj.Servo;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.ServoItem;

@Singleton
public class ServoSet {

  private static final Logger logger = LoggerFactory.getLogger(ServoSet.class);
  private final TelemetryService telemetryService;
  private Servo servo;

  @Inject
  public ServoSet(TelemetryService telemetryService) {
    this.telemetryService = telemetryService;
  }

  void restartTelemetryService() {
    logger.info("restarting TelemetryService");
    telemetryService.stop();
    telemetryService.register(new ServoItem(servo));
    telemetryService.start();
  }

  public Servo getServo() {
    return servo;
  }

  public void setServo(Servo servo) {
    clearSelected();
    this.servo = servo;
  }

  public void clearSelected() {
    if (servo == null) {
      return;
    }
    servo.free();
    servo = null;
  }
}
