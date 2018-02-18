package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

/** Builder for {@link TalonConfiguration}. */
@ParametersAreNonnullByDefault
public class TalonConfigurationBuilder {

  @NotNull public static final String DEFAULT_NAME = "DEFAULT";

  // TalonConfiguration
  @NotNull public static final String NAME = "name";
  @NotNull private static final String MODE = "mode";

  // TalonConfiguration
  @NotNull private String name = DEFAULT_NAME;
  @NotNull private ControlMode mode = ControlMode.PercentOutput;
  private double setpointMax = 12;
  @Nullable private Encoder encoder;
  @Nullable private NeutralMode neutralMode;
  @Nullable private Boolean outputReversed;
  @Nullable private VelocityMeasPeriod velocityMeasurementPeriod;
  @Nullable private Integer velocityMeasurementWindow;
  @Nullable private LimitSwitch forwardLimitSwitch;
  @Nullable private LimitSwitch reverseLimitSwitch;
  @Nullable private SoftLimit forwardSoftLimit;
  @Nullable private SoftLimit reverseSoftLimit;
  @Nullable private Integer continuousCurrentLimit;
  @Nullable private Integer peakCurrentLimit;
  @Nullable private Integer peakCurrentLimitDuration;
  @Nullable private Double openLoopRampTime;
  @Nullable private Double voltageCompSaturation;

  // PIDTalonConfiguration
  @Nullable private Double closedLoopRampRate;
  @Nullable private Double forwardOutputVoltagePeak;
  @Nullable private Double reverseOutputVoltagePeak;
  @Nullable private Double forwardOutputVoltageNominal;
  @Nullable private Double reverseOutputVoltageNominal;
  @Nullable private Integer allowableClosedLoopError;
  @Nullable private Double nominalClosedLoopVoltage;
  @Nullable private Double pGain;
  @Nullable private Double iGain;
  @Nullable private Double dGain;
  @Nullable private Double fGain;
  @Nullable private Integer iZone;
  @Nullable private Integer profileSlot;

  // MotionMagicTalonConfiguration
  @Nullable private Integer motionMagicAcceleration;
  @Nullable private Integer motionMagicCruiseVelocity;

  /** Create a builder with defaults. */
  @Inject
  public TalonConfigurationBuilder() {}

  public TalonConfigurationBuilder(final TalonConfiguration config) {
    name = config.getName();
    setpointMax = config.getSetpointMax();
    encoder = config.getEncoder();
    neutralMode = config.getBrakeInNeutral();
    outputReversed = config.isOutputReversed();
    velocityMeasurementPeriod = config.getVelocityMeasurementPeriod();
    velocityMeasurementWindow = config.getVelocityMeasurementWindow();
    forwardLimitSwitch = config.getForwardLimitSwitch();
    reverseLimitSwitch = config.getReverseLimitSwitch();
    forwardSoftLimit = config.getForwardSoftLimit();
    reverseSoftLimit = config.getReverseSoftLimit();
    continuousCurrentLimit = config.getContinuousCurrentLimit();
    peakCurrentLimit = config.getPeakCurrentLimit();
    peakCurrentLimitDuration = config.getPeakCurrentLimitDuration();
    openLoopRampTime = config.getOpenLoopRampTime();
    voltageCompSaturation = config.getVoltageCompSaturation();

    if (config instanceof VoltageTalonConfiguration) {
      mode = ControlMode.PercentOutput;
      return;
    } else if (config instanceof SpeedTalonConfiguration) {
      mode = ControlMode.Velocity;
    } else if (config instanceof PositionTalonConfiguration) {
      mode = ControlMode.Position;
    } else if (config instanceof MotionMagicTalonConfiguration) {
      mode = ControlMode.MotionMagic;
      MotionMagicTalonConfiguration mm = (MotionMagicTalonConfiguration) config;
      motionMagicAcceleration = mm.getMotionMagicAcceleration();
      motionMagicCruiseVelocity = mm.getMotionMagicCruiseVelocity();
    } else {
      throw new AssertionError(config.getClass().getCanonicalName());
    }
    PIDTalonConfiguration pid = (PIDTalonConfiguration) config;
    voltageCompSaturation = pid.getVoltageCompSaturation();
    forwardOutputVoltagePeak = pid.getForwardOutputVoltagePeak();
    reverseOutputVoltagePeak = pid.getReverseOutputVoltagePeak();
    forwardOutputVoltageNominal = pid.getForwardOutputVoltageNominal();
    reverseOutputVoltageNominal = pid.getReverseOutputVoltageNominal();
    allowableClosedLoopError = pid.getAllowableClosedLoopError();
    pGain = pid.getPGain();
    iGain = pid.getIGain();
    dGain = pid.getDGain();
    fGain = pid.getFGain();
    iZone = pid.getIZone();
    profileSlot = pid.getProfileSlot();
  }

