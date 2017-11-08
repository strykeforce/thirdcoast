package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.FeedbackDeviceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Encoder {

  final static Encoder DEFAULT = new Encoder();

  final static Logger logger = LoggerFactory.getLogger(Encoder.class);
  private final CANTalon.FeedbackDevice device;
  private final boolean reversed;
  private final boolean unitScalingEnabled;
  private final int ticksPerRevolution;

  Encoder(CANTalon.FeedbackDevice device, Boolean reversed, Integer ticksPerRevolution) {
    this.device = device;
    this.reversed = reversed != null ? reversed : false;
    unitScalingEnabled = ticksPerRevolution != null;
    this.ticksPerRevolution = unitScalingEnabled ? ticksPerRevolution : 0;
  }

  Encoder(CANTalon.FeedbackDevice device) {
    this(device, null, null);
  }

  Encoder() {
    this((CANTalon.FeedbackDevice) null, null, null);
  }

  Encoder(boolean reversed) {
    this((CANTalon.FeedbackDevice) null, reversed, null);
  }

  Encoder copyWithReversed(boolean reversed) {
    return new Encoder(device, reversed, unitScalingEnabled ? ticksPerRevolution : null);
  }

  Encoder copyWithEncoder(CANTalon.FeedbackDevice feedbackDevice) {
    return new Encoder(feedbackDevice, reversed, unitScalingEnabled ? ticksPerRevolution : null);
  }

  Encoder copyWithTicksPerRevolution(Integer ticksPerRevolution) {
    return new Encoder(device, reversed, ticksPerRevolution);
  }

  public void configure(CANTalon talon) {
    talon.setFeedbackDevice(getDevice());
    talon.reverseSensor(reversed);
    if (unitScalingEnabled) {
      talon.configEncoderCodesPerRev(ticksPerRevolution);
    }
    checkEncoder(talon);
  }

  public void checkEncoder(CANTalon talon) {
    FeedbackDeviceStatus status = talon.isSensorPresent(getDevice());
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

  public FeedbackDevice getDevice() {
    return device != null ? device : FeedbackDevice.QuadEncoder;
  }

  public boolean isReversed() {
    return reversed;
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
        "device=" + device +
        ", reversed=" + reversed +
        ", unitScalingEnabled=" + unitScalingEnabled +
        ", ticksPerRevolution=" + ticksPerRevolution +
        '}';
  }
}
