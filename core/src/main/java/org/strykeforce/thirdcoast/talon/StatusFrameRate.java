package org.strykeforce.thirdcoast.talon;

import static com.ctre.phoenix.motorcontrol.StatusFrameEnhanced.Status_10_MotionMagic;
import static com.ctre.phoenix.motorcontrol.StatusFrameEnhanced.Status_13_Base_PIDF0;
import static com.ctre.phoenix.motorcontrol.StatusFrameEnhanced.Status_1_General;
import static com.ctre.phoenix.motorcontrol.StatusFrameEnhanced.Status_2_Feedback0;
import static com.ctre.phoenix.motorcontrol.StatusFrameEnhanced.Status_3_Quadrature;
import static com.ctre.phoenix.motorcontrol.StatusFrameEnhanced.Status_4_AinTempVbat;
import static com.ctre.phoenix.motorcontrol.StatusFrameEnhanced.Status_8_PulseWidth;
import static org.strykeforce.thirdcoast.talon.TalonProvisioner.TIMEOUT_MS;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a set of frame update rates for a {@link com.ctre.phoenix.motorcontrol.can.TalonSRX}.
 *
 * <p>If not modified, default status frame update rates are:
 *
 * <ul>
 *   <li>General <b>10ms</b>: motor output, limit switches, faults, control mode, soft limits,
 *       inverted motor output, neutral mode brake
 *   <li>Feedback0 <b>20ms</b>: selected encoder pos/vel for PID loop 0, current, sticky faults,
 *       motion control profile select
 *   <li>Quad Encoder <b>160ms</b>: pos/vel, Index rising edge count, A/B/Index pin state
 *   <li>Analog In/Temp/Bus Voltage <b>160ms</b>: analog pos/vel, temp, bus voltage, PID loop 0
 *       selected feedback sensor
 *   <li>Pulse Width <b>160ms</b>: period and pulse width, pos/vel
 *   <li>Motion Magic <b>160ms</b>: Motion Magic and Motion Profile target pos/vel and heading for
 *       active trajectory point
 *   <li>PIDF0 <b>160ms</b>: PID loop 0 closed-loop error, integral accumulator, derivative
 * </ul>
 */
@ParametersAreNonnullByDefault
public final class StatusFrameRate {

  /** Sets Talon to default frame rates. */
  public static final StatusFrameRate DEFAULT = StatusFrameRate.builder().build();
  /** Sets Talon to 5 ms rate for all frames. */
  public static final StatusFrameRate GRAPHER;

  private static final Logger logger = LoggerFactory.getLogger(StatusFrameRate.class);

  static {
    GRAPHER =
        StatusFrameRate.builder()
            .analogTempVbat(5)
            .feedback(5)
            .general(5)
            .pulseWidth(5)
            .quadEncoder(5)
            .motion(5)
            .pidf0(5)
            .build();
  }

  private final int general;
  private final int feedback; // Feedback0
  private final int quadEncoder;
  private final int analogTempVbat;
  private final int pulseWidth;
  private final int motion;
  private final int pidf0;

  StatusFrameRate(
      int analogTempVbat,
      int feedback,
      int general,
      int pulseWidth,
      int quadEncoder,
      int motion,
      int pidf0) {
    this.analogTempVbat = analogTempVbat;
    this.feedback = feedback;
    this.general = general;
    this.pulseWidth = pulseWidth;
    this.quadEncoder = quadEncoder;
    this.motion = motion;
    this.pidf0 = pidf0;
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

  /**
   * Configures the Talon with status frame update rates.
   *
   * @param talon the Talon to registerWith
   */
  public void configure(TalonSRX talon) {
    ErrorCode err = talon.setStatusFramePeriod(Status_1_General, general, TIMEOUT_MS);
    Errors.check(err, logger);
    err = talon.setStatusFramePeriod(Status_2_Feedback0, feedback, TIMEOUT_MS);
    Errors.check(err, logger);
    err = talon.setStatusFramePeriod(Status_3_Quadrature, quadEncoder, TIMEOUT_MS);
    Errors.check(err, logger);
    err = talon.setStatusFramePeriod(Status_4_AinTempVbat, analogTempVbat, TIMEOUT_MS);
    Errors.check(err, logger);
    err = talon.setStatusFramePeriod(Status_8_PulseWidth, pulseWidth, TIMEOUT_MS);
    Errors.check(err, logger);
    err = talon.setStatusFramePeriod(Status_10_MotionMagic, motion, TIMEOUT_MS);
    Errors.check(err, logger);
    err = talon.setStatusFramePeriod(Status_13_Base_PIDF0, pidf0, TIMEOUT_MS);
    Errors.check(err, logger);
  }

  @Override
  public String toString() {
    return "StatusFrameRate{"
        + "general="
        + general
        + ", feedback="
        + feedback
        + ", quadEncoder="
        + quadEncoder
        + ", analogTempVbat="
        + analogTempVbat
        + ", pulseWidth="
        + pulseWidth
        + ", motion="
        + motion
        + ", pidf0="
        + pidf0
        + '}';
  }

  /** Builder for StatusFrameRate with default values. */
  public static class Builder {

    private int general = 10;
    private int feedback = 20;
    private int quadEncoder = 160;
    private int analogTempVbat = 160;
    private int pulseWidth = 160;
    private int motion = 160;
    private int pidf0 = 160;

    Builder() {}

    @NotNull
    public StatusFrameRate build() {
      return new StatusFrameRate(
          analogTempVbat, feedback, general, pulseWidth, quadEncoder, motion, pidf0);
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

    @NotNull
    public Builder motion(int ms) {
      motion = ms;
      return this;
    }

    @NotNull
    public Builder pidf0(int ms) {
      pidf0 = ms;
      return this;
    }
  }
}
