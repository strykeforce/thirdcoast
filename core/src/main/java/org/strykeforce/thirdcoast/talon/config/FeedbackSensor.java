package org.strykeforce.thirdcoast.talon.config;

import static com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Errors;

@ParametersAreNonnullByDefault
public class FeedbackSensor implements Configurable {

  public static final FeedbackSensor DEFAULT = new FeedbackSensor(QuadEncoder, 0, false);
  private static final Toml DEFAULT_TOML =
      new Toml().read(new TomlWriter().write(FeedbackSensor.DEFAULT));

  private static final Logger logger = LoggerFactory.getLogger(FeedbackSensor.class);

  private final FeedbackDevice feedbackDevice;
  private final int pidIdx;
  private final boolean phaseSensor;

  public FeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx, boolean phaseSensor) {
    this.feedbackDevice = feedbackDevice;
    this.pidIdx = pidIdx;
    this.phaseSensor = phaseSensor;
  }

  public static FeedbackSensor create(@Nullable Toml toml) {
    if (toml == null) {
      return DEFAULT;
    }
    return new Toml(DEFAULT_TOML).read(toml).to(FeedbackSensor.class);
  }

  @Override
  public void configure(TalonSRX talon, int timeout) {
    ErrorCode err = talon.configSelectedFeedbackSensor(feedbackDevice, pidIdx, timeout);
    Errors.check(talon, "configSelectedFeedbackSensor", err, logger);
    talon.setSensorPhase(phaseSensor);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FeedbackSensor that = (FeedbackSensor) o;
    return pidIdx == that.pidIdx
        && phaseSensor == that.phaseSensor
        && feedbackDevice == that.feedbackDevice;
  }

  @Override
  public int hashCode() {
    return Objects.hash(feedbackDevice, pidIdx, phaseSensor);
  }

  @Override
  public String toString() {
    return "FeedbackSensor{"
        + "feedbackDevice="
        + feedbackDevice
        + ", pidIdx="
        + pidIdx
        + ", phaseSensor="
        + phaseSensor
        + '}';
  }
}
