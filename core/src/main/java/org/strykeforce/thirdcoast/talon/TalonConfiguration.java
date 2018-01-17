package org.strykeforce.thirdcoast.talon;

import static com.ctre.phoenix.motorcontrol.LimitSwitchNormal.Disabled;
import static com.ctre.phoenix.motorcontrol.LimitSwitchNormal.NormallyClosed;
import static com.ctre.phoenix.motorcontrol.LimitSwitchNormal.NormallyOpen;
import static com.ctre.phoenix.motorcontrol.LimitSwitchSource.Deactivated;
import static com.ctre.phoenix.motorcontrol.LimitSwitchSource.FeedbackConnector;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Talon configuration.
 *
 * @see com.ctre.phoenix.motorcontrol.can.TalonSRX
 */
public abstract class TalonConfiguration {

  public static final int TIMEOUT_MS = 0;

  // required
  @NotNull private final String name;
  @NotNull private final ControlMode mode;

  // optional
  private final Encoder encoder;
  private final double setpointMax;
  private final NeutralMode neutralMode;
  private final Boolean outputReversed;
  private final VelocityMeasPeriod velocityMeasurementPeriod;
  private final Integer velocityMeasurementWindow;
  private final LimitSwitch forwardLimitSwitch;
  private final LimitSwitch reverseLimitSwitch;
  private final SoftLimit forwardSoftLimit;
  private final SoftLimit reverseSoftLimit;
  private final Integer continuousCurrentLimit;
  private final Integer peakCurrentLimit;
  private final Integer peakCurrentLimitDuration;
  private final Double openLoopRampTime;
  private final Double voltageCompSaturation;
  private Set<Integer> talonIds;

  TalonConfiguration(
      @NotNull String name,
      @NotNull ControlMode mode,
      double setpointMax,
      Encoder encoder,
      NeutralMode neutralMode,
      Boolean outputReversed,
      VelocityMeasPeriod velocityMeasurementPeriod,
      Integer velocityMeasurementWindow,
      LimitSwitch forwardLimitSwitch,
      LimitSwitch reverseLimitSwitch,
      SoftLimit forwardSoftLimit,
      SoftLimit reverseSoftLimit,
      Integer continuousCurrentLimit,
      Integer peakCurrentLimit,
      Integer peakCurrentLimitDuration,
      Double openLoopRampTime,
      Double voltageCompSaturation) {
    this.name = name;
    this.mode = mode;
    this.setpointMax = setpointMax;
    this.encoder = encoder;
    this.neutralMode = neutralMode;
    this.outputReversed = outputReversed;
    this.velocityMeasurementPeriod = velocityMeasurementPeriod;
    this.velocityMeasurementWindow = velocityMeasurementWindow;
    this.forwardLimitSwitch = forwardLimitSwitch;
    this.reverseLimitSwitch = reverseLimitSwitch;
    this.forwardSoftLimit = forwardSoftLimit;
    this.reverseSoftLimit = reverseSoftLimit;
    this.continuousCurrentLimit = continuousCurrentLimit;
    this.peakCurrentLimit = peakCurrentLimit;
    this.peakCurrentLimitDuration = peakCurrentLimitDuration;
    this.openLoopRampTime = openLoopRampTime;
    this.voltageCompSaturation = voltageCompSaturation;
  }

  /**
   * Configure a Talon with stored parameters.
   *
   * @param talon the Talon to registerWith
   */
  public void configure(@NotNull TalonSRX talon) {
    talon.selectProfileSlot(0, 0);

    talon.enableVoltageCompensation(true);
    talon.configOpenloopRamp(openLoopRampTime != null ? openLoopRampTime : 0, TIMEOUT_MS);
    talon.configVoltageCompSaturation(valueOrElseZero(voltageCompSaturation, 12), TIMEOUT_MS);

    Encoder enc = encoder != null ? encoder : Encoder.DEFAULT;
    enc.configure(talon);

    talon.setNeutralMode(neutralMode != null ? neutralMode : NeutralMode.Coast);
    talon.setInverted(outputReversed != null ? outputReversed : false);

    if (velocityMeasurementPeriod != null) {
      talon.configVelocityMeasurementPeriod(velocityMeasurementPeriod, TIMEOUT_MS);
    } else {
      talon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, TIMEOUT_MS);
    }

