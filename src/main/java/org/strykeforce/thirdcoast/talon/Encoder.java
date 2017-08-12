package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.FeedbackDeviceStatus;
import java.util.Optional;

final class Encoder {

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
    checkEncoder(talon);
  }

  public void checkEncoder(CANTalon talon) {
    FeedbackDeviceStatus status = talon.isSensorPresent(feedbackDevice);

    if (status != null) {
      System.out.print(talon.getDescription() + ": ");
    } else {
      return; // unit testing
    }
    switch (status) {
      case FeedbackStatusPresent:
        System.out.println("encoder is present");
        break;
      case FeedbackStatusNotPresent:
        System.out.println("encoder is MISSING");
        break;
      case FeedbackStatusUnknown:
        System.out.println(
            "encoder is unknown, only CTRE Mag Encoder or Pulse-Width Encoder supported");
        break;
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

  @Override
  public String toString() {
    return "Encoder{" +
        "feedbackDevice=" + feedbackDevice +
        ", isReversed=" + isReversed +
        ", isUnitScalingEnabled=" + isUnitScalingEnabled +
        ", ticksPerRevolution=" + ticksPerRevolution +
        '}';
  }
}
