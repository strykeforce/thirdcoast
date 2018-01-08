package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.MotorSafety;
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

  static int TIMEOUT_MS = 0;

  // required
  @NotNull private final String name;
  @NotNull private final TalonControlMode mode;

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
  private final Integer currentLimit;
  private final Double openLoopRampTime;
  private final Double voltageCompSaturation;
  private Set<Integer> talonIds;

  TalonConfiguration(
      @NotNull String name,
      @NotNull TalonControlMode mode,
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
      Integer currentLimit,
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
    this.currentLimit = currentLimit;
    this.openLoopRampTime = openLoopRampTime;
    this.voltageCompSaturation = voltageCompSaturation;
  }

  /**
   * Configure a Talon with stored parameters.
   *
   * @param talon the Talon to registerWith
   */
  public void configure(@NotNull TalonSRX talon) {
    ((WPI_TalonSRX) talon).setSafetyEnabled(false);
    ((WPI_TalonSRX) talon).setExpiration(MotorSafety.DEFAULT_SAFETY_EXPIRATION);

    talon.selectProfileSlot(0, 0);
    Encoder enc = encoder != null ? encoder : Encoder.DEFAULT;
    enc.configure(talon);

    talon.setNeutralMode(neutralMode);
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

    //    LimitSwitch fls = forwardLimitSwitch != null ? forwardLimitSwitch : LimitSwitch.DEFAULT;
    //    LimitSwitch rls = reverseLimitSwitch != null ? reverseLimitSwitch : LimitSwitch.DEFAULT;
    // TODO: configForwardLimitSwitchSource
    //    talon.enableLimitSwitch(fls.isEnabled(), rls.isEnabled());
    //    if (fls.isEnabled()) {
    //      talon.ConfigFwdLimitSwitchNormallyOpen(fls.isNormallyOpen());
    //    }
    //    if (rls.isEnabled()) {
    //      talon.ConfigRevLimitSwitchNormallyOpen(rls.isNormallyOpen());
    //    }

    // TODO: configForwardSoftLimitEnable
    //    SoftLimit sl = forwardSoftLimit != null ? forwardSoftLimit : SoftLimit.DEFAULT;
    //    talon.enableForwardSoftLimit(sl.isEnabled());
    //    if (sl.isEnabled()) {
    //      talon.setForwardSoftLimit(sl.getPosition());
    //    }
    //    sl = reverseSoftLimit != null ? reverseSoftLimit : SoftLimit.DEFAULT;
    //    talon.enableReverseSoftLimit(sl.isEnabled());
    //    if (sl.isEnabled()) {
    //      talon.setReverseSoftLimit(sl.getPosition());
    //    }

    if (currentLimit != null && currentLimit > 0) {
      talon.configContinuousCurrentLimit(currentLimit, TIMEOUT_MS);
      talon.enableCurrentLimit(true);
    } else {
      talon.enableCurrentLimit(false);
    }
    talon.configOpenloopRamp(openLoopRampTime != null ? openLoopRampTime : 0, TIMEOUT_MS);
    talon.configVoltageCompSaturation(valueOrElseZero(voltageCompSaturation, 12), TIMEOUT_MS);
    addTalonId(talon.getDeviceID());
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
  public Integer getCurrentLimit() {
    return currentLimit;
  }

  @NotNull
  public TalonControlMode getMode() {
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

  // TODO: generate toString
}