  /**
   * Create a {@link TalonConfiguration} based on supplied config.
   *
   * @param config the configuration
   * @return the TalonConfiguration
   * @throws IllegalArgumentException if mode is missing from config
   * @throws UnsupportedOperationException if mode not implemented yet
   */
  @NotNull
  public static TalonConfiguration create(Toml config) {
    TalonConfiguration talonConfiguration = null;
    ControlMode mode = getMode(config);
    switch (mode) {
      case PercentOutput:
        talonConfiguration = config.to(VoltageTalonConfiguration.class);
        break;
      case Position:
        talonConfiguration = config.to(PositionTalonConfiguration.class);
        break;
      case Velocity:
        talonConfiguration = config.to(SpeedTalonConfiguration.class);
        break;
      case MotionMagic:
        talonConfiguration = config.to(MotionMagicTalonConfiguration.class);
        break;
      case MotionMagicArc:
      case MotionProfileArc:
      case Current:
      case Follower:
      case MotionProfile:
      case Disabled:
        throw new UnsupportedOperationException(mode.name());
    }
    return talonConfiguration;
  }

  static ControlMode getMode(Toml config) {
    String mode = config.getString(MODE);
    if (mode == null) {
      throw new IllegalArgumentException("mode missing from configuration");
    }
    return ControlMode.valueOf(mode);
  }

  /**
   * Get the {@code Toml} String representation for the {@link TalonConfiguration} that would be
   * built by this builder.
   *
   * @return the Toml String.
   */
  public String getToml() {
    TalonConfiguration talonConfiguration = build();
    TomlWriter writer = new TomlWriter();
    return writer.write(talonConfiguration);
  }

  /**
   * Creates a new {@link TalonConfiguration} with the provided settings.
   *
   * @return a new TalonConfiguration.
   */
  @NotNull
  public TalonConfiguration build() {
    TalonConfiguration tc = null;
    switch (mode) {
      case PercentOutput:
        tc =
            new VoltageTalonConfiguration(
                name,
                setpointMax,
                encoder,
                neutralMode,
                outputReversed,
                velocityMeasurementPeriod,
                velocityMeasurementWindow,
                forwardLimitSwitch,
                reverseLimitSwitch,
                forwardSoftLimit,
                reverseSoftLimit,
                continuousCurrentLimit,
                peakCurrentLimit,
                peakCurrentLimitDuration,
                openLoopRampTime,
                voltageCompSaturation);
        break;
      case Position:
        tc =
            new PositionTalonConfiguration(
                name,
                setpointMax,
                encoder,
                neutralMode,
                outputReversed,
                velocityMeasurementPeriod,
                velocityMeasurementWindow,
                forwardLimitSwitch,
                reverseLimitSwitch,
                forwardSoftLimit,
                reverseSoftLimit,
                continuousCurrentLimit,
                peakCurrentLimit,
                peakCurrentLimitDuration,
                openLoopRampTime,
                voltageCompSaturation,
                closedLoopRampRate,
                forwardOutputVoltagePeak,
                reverseOutputVoltagePeak,
                forwardOutputVoltageNominal,
                reverseOutputVoltageNominal,
                allowableClosedLoopError,
                nominalClosedLoopVoltage,
                pGain,
                iGain,
                dGain,
                fGain,
                iZone,
                profileSlot);
        break;
      case Velocity:
        tc =
            new SpeedTalonConfiguration(
                name,
                setpointMax,
                encoder,
                neutralMode,
                outputReversed,
                velocityMeasurementPeriod,
                velocityMeasurementWindow,
                forwardLimitSwitch,
                reverseLimitSwitch,
                forwardSoftLimit,
                reverseSoftLimit,
                continuousCurrentLimit,
                peakCurrentLimit,
                peakCurrentLimitDuration,
                openLoopRampTime,
                voltageCompSaturation,
                closedLoopRampRate,
                forwardOutputVoltagePeak,
                reverseOutputVoltagePeak,
                forwardOutputVoltageNominal,
                reverseOutputVoltageNominal,
                allowableClosedLoopError,
                nominalClosedLoopVoltage,
                pGain,
                iGain,
                dGain,
                fGain,
                iZone,
                profileSlot);
        break;
      case MotionMagic:
        tc =
            new MotionMagicTalonConfiguration(
                name,
                setpointMax,
                encoder,
                neutralMode,
                outputReversed,
                velocityMeasurementPeriod,
                velocityMeasurementWindow,
                forwardLimitSwitch,
                reverseLimitSwitch,
                forwardSoftLimit,
                reverseSoftLimit,
                continuousCurrentLimit,
                peakCurrentLimit,
                peakCurrentLimitDuration,
                openLoopRampTime,
                voltageCompSaturation,
                closedLoopRampRate,
                forwardOutputVoltagePeak,
                reverseOutputVoltagePeak,
                forwardOutputVoltageNominal,
                reverseOutputVoltageNominal,
                allowableClosedLoopError,
                nominalClosedLoopVoltage,
                pGain,
                iGain,
                dGain,
                fGain,
                iZone,
                profileSlot,
                motionMagicAcceleration,
                motionMagicCruiseVelocity);
        break;
      case MotionMagicArc:
      case MotionProfileArc:
      case Follower:
      case MotionProfile:
      case Current:
      case Disabled:
        throw new UnsupportedOperationException(mode.name());
    }
    return tc;
  }

