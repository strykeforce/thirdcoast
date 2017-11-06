package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.FeedbackDeviceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Encoder {

  final static Encoder DEFAULT = new Encoder((String)null, null, null);

  final static Logger logger = LoggerFactory.getLogger(Encoder.class);
  private final CANTalon.FeedbackDevice feedbackDevice;
  private final boolean isReversed;
  private final boolean unitScalingEnabled;
  private final int ticksPerRevolution;

  Encoder(CANTalon.FeedbackDevice feedbackDevice, Boolean isReversed, Integer ticksPerRevolution) {
    this.feedbackDevice = feedbackDevice;
    this.isReversed = isReversed != null ? isReversed : false;
    unitScalingEnabled = ticksPerRevolution != null;
    this.ticksPerRevolution = unitScalingEnabled ? ticksPerRevolution : -1;

  }

  Encoder(String feedbackDevice, Boolean isReversed, Integer ticksPerRevolution) {
    this(CANTalon.FeedbackDevice.valueOf(feedbackDevice != null ? feedbackDevice : "QuadEncoder"),
        isReversed,
        ticksPerRevolution);
  }

  public void configure(CANTalon talon) {
    talon.setFeedbackDevice(feedbackDevice);
    talon.reverseSensor(isReversed);
    if (unitScalingEnabled) {
      talon.configEncoderCodesPerRev(ticksPerRevolution);
    }
    checkEncoder(talon);
  }

  public void checkEncoder(CANTalon talon) {
    FeedbackDeviceStatus status = talon.isSensorPresent(feedbackDevice);
    if (status == null) {
      return; // unit testing
    }

    switch (status) {
      case FeedbackStatusPresent:
        logger.info("{}: encoder is present", talon.getDescription());
        break;
      case FeedbackStatusNotPresent:
        logger.warn("{}: encoder is MISSING", talon.getDescription());
        break;
      case FeedbackStatusUnknown:
        logger.info("{}: encoder is unknown, only CTRE Mag or Pulse-Width Encoder supported",
            talon.getDescription());
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
    return unitScalingEnabled;
  }

  public int getTicksPerRevolution() {
    return ticksPerRevolution;
  }

  @Override
  public String toString() {
    return "Encoder{" +
        "feedbackDevice=" + feedbackDevice +
        ", isReversed=" + isReversed +
        ", unitScalingEnabled=" + unitScalingEnabled +
        ", ticksPerRevolution=" + ticksPerRevolution +
        '}';
  }
}
