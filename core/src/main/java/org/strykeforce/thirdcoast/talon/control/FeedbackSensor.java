package org.strykeforce.thirdcoast.talon.control;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Errors;

@ParametersAreNonnullByDefault
public final class FeedbackSensor {

  public static final FeedbackSensor DEFAULT = new FeedbackSensor(FeedbackDevice.None, 0);
  private static final Logger logger = LoggerFactory.getLogger(FeedbackSensor.class);

  private final FeedbackDevice feedbackDevice;
  private final int pidIdx;

  public FeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx) {
    this.feedbackDevice = feedbackDevice;
    this.pidIdx = pidIdx;
  }

  public void configure(TalonSRX talon, int timeout) {
    ErrorCode err = talon.configSelectedFeedbackSensor(feedbackDevice, pidIdx, timeout);
    Errors.check(talon, "configSelectedFeedbackSensor", err, logger);
  }

  @Override
  public String toString() {
    return "FeedbackSensor{" + "feedbackDevice=" + feedbackDevice + ", pidIdx=" + pidIdx + '}';
  }
}
