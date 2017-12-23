package org.strykeforce.thirdcoast.telemetry.tct.dio;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.DigitalInputItem;
import org.strykeforce.thirdcoast.telemetry.item.DigitalOutputItem;
import org.strykeforce.thirdcoast.telemetry.item.Item;

@Singleton
public class DioSet {

  private static final Logger logger = LoggerFactory.getLogger(DioSet.class);
  private final TelemetryService telemetryService;
  private final List<DigitalInput> inputs = new ArrayList<>(10);
  private final Set<Integer> outputs = new HashSet<>();
  private DigitalOutput digitalOutput;

  @Inject
  public DioSet(TelemetryService telemetryService) {
    this.telemetryService = telemetryService;
  }

  @Nullable
  DigitalOutput getDigitalOutput() {
    return digitalOutput;
  }

  void selectDigitalOutput(int channel) {
    clearSelectedOutput();
    removeInput(channel);
    digitalOutput = new DigitalOutput(channel);
    outputs.add(channel); // outputs can't be inputs

    telemetryService.stop();
    for (Item item : telemetryService.getItems()) {
      if (item instanceof DigitalInputItem && item.deviceId() == channel) {
        telemetryService.remove(item);
        break;
      }
    }
    telemetryService.register(new DigitalOutputItem(digitalOutput));
    telemetryService.start();
    logger.info("initialized output {} and restarted TelemetryService", channel);
  }

  void clearSelectedOutput() {
    if (digitalOutput == null) {
      logger.debug("clearSelectedOutput: output not initialized, returning");
      return;
    }
    logger.info("clearing output {}", digitalOutput.getChannel());
    digitalOutput.free();
    digitalOutput = null;
  }

  void removeInput(int channel) {
    if (inputs.size() == 0) {
      logger.debug("removeInput: inputs not initialized, returning");
      return;
    }
    DigitalInput input = inputs.get(channel);
    if (input == null) {
      logger.info("input channel {} is already removed", channel);
      return;
    }
    input.free();
    inputs.set(channel, null);
    logger.info("removed input {}", channel);
  }

  @NotNull
  List<DigitalInput> getDigitalInputs() {
    if (inputs.size() == 0) {
      telemetryService.stop();
      for (int i = 0; i < 10; i++) {
        if (outputs.contains(i)) {
          inputs.add(null); // outputs can't be inputs
        } else {
          DigitalInput input = new DigitalInput(i);
          inputs.add(input);
          telemetryService.register(new DigitalInputItem(input));
        }
      }
      telemetryService.start();
      logger.info("initialized inputs and restarted TelemetryService");
    }
    return Collections.unmodifiableList(inputs);
  }
}