  /**
   * Set the configuration name, must not be null.
   *
   * @param name the configuration name.
   * @return this builder.
   * @throws IllegalArgumentException if name is null
   */
  @NotNull
  public TalonConfigurationBuilder name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Set the Talon mode, must not be null.
   *
   * @param mode the mode.
   * @return this builder.
   * @throws IllegalArgumentException if mode is null
   */
  @NotNull
  public TalonConfigurationBuilder mode(ControlMode mode) {
    this.mode = mode;
    return this;
  }

  /**
   * Set the Talon max setpoint.
   *
   * @param setpointMax the max setpoint.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder setpointMax(double setpointMax) {
    this.setpointMax = setpointMax;
    return this;
  }

  /**
   * Configure the Talon encoder in use.
   *
   * @param feedbackDevice the encoder type, must not be null.
   * @param isReversed encoder phase is reversed with respect to motor output.
   * @return this builder.
   * @throws IllegalArgumentException if feedbackDevice is null
   */
  @NotNull
  public TalonConfigurationBuilder encoder(FeedbackDevice feedbackDevice, boolean isReversed) {
    this.encoder = new Encoder(feedbackDevice, isReversed);
    return this;
  }

  /**
   * Configure the Talon encoder in use.
   *
   * @param feedbackDevice the encoder type, must not be null.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder encoder(FeedbackDevice feedbackDevice) {
    if (encoder == null) {
      encoder = new Encoder(feedbackDevice);
    } else {
      encoder = encoder.copyWithEncoder(feedbackDevice);
    }
    return this;
  }

  /**
   * Configure the Talon encoder.
   *
   * @param reversed true if encoder is out of phase with motor output.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder encoderReversed(boolean reversed) {
    if (encoder == null) {
      encoder = new Encoder(reversed);
    } else {
      encoder = encoder.copyWithReversed(reversed);
    }
    return this;
  }

  /**
   * Configure if Talon brakes in neutral.
   *
   * @param brakeInNeutral true if Talon brakes in neutral
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder brakeInNeutral(boolean brakeInNeutral) {
    this.neutralMode = brakeInNeutral ? NeutralMode.Brake : NeutralMode.Coast;
    return this;
  }

  /**
   * Configure if Talon output is reversed.
   *
   * @param outputReversed true if Talon output is reversed.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder outputReversed(boolean outputReversed) {
    this.outputReversed = outputReversed;
    return this;
  }

  /**
   * Configure the Talon velocity measurement period.
   *
   * @param velocityMeasurementPeriod the velocity measurement period.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder velocityMeasurementPeriod(
      VelocityMeasPeriod velocityMeasurementPeriod) {
    this.velocityMeasurementPeriod = velocityMeasurementPeriod;
    return this;
  }

  /**
   * Configure the Talon velocity measurement window.
   *
   * @param velocityMeasurementWindow the velocity measurement window.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder velocityMeasurementWindow(int velocityMeasurementWindow) {
    this.velocityMeasurementWindow = velocityMeasurementWindow;
    return this;
  }

  /**
   * Enable and configure the Talon forward limit switch.
   *
   * @param normallyOpen limit switch is normally open, null to disable.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder forwardLimitSwitch(@Nullable Boolean normallyOpen) {
    if (normallyOpen != null) {
      forwardLimitSwitch = new LimitSwitch(true, normallyOpen);
    } else {
      forwardLimitSwitch = null;
    }
    return this;
  }

  /**
   * Enable and configure the Talon reverse limit switch.
   *
   * @param normallyOpen limit switch is normally open, null to disable.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder reverseLimitSwitch(@Nullable Boolean normallyOpen) {
    if (normallyOpen != null) {
      reverseLimitSwitch = new LimitSwitch(true, normallyOpen);
    } else {
      reverseLimitSwitch = null;
    }
    return this;
  }

  /**
   * Enable and configure the Talon forward soft limit.
   *
   * @param forwardSoftLimit the soft limit, null to disable.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder forwardSoftLimit(@Nullable Integer forwardSoftLimit) {
    this.forwardSoftLimit = new SoftLimit(forwardSoftLimit);
    return this;
  }

  /**
   * Enable and configure the Talon reverse soft limit.
   *
   * @param reverseSoftLimit the soft limit, null to disable
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder reverseSoftLimit(@Nullable Integer reverseSoftLimit) {
    this.reverseSoftLimit = new SoftLimit(reverseSoftLimit);
    return this;
  }

  /**
   * Configure the Talon current limit.
   *
   * @param currentLimit the current limit.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder currentLimit(int currentLimit) {
    this.continuousCurrentLimit = currentLimit;
    return this;
  }

  /**
   * Configure the Talon peak current limit.
   *
   * @param currentLimit the current limit.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder peakCurrentLimit(int currentLimit) {
    this.peakCurrentLimit = currentLimit;
    return this;
  }

  /**
   * Configure the Talon peak current limit duration.
   *
   * @param duration the peak current limit duration.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder peakCurrentDuration(int duration) {
    this.peakCurrentLimitDuration = duration;
    return this;
  }


  /**
   * Set the voltage ramp rate. Limits the rate at which the throttle will change. Affects all
   * modes.
   *
   * @param voltageRampRate maximum change in voltage, in volts / sec.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder voltageRampRate(double voltageRampRate) {
    this.openLoopRampTime = voltageRampRate;
    return this;
  }

  /**
   * Configure the maximum output voltage for 100% output.
   *
   * @param voltageCompSaturation the maximum output voltage.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder voltageCompSaturation(double voltageCompSaturation) {
    this.voltageCompSaturation = voltageCompSaturation;
    return this;
  }

  /**
   * Set the closed loop ramp rate for the current profile. Limits the rate at which the throttle
   * will change. Only affects position and speed closed loop modes.
   *
   * @param closedLoopRampRate maximum change in voltage, in volts / sec.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder closedLoopRampRate(double closedLoopRampRate) {
    this.closedLoopRampRate = closedLoopRampRate;
    return this;
  }

  /**
   * Configure the peak forward and reverse output voltages.
   *
   * @param forward the forward peak output voltage allowed.
   * @param reverse the reverse peak output voltage allowed.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder outputVoltagePeak(double forward, double reverse) {
    this.forwardOutputVoltagePeak = forward;
    this.reverseOutputVoltagePeak = reverse;
    return this;
  }

  /**
   * Configure the nominal forward and reverse output voltages.
   *
   * @param forward the forward nominal output voltage allowed.
   * @param reverse the reverse nominal output voltage allowed.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder outputVoltageNominal(double forward, double reverse) {
    this.forwardOutputVoltageNominal = forward;
    this.reverseOutputVoltageNominal = reverse;
    return this;
  }

  /**
   * Configure the allowable closed-loop error.
   *
   * @param allowableClosedLoopError the allowable closed-loop error.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder allowableClosedLoopError(int allowableClosedLoopError) {
    this.allowableClosedLoopError = allowableClosedLoopError;
    return this;
  }

  /**
   * Configure the nominal closed-loop output voltage.
   *
   * @param nominalClosedLoopVoltage the nominal closed-loop output voltage.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder nominalClosedLoopVoltage(double nominalClosedLoopVoltage) {
    this.nominalClosedLoopVoltage = nominalClosedLoopVoltage;
    return this;
  }

  /**
   * Configure P.
   *
   * @param pGain value for P.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder P(double pGain) {
    this.pGain = pGain;
    return this;
  }

  /**
   * Configure I.
   *
   * @param iGain value for I.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder I(double iGain) {
    this.iGain = iGain;
    return this;
  }

  /**
   * Configure D.
   *
   * @param dGain value for D.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder D(double dGain) {
    this.dGain = dGain;
    return this;
  }

  /**
   * Configure F.
   *
   * @param fGain value for F.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder F(double fGain) {
    this.fGain = fGain;
    return this;
  }

  /**
   * Set the integration zone of the current closed-loop profile.
   *
   * @param iZone value for I-zone.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder iZone(int iZone) {
    this.iZone = iZone;
    return this;
  }

  /**
   * Set the profile slot of the current closed-loop profile.
   *
   * @param profileSlot value for profile slot.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder profileSlot(int profileSlot) {
    assert(profileSlot >=0 && profileSlot < 4);
    this.profileSlot = profileSlot;
    return this;
  }


  /**
   * Set the motion-magic acceleration.
   *
   * @param accel value for acceleration.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder motionMagicAcceleration(int accel) {
    this.motionMagicAcceleration = accel;
    return this;
  }

  /**
   * Set the motion-magic cruise velocity.
   *
   * @param vel value for cruise velocity.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder motionMagicCruiseVelocity(int vel) {
    this.motionMagicCruiseVelocity = vel;
    return this;
  }

  /**
   * Get the forward limit switch.
   *
   * @return the forward limit switch.
   */
  @Nullable
  public LimitSwitch getForwardLimitSwitch() {
    return forwardLimitSwitch;
  }

