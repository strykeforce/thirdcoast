package org.strykeforce.thirdcoast.talon;

import static org.strykeforce.thirdcoast.talon.TalonConfiguration.TIMEOUT_MS;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public final class Encoder {

  @NotNull static final Encoder DEFAULT = new Encoder();

  private static final Logger logger = LoggerFactory.getLogger(Encoder.class);
  @Nullable private final FeedbackDevice device;
  private final boolean reversed;

  Encoder(@Nullable FeedbackDevice device, @Nullable Boolean reversed) {
    this.device = device;
    this.reversed = reversed != null ? reversed : false;
  }

  Encoder(FeedbackDevice device) {
    this(device, null);
  }

  private Encoder() {
    this(null, null);
  }

  Encoder(boolean reversed) {
    this(null, reversed);
  }

  @NotNull
  Encoder copyWithReversed(boolean reversed) {
    return new Encoder(device, reversed);
  }

  @NotNull
  Encoder copyWithEncoder(FeedbackDevice feedbackDevice) {
    return new Encoder(feedbackDevice, reversed);
  }

  public void configure(TalonSRX talon) {
    ErrorCode error = talon.configSelectedFeedbackSensor(getDevice(), 0, TIMEOUT_MS);
    Errors.check(error, logger);
    talon.setSensorPhase(reversed);
    logger.info(
        "{}: encoder {} {} reversed",
        ((WPI_TalonSRX) talon).getDescription(),
        getDevice(),
        reversed ? "" : "not");
  }

  @NotNull
  public FeedbackDevice getDevice() {
    return device != null ? device : FeedbackDevice.QuadEncoder;
  }

  public boolean isReversed() {
    return reversed;
  }

  @Override
  public String toString() {
    return "Encoder{" + "device=" + device + ", reversed=" + reversed + '}';
  }
}
