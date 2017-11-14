package org.strykeforce.thirdcoast.telemetry.tct.dio;

import edu.wpi.first.wpilibj.DigitalOutput;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.DigitalOutputItem;
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;

@ModeScoped
public class DigitalOutputSet {

  private final static Logger logger = LoggerFactory.getLogger(DigitalOutputSet.class);
  private DigitalOutput dio;
  private final TelemetryService telemetryService;

  @Inject
  public DigitalOutputSet(TelemetryService telemetryService) {
    this.telemetryService = telemetryService;
  }

  void restartTelemetryService() {
    logger.info("restarting TelemetryService");
    telemetryService.stop();
    telemetryService.clear();
    telemetryService.register(new DigitalOutputItem(dio));
    telemetryService.start();
  }


  public DigitalOutput getDigitalOutput() {
    return dio;
  }

  public void selectDigitalOutput(int channel) {
    clearSelected();
    this.dio = new DigitalOutput(channel);
  }

  public void clearSelected() {
    if (this.dio != null) {
      this.dio.free();
    }
  }
}
