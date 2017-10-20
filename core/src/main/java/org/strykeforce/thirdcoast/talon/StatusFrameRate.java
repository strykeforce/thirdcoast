package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;

/**
 * Represents a set of frame update rates for a {@link com.ctre.CANTalon}.
 *
 * If not modified, default status frame update rates are:
 *
 * <ul>
 *   <li>General <b>10ms</b>: error, output duty cycle, limit switches, faults, mode</li>
 *   <li>Feedback <b>20ms</b>: selected encoder pos/vel, current, sticky faults, brake neutral state,
 *   motion control profile select</li>
 *   <li>Quad Encoder <b>100ms</b>: pos/vel, Index rising edge count, A/B/Index pin state</li>
 *   <li>Pulse Width <b>100ms</b>: assume abs encoder pos</li>
 *   <li>Analog In/Temp/Bus Voltage <b>100ms</b>: analog pos/vel, temp, bus voltage</li>
 * </ul>
 */
public final class StatusFrameRate {

  public final static StatusFrameRate DEFAULT = StatusFrameRate.builder().build();

  public final int analogTempVbat;
  public final int feedback;
  public final int general;
  public final int pulseWidth;
  public final int quadEncoder;

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
   * @param talon the Talon to configure
   */
  public void configure(CANTalon talon) {
    talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.AnalogTempVbat, analogTempVbat);
    talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.Feedback, feedback);
    talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.General, general);
    talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.PulseWidth, pulseWidth);
    talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.QuadEncoder, quadEncoder);
  }

  @Override
  public String toString() {
    return "StatusFrameRate{" +
        "analogTempVbat=" + analogTempVbat +
        ", feedback=" + feedback +
        ", general=" + general +
        ", pulseWidth=" + pulseWidth +
        ", quadEncoder=" + quadEncoder +
        '}';
  }

  /**
   * Builder for StatusFrameRate with default values.
   *
   * @return the builder for StatusFrameRate
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder for StatusFrameRate with default values.
   */
  public static class Builder {

    private int analogTempVbat = 100;
    private int feedback = 20;
    private int general = 10;
    private int pulseWidth = 100;
    private int quadEncoder = 100;

    Builder() {}

    public StatusFrameRate build() {
      return new StatusFrameRate(analogTempVbat, feedback, general, pulseWidth, quadEncoder);
    }

    public Builder analogTempVbat(int ms) {
      analogTempVbat = ms;
      return this;
    }

    public Builder feedback(int ms) {
      feedback = ms;
      return this;
    }

    public Builder general(int ms) {
      general = ms;
      return this;
    }

    public Builder pulseWidth(int ms) {
      pulseWidth = ms;
      return this;
    }

    public Builder quadEncoder(int ms) {
      quadEncoder = ms;
      return this;
    }
  }
}