  /**
   * Set the forward limit switch.
   *
   * @param forwardLimitSwitch the forward limit switch.
   */
  public void setForwardLimitSwitch(@Nullable LimitSwitch forwardLimitSwitch) {
    this.forwardLimitSwitch = forwardLimitSwitch;
  }

  /**
   * Get the reverse limit switch.
   *
   * @return the reverse limit switch.
   */
  @Nullable
  public LimitSwitch getReverseLimitSwitch() {
    return reverseLimitSwitch;
  }

  /**
   * Set the reverse limit switch.
   *
   * @param reverseLimitSwitch the forward limit switch.
   */
  public void setReverseLimitSwitch(@Nullable LimitSwitch reverseLimitSwitch) {
    this.reverseLimitSwitch = reverseLimitSwitch;
  }

  /**
   * Get the forward soft limit.
   *
   * @return the forward soft limit
   */
  @Nullable
  public SoftLimit getForwardSoftLimit() {
    return forwardSoftLimit;
  }

  /**
   * Set the forward soft limit.
   *
   * @param forwardSoftLimit the forward soft limit.
   */
  public void setForwardSoftLimit(@Nullable SoftLimit forwardSoftLimit) {
    this.forwardSoftLimit = forwardSoftLimit;
  }

  /**
   * Get the reverse soft limit.
   *
   * @return the reverse soft limit
   */
  @Nullable
  public SoftLimit getReverseSoftLimit() {
    return reverseSoftLimit;
  }

  /**
   * Set the reverse soft limit.
   *
   * @param reverseSoftLimit the reverse soft limit.
   */
  public void setReverseSoftLimit(@Nullable SoftLimit reverseSoftLimit) {
    this.reverseSoftLimit = reverseSoftLimit;
  }
}
