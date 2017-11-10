package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

/**
 * Builder for {@link TalonConfiguration}.
 */
@ParametersAreNonnullByDefault
public class TalonConfigurationBuilder {

  @NotNull
  public final static String DEFAULT_NAME = "DEFAULT";

  // TalonConfiguration
  @NotNull
  public final static String NAME = "name";
  @NotNull
  public final static String MODE = "mode";

  // TalonConfiguration
  @NotNull
  private String name = DEFAULT_NAME;
  @NotNull
  private CANTalon.TalonControlMode mode = TalonControlMode.Voltage;
  private double setpointMax = 12;
  @Nullable
  private Encoder encoder;
  @Nullable
  private Boolean brakeInNeutral;
  @Nullable
  private Boolean outputReversed;
  @Nullable
  private VelocityMeasurementPeriod velocityMeasurementPeriod;
  @Nullable
  private Integer velocityMeasurementWindow;
  @Nullable
  private LimitSwitch forwardLimitSwitch;
  @Nullable
  private LimitSwitch reverseLimitSwitch;
  @Nullable
  private SoftLimit forwardSoftLimit;
  @Nullable
  private SoftLimit reverseSoftLimit;
  @Nullable
  private Integer currentLimit;
  @Nullable
  private Double voltageRampRate;

  // PIDTalonConfiguration
  @Nullable
  private Double outputVoltageMax;
  @Nullable
  private Double closedLoopRampRate;
  @Nullable
  private Double forwardOutputVoltagePeak;
  @Nullable
  private Double reverseOutputVoltagePeak;
  @Nullable
  private Double forwardOutputVoltageNominal;
  @Nullable
  private Double reverseOutputVoltageNominal;
  @Nullable
  private Integer allowableClosedLoopError;
  @Nullable
  private Double nominalClosedLoopVoltage;
  @Nullable
  private Double pGain;
  @Nullable
  private Double iGain;
  @Nullable
  private Double dGain;
  @Nullable
  private Double fGain;
  @Nullable
  private Integer iZone;

  /**
   * Create a builder with defaults.
   */
  @Inject
  public TalonConfigurationBuilder() {
  }

  public TalonConfigurationBuilder(final TalonConfiguration config) {
    name = config.getName();
    setpointMax = config.getSetpointMax();
    encoder = config.getEncoder();
    brakeInNeutral = config.isBrakeInNeutral();
    outputReversed = config.isOutputReversed();
    velocityMeasurementPeriod = config.getVelocityMeasurementPeriod();
    velocityMeasurementWindow = config.getVelocityMeasurementWindow();
    forwardLimitSwitch = config.getForwardLimitSwitch();
    reverseLimitSwitch = config.getReverseLimitSwitch();
    forwardSoftLimit = config.getForwardSoftLimit();
    reverseSoftLimit = config.getReverseSoftLimit();
    currentLimit = config.getCurrentLimit();

    if (config instanceof VoltageTalonConfiguration) {
      mode = TalonControlMode.Voltage;
      return;
    } else if (config instanceof SpeedTalonConfiguration) {
      mode = TalonControlMode.Speed;
    } else if (config instanceof PositionTalonConfiguration) {
      mode = TalonControlMode.Position;
    } else {
      throw new AssertionError(config.getClass().getCanonicalName());
    }
    PIDTalonConfiguration pid = (PIDTalonConfiguration) config;
    outputVoltageMax = pid.getOutputVoltageMax();
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
    CANTalon.TalonControlMode mode = getMode(config);
    switch (mode) {
      case Voltage:
        talonConfiguration = config.to(VoltageTalonConfiguration.class);
        break;
      case Position:
        talonConfiguration = config.to(PositionTalonConfiguration.class);
        break;
      case Speed:
        talonConfiguration = config.to(SpeedTalonConfiguration.class);
        break;
      case PercentVbus:
      case Current:
      case Follower:
      case MotionProfile:
      case MotionMagic:
      case Disabled:
        throw new UnsupportedOperationException(mode.name());
    }
    return talonConfiguration;
  }

