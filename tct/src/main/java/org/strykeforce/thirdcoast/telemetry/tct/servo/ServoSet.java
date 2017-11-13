package org.strykeforce.thirdcoast.telemetry.tct.servo;

import edu.wpi.first.wpilibj.Servo;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.ServoItem;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;

@ModeScoped
public class ServoSet {

  private final static Logger logger = LoggerFactory.getLogger(ServoSet.class);
  private Servo servo;
  private final TelemetryService telemetryService;

  @Inject
  public ServoSet(TelemetryService telemetryService) {
    this.telemetryService = telemetryService;
  }

  void restartTelemetryService() {
    logger.info("restarting TelemetryService");
    telemetryService.stop();
    telemetryService.clear();
    telemetryService.register(new ServoItem(servo));
    telemetryService.start();
  }


  public Servo getServo() {
    return servo;
  }

  public void setServo(Servo servo) {
    this.servo = servo;
  }
}
