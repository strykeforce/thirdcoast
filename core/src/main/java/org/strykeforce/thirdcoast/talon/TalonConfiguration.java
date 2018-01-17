package org.strykeforce.thirdcoast.talon;

import static com.ctre.phoenix.motorcontrol.LimitSwitchNormal.Disabled;
import static com.ctre.phoenix.motorcontrol.LimitSwitchNormal.NormallyClosed;
import static com.ctre.phoenix.motorcontrol.LimitSwitchNormal.NormallyOpen;
import static com.ctre.phoenix.motorcontrol.LimitSwitchSource.Deactivated;
import static com.ctre.phoenix.motorcontrol.LimitSwitchSource.FeedbackConnector;

import com.ctre.phoenix.ErrorCode;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Talon configuration.
 *
 * @see com.ctre.phoenix.motorcontrol.can.TalonSRX
 */
public abstract class TalonConfiguration {
  protected static final Logger logger = LoggerFactory.getLogger(TalonConfiguration.class);

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
  protected int timeout = 0;
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
    logger.info("configuring Talon {} with timeout = {}", talon.getDeviceID(), timeout);

    ErrorCode err;
    talon.selectProfileSlot(0, 0);

    talon.enableVoltageCompensation(true);
    err = talon.configOpenloopRamp(openLoopRampTime != null ? openLoopRampTime : 0, timeout);
    Errors.check(talon, "configOpenloopRamp", err, logger);
    err = talon.configVoltageCompSaturation(valueOrElseZero(voltageCompSaturation, 12), timeout);
    Errors.check(talon, "configVoltageCompSaturation", err, logger);

    Encoder enc = encoder != null ? encoder : Encoder.DEFAULT;
    enc.configure(talon, timeout);

    talon.setNeutralMode(neutralMode != null ? neutralMode : NeutralMode.Coast);
    talon.setInverted(outputReversed != null ? outputReversed : false);

    if (velocityMeasurementPeriod != null) {
      err = talon.configVelocityMeasurementPeriod(velocityMeasurementPeriod, timeout);
    } else {
      err = talon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, timeout);
    }
    Errors.check(talon, "configVelocityMeasurementPeriod", err, logger);

    if (velocityMeasurementWindow != null) {
      err = talon.configVelocityMeasurementWindow(velocityMeasurementWindow, timeout);
    } else {
      err = talon.configVelocityMeasurementWindow(64, timeout);
    }
    Errors.check(talon, "configVelocityMeasurementWindow", err, logger);

    LimitSwitch hardLimit = forwardLimitSwitch != null ? forwardLimitSwitch : LimitSwitch.DEFAULT;
    boolean enabled = hardLimit.isEnabled();
    err =
        talon.configForwardLimitSwitchSource(
            hardLimit.isEnabled() ? FeedbackConnector : Deactivated,
            hardLimit.isEnabled()
                ? (hardLimit.isNormallyOpen() ? NormallyOpen : NormallyClosed)
                : Disabled,
            timeout);
    Errors.check(talon, "configForwardLimitSwitchSource", err, logger);

    hardLimit = reverseLimitSwitch != null ? reverseLimitSwitch : LimitSwitch.DEFAULT;
    enabled |= hardLimit.isEnabled();
    err =
        talon.configReverseLimitSwitchSource(
            hardLimit.isEnabled() ? FeedbackConnector : Deactivated,
            hardLimit.isEnabled()
                ? (hardLimit.isNormallyOpen() ? NormallyOpen : NormallyClosed)
                : Disabled,
            timeout);
    Errors.check(talon, "configReverseLimitSwitchSource", err, logger);
    talon.overrideLimitSwitchesEnable(enabled);

    SoftLimit softLimit = forwardSoftLimit != null ? forwardSoftLimit : SoftLimit.DEFAULT;
    enabled = softLimit.isEnabled();
    err = talon.configForwardSoftLimitEnable(softLimit.isEnabled(), timeout);
    Errors.check(talon, "configForwardSoftLimitEnable", err, logger);
    err = talon.configForwardSoftLimitThreshold(softLimit.getPosition(), timeout);
    Errors.check(talon, "configForwardSoftLimitThreshold", err, logger);

    softLimit = reverseSoftLimit != null ? reverseSoftLimit : SoftLimit.DEFAULT;
    enabled |= softLimit.isEnabled();
    err = talon.configReverseSoftLimitEnable(softLimit.isEnabled(), timeout);
    Errors.check(talon, "configReverseSoftLimitEnable", err, logger);
    err = talon.configReverseSoftLimitThreshold(softLimit.getPosition(), timeout);
    Errors.check(talon, "configReverseSoftLimitThreshold", err, logger);
    talon.overrideSoftLimitsEnable(enabled);

    configCurrentLimits(talon);

    addTalonId(talon.getDeviceID());
  }

  private void configCurrentLimits(TalonSRX talon) {
    ErrorCode err;
    boolean contEnabled = continuousCurrentLimit != null && continuousCurrentLimit > 0;
    err = talon.configContinuousCurrentLimit(contEnabled ? continuousCurrentLimit : 0, timeout);
    Errors.check(talon, "configContinuousCurrentLimit", err, logger);

    boolean peakEnabled = peakCurrentLimit != null && peakCurrentLimit > 0;
    err = talon.configPeakCurrentLimit(peakEnabled ? peakCurrentLimit : 0, timeout);
    Errors.check(talon, "configPeakCurrentLimit", err, logger);

    if (peakEnabled) {
      if (peakCurrentLimitDuration == null) {
        throw new IllegalArgumentException(
            "peakCurrentLimitDuration must be specified for peakCurrentLimit = "
                + peakCurrentLimit);
      }
      err = talon.configPeakCurrentDuration(peakCurrentLimitDuration, timeout);
      Errors.check(talon, "configPeakCurrentDuration", err, logger);
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

  int getTimeout() {
    return timeout;
  }

  void setTimeout(int timeout) {
    this.timeout = timeout;
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