  static CANTalon.TalonControlMode getMode(Toml config) {
    String mode = config.getString(MODE);
    if (mode == null) {
      throw new IllegalArgumentException("mode missing from configuration");
    }
    return CANTalon.TalonControlMode.valueOf(mode);
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
    System.out.println("writer.write(talonConfiguration) = " + writer.write(talonConfiguration));
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
      case Voltage:
        tc = new VoltageTalonConfiguration(name, setpointMax, encoder, brakeInNeutral,
            outputReversed, velocityMeasurementPeriod, velocityMeasurementWindow,
            forwardLimitSwitch, reverseLimitSwitch, forwardSoftLimit, reverseSoftLimit,
            currentLimit, voltageRampRate);
        break;
      case Position:
        tc = new PositionTalonConfiguration(name, setpointMax, encoder, brakeInNeutral,
            outputReversed, velocityMeasurementPeriod, velocityMeasurementWindow,
            forwardLimitSwitch, reverseLimitSwitch, forwardSoftLimit, reverseSoftLimit,
            currentLimit, voltageRampRate, outputVoltageMax, closedLoopRampRate,
            forwardOutputVoltagePeak,
            reverseOutputVoltagePeak, forwardOutputVoltageNominal, reverseOutputVoltageNominal,
            allowableClosedLoopError, nominalClosedLoopVoltage, pGain, iGain, dGain, fGain, iZone);
        break;
      case Speed:
        tc = new SpeedTalonConfiguration(name, setpointMax, encoder, brakeInNeutral,
            outputReversed, velocityMeasurementPeriod, velocityMeasurementWindow,
            forwardLimitSwitch, reverseLimitSwitch, forwardSoftLimit, reverseSoftLimit,
            currentLimit, voltageRampRate, outputVoltageMax, closedLoopRampRate,
            forwardOutputVoltagePeak,
            reverseOutputVoltagePeak, forwardOutputVoltageNominal, reverseOutputVoltageNominal,
            allowableClosedLoopError, nominalClosedLoopVoltage, pGain, iGain, dGain, fGain, iZone);
        break;
      case Follower:
      case MotionMagic:
      case Current:
      case MotionProfile:
      case Disabled:
      case PercentVbus:
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
  public TalonConfigurationBuilder mode(CANTalon.TalonControlMode mode) {
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
   * @param ticksPerRevolution encoder ticks per motor revolution, null to disable.
   * @return this builder.
   * @throws IllegalArgumentException if feedbackDevice is null
   */
  @NotNull
  public TalonConfigurationBuilder encoder(CANTalon.FeedbackDevice feedbackDevice,
      boolean isReversed, @Nullable Integer ticksPerRevolution) {
    this.encoder = new Encoder(feedbackDevice, isReversed, ticksPerRevolution);
    return this;
  }

  /**
   * Configure the Talon encoder in use.
   *
   * @param feedbackDevice the encoder type, must not be null.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder encoder(CANTalon.FeedbackDevice feedbackDevice) {
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
    this.brakeInNeutral = brakeInNeutral;
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
      VelocityMeasurementPeriod velocityMeasurementPeriod) {
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
  public TalonConfigurationBuilder forwardSoftLimit(@Nullable Double forwardSoftLimit) {
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
  public TalonConfigurationBuilder reverseSoftLimit(@Nullable Double reverseSoftLimit) {
    this.reverseSoftLimit = new SoftLimit(reverseSoftLimit);
    return this;
  }

  /**
   * Configure the Talon current limit, enabled if greater than 0.
   *
   * @param currentLimit the current limit.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder currentLimit(int currentLimit) {
    this.currentLimit = currentLimit;
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
    this.voltageRampRate = voltageRampRate;
    return this;
  }

  /**
   * Configure the maximum output voltage in closed-loop modes.
   *
   * @param outputVoltageMax the maximum output voltage.
   * @return this builder.
   */
  @NotNull
  public TalonConfigurationBuilder outputVoltageMax(double outputVoltageMax) {
    this.outputVoltageMax = outputVoltageMax;
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
   * Get the forward limit switch.
   *
   * @return the forward limit switch.
   */
  @Nullable
  public LimitSwitch getForwardLimitSwitch() {
    return forwardLimitSwitch;
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
   * Set the forward limit switch.
   *
   * @param forwardLimitSwitch the forward limit switch.
   */
  public void setForwardLimitSwitch(@Nullable LimitSwitch forwardLimitSwitch) {
    this.forwardLimitSwitch = forwardLimitSwitch;
  }

  /**
   * Set the reverse limit switch.
   *
   * @param reverseLimitSwitch the forward limit switch.
   */
  public void setReverseLimitSwitch(@Nullable LimitSwitch reverseLimitSwitch) {
    this.reverseLimitSwitch = reverseLimitSwitch;
  }
}