    if (velocityMeasurementWindow != null) {
      talon.configVelocityMeasurementWindow(velocityMeasurementWindow, TIMEOUT_MS);
    } else {
      talon.configVelocityMeasurementWindow(64, TIMEOUT_MS);
    }

    LimitSwitch hardLimit = forwardLimitSwitch != null ? forwardLimitSwitch : LimitSwitch.DEFAULT;
    boolean enabled = hardLimit.isEnabled();
    talon.configForwardLimitSwitchSource(
        hardLimit.isEnabled() ? FeedbackConnector : Deactivated,
        hardLimit.isEnabled()
            ? (hardLimit.isNormallyOpen() ? NormallyOpen : NormallyClosed)
            : Disabled,
        TIMEOUT_MS);
    hardLimit = reverseLimitSwitch != null ? reverseLimitSwitch : LimitSwitch.DEFAULT;
    enabled |= hardLimit.isEnabled();
    talon.configReverseLimitSwitchSource(
        hardLimit.isEnabled() ? FeedbackConnector : Deactivated,
        hardLimit.isEnabled()
            ? (hardLimit.isNormallyOpen() ? NormallyOpen : NormallyClosed)
            : Disabled,
        TIMEOUT_MS);
    talon.overrideLimitSwitchesEnable(enabled);

    SoftLimit softLimit = forwardSoftLimit != null ? forwardSoftLimit : SoftLimit.DEFAULT;
    enabled = softLimit.isEnabled();
    talon.configForwardSoftLimitEnable(softLimit.isEnabled(), TIMEOUT_MS);
    talon.configForwardSoftLimitThreshold(softLimit.getPosition(), TIMEOUT_MS);

    softLimit = reverseSoftLimit != null ? reverseSoftLimit : SoftLimit.DEFAULT;
    enabled |= softLimit.isEnabled();
    talon.configReverseSoftLimitEnable(softLimit.isEnabled(), TIMEOUT_MS);
    talon.configReverseSoftLimitThreshold(softLimit.getPosition(), TIMEOUT_MS);
    talon.overrideSoftLimitsEnable(enabled);

    configCurrentLimits(talon);

