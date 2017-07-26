package org.strykeforce.sidewinder.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import java.util.Optional;

public class Encoder {

  private final CANTalon.FeedbackDevice feedbackDevice;
  private final boolean isReversed;
  private final boolean isUnitScalingEnabled;
  private final int ticksPerRevolution;

  Encoder(Optional<String> feedbackDevice, Optional<Boolean> isReversed,
      Optional<Integer> ticksPerRevolution) {

    this.feedbackDevice = CANTalon.FeedbackDevice.valueOf(feedbackDevice.orElse("QuadEncoder"));
    this.isReversed = isReversed.orElse(false);
    isUnitScalingEnabled = ticksPerRevolution.isPresent();
    this.ticksPerRevolution = isUnitScalingEnabled ? ticksPerRevolution.get() : -1;
  }

  public void configure(CANTalon talon) {
    talon.setFeedbackDevice(feedbackDevice);
    talon.reverseSensor(isReversed);
    if (isUnitScalingEnabled) {
      talon.configEncoderCodesPerRev(ticksPerRevolution);
    }
  }

  public FeedbackDevice getFeedbackDevice() {
    return feedbackDevice;
  }

  public boolean isReversed() {
    return isReversed;
  }

  public boolean isUnitScalingEnabled() {
    return isUnitScalingEnabled;
  }

  public int getTicksPerRevolution() {
    return ticksPerRevolution;
  }
}
