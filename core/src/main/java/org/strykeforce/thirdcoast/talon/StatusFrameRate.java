package org.strykeforce.thirdcoast.talon;

import static org.strykeforce.thirdcoast.talon.TalonConfiguration.TIMEOUT_MS;

import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a set of frame update rates for a {@link com.ctre.phoenix.motorcontrol.can.TalonSRX}.
 *
 * <p>If not modified, default status frame update rates are:
 *
 * <ul>
 *   <li>General <b>10ms</b>: error, output duty cycle, limit switches, faults, mode
 *   <li>Feedback <b>20ms</b>: selected encoder pos/vel, current, sticky faults, brake neutral
 *       state, motion control profile select
 *   <li>Quad Encoder <b>100ms</b>: pos/vel, Index rising edge count, A/B/Index pin state
 *   <li>Pulse Width <b>100ms</b>: assume abs encoder pos
 *   <li>Analog In/Temp/Bus Voltage <b>100ms</b>: analog pos/vel, temp, bus voltage
 * </ul>
 */
@ParametersAreNonnullByDefault
public final class StatusFrameRate {

  /** Sets Talon to default frame rates. */
  public static final StatusFrameRate DEFAULT = StatusFrameRate.builder().build();
  /** Sets Talon to 5 ms rate for all frames. */
  public static final StatusFrameRate GRAPHER;

  static {
    GRAPHER =
        StatusFrameRate.builder()
            .analogTempVbat(5)
            .feedback(5)
            .general(5)
            .pulseWidth(5)
            .quadEncoder(5)
            .build();
  }

  private final int analogTempVbat;
  private final int feedback;
  private final int general;
  private final int pulseWidth;
  private final int quadEncoder;

  StatusFrameRate(int analogTempVbat, int feedback, int general, int pulseWidth, int quadEncoder) {
    this.analogTempVbat = analogTempVbat;
    this.feedback = feedback;
    this.general = general;
    this.pulseWidth = pulseWidth;
    this.quadEncoder = quadEncoder;
  }

  /**
   * Configures the Talon with status frame update rates.
   *
   * @param talon the Talon to registerWith
   */
  public void configure(TalonSRX talon) {
    talon.setStatusFramePeriod(StatusFrame.Status_4_AinTempVbat, analogTempVbat, TIMEOUT_MS);
    talon.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, feedback, TIMEOUT_MS);
    talon.setStatusFramePeriod(StatusFrame.Status_1_General, general, TIMEOUT_MS);
    //    talon.setStatusFramePeriod(StatusFrame, pulseWidth);
    //    talon.setStatusFramePeriod(TalonSRX.StatusFrameRate.QuadEncoder, quadEncoder);
  }

  @Override
  @NotNull
  public String toString() {
    return "StatusFrameRate{"
        + "analogTempVbat="
        + analogTempVbat
        + ", feedback="
        + feedback
        + ", general="
        + general
        + ", pulseWidth="
        + pulseWidth
        + ", quadEncoder="
        + quadEncoder
        + '}';
  }

  /**
   * Builder for StatusFrameRate with default values.
   *
   * @return the builder for StatusFrameRate
   */
  @ParametersAreNonnullByDefault
  public static Builder builder() {
    return new Builder();
  }

  /** Builder for StatusFrameRate with default values. */
  public static class Builder {

    private int analogTempVbat = 100;
    private int feedback = 20;
    private int general = 10;
    private int pulseWidth = 100;
    private int quadEncoder = 100;

    Builder() {}

    @NotNull
    public StatusFrameRate build() {
      return new StatusFrameRate(analogTempVbat, feedback, general, pulseWidth, quadEncoder);
    }

    @NotNull
    public Builder analogTempVbat(int ms) {
      analogTempVbat = ms;
      return this;
    }

    @NotNull
    public Builder feedback(int ms) {
      feedback = ms;
      return this;
    }

    @NotNull
    public Builder general(int ms) {
      general = ms;
      return this;
    }

    @NotNull
    public Builder pulseWidth(int ms) {
      pulseWidth = ms;
      return this;
    }

    @NotNull
    public Builder quadEncoder(int ms) {
      quadEncoder = ms;
      return this;
    }
  }
}