    addTalonId(talon.getDeviceID());
  }

  private void configCurrentLimits(TalonSRX talon) {
    boolean contEnabled = continuousCurrentLimit != null && continuousCurrentLimit > 0;
    talon.configContinuousCurrentLimit(contEnabled ? continuousCurrentLimit : 0, TIMEOUT_MS);

    boolean peakEnabled = peakCurrentLimit != null && peakCurrentLimit > 0;
    talon.configPeakCurrentLimit(peakEnabled ? peakCurrentLimit : 0, TIMEOUT_MS);

    if (peakEnabled) {
      if (peakCurrentLimitDuration == null) {
        throw new IllegalArgumentException(
            "peakCurrentLimitDuration must be specified for peakCurrentLimit = "
                + peakCurrentLimit);
      }
      talon.configPeakCurrentDuration(peakCurrentLimitDuration, TIMEOUT_MS);
    }
    talon.enableCurrentLimit(contEnabled || peakEnabled);
  }

  /**
   * Add Talon ID for tracking in TOML config.
   *
   * @param id the Talon ID.
   */
  private void addTalonId(int id) {
    if (talonIds == null) {
      talonIds = new HashSet<>();
    }
    talonIds.add(id);
  }

  /**
   * Add optional Talon IDs for tracking in TOML config.
   *
   * @param ids the Talon IDs to add.
   */
  public void addAllTalonIds(@NotNull Collection<Integer> ids) {
    if (talonIds == null) {
      talonIds = new HashSet<>();
    }
    talonIds.addAll(ids);
  }

  /**
   * If tracking Talon IDs in TOML config, get the set of IDs.
   *
   * @return the Set of Talon IDs.
   */
  @NotNull
  public Set<Integer> getTalonIds() {
    if (talonIds == null) {
      return Collections.emptySet();
    }
    return Collections.unmodifiableSet(talonIds);
  }

  /**
   * Get the name used to look up this Talon configuration.
   *
   * @return configuration name
   */
  @NotNull
  public String getName() {
    return name;
  }

  public Encoder getEncoder() {
    return encoder;
  }

  // FIXME: duplicated below?
  NeutralMode isBrakeInNeutral() {
    return neutralMode;
  }

  Boolean isOutputReversed() {
    return outputReversed;
  }

  public VelocityMeasPeriod getVelocityMeasurementPeriod() {
    return velocityMeasurementPeriod;
  }

  public Integer getVelocityMeasurementWindow() {
    return velocityMeasurementWindow;
  }

  public LimitSwitch getForwardLimitSwitch() {
    return forwardLimitSwitch;
  }

  public LimitSwitch getReverseLimitSwitch() {
    return reverseLimitSwitch;
  }

  public SoftLimit getForwardSoftLimit() {
    return forwardSoftLimit;
  }

  public SoftLimit getReverseSoftLimit() {
    return reverseSoftLimit;
  }

  /**
   * Get the current limit.
   *
   * @return the current limit, or null if not enabled.
   */
  public Integer getContinuousCurrentLimit() {
    return continuousCurrentLimit;
  }

  public Integer getPeakCurrentLimit() {
    return peakCurrentLimit;
  }

  public Integer getPeakCurrentLimitDuration() {
    return peakCurrentLimitDuration;
  }

  @NotNull
  public ControlMode getMode() {
    return mode;
  }

  public NeutralMode getBrakeInNeutral() {
    return neutralMode;
  }

  public Boolean getOutputReversed() {
    return outputReversed;
  }

  public Double getOpenLoopRampTime() {
    return openLoopRampTime;
  }

  public Double getVoltageCompSaturation() {
    return voltageCompSaturation;
  }

  /**
   * Maximum setpoint allowed by this Talon configuration. This is used by the {@link
   * org.strykeforce.thirdcoast.swerve.Wheel#set} to scale the drive output setpoint.
   *
   * @return the maximum allowed setpoint
   */
  public double getSetpointMax() {
    return setpointMax;
  }

  double valueOrElseZero(@Nullable Double value, double def) {
    if (value != null) {
      return value;
    }
    return def;
  }

  int valueOrElseZero(@Nullable Integer value) {
    if (value != null) {
      return value;
    }
    return 0;
  }

  @Override
  public String toString() {
    return "TalonConfiguration{"
        + "name='"
        + name
        + '\''
        + ", mode="
        + mode
        + ", encoder="
        + encoder
        + ", setpointMax="
        + setpointMax
        + ", neutralMode="
        + neutralMode
        + ", outputReversed="
        + outputReversed
        + ", velocityMeasurementPeriod="
        + velocityMeasurementPeriod
        + ", velocityMeasurementWindow="
        + velocityMeasurementWindow
        + ", forwardLimitSwitch="
        + forwardLimitSwitch
        + ", reverseLimitSwitch="
        + reverseLimitSwitch
        + ", forwardSoftLimit="
        + forwardSoftLimit
        + ", reverseSoftLimit="
        + reverseSoftLimit
        + ", continuousCurrentLimit="
        + continuousCurrentLimit
        + ", openLoopRampTime="
        + openLoopRampTime
        + ", voltageCompSaturation="
        + voltageCompSaturation
        + '}';
  }
}
