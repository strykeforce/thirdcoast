package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import javax.inject.Inject;

/**
 * Builder for {@link TalonConfiguration}.
 */
public class TalonConfigurationBuilder {

  public final static String DEFAULT_NAME = "DEFAULT";

  // TalonConfiguration
  public final static String NAME = "name";
  public final static String MODE = "mode";

  // TalonConfiguration
  private String name = DEFAULT_NAME;
  private CANTalon.TalonControlMode mode = TalonControlMode.Voltage;
  private double setpointMax = 12;
  private Encoder encoder;
  private Boolean brakeInNeutral;
  private Boolean outputReversed;
  private VelocityMeasurementPeriod velocityMeasurementPeriod;
  private Integer velocityMeasurementWindow;
  private LimitSwitch forwardLimitSwitch;
  private LimitSwitch reverseLimitSwitch;
  private SoftLimit forwardSoftLimit;
  private SoftLimit reverseSoftLimit;
  private Integer currentLimit;

  // PIDTalonConfiguration
  private Double outputVoltageMax;
  private Double forwardOutputVoltagePeak;
  private Double reverseOutputVoltagePeak;
  private Double forwardOutputVoltageNominal;
  private Double reverseOutputVoltageNominal;
  private Integer allowableClosedLoopError;
  private Double nominalClosedLoopVoltage;
  private Double pGain;
  private Double iGain;
  private Double dGain;
  private Double fGain;
  private Integer iZone;

  /**
   * Create a builder with defaults.
   */
  @Inject
  public TalonConfigurationBuilder() {
  }

  public TalonConfigurationBuilder(final TalonConfiguration config) {
    name = config.getName() != null ? config.getName() : DEFAULT_NAME;
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
    Double setpointMax = config.getDouble("setpointMax");
    if (setpointMax == null) {
      throw new IllegalArgumentException("setpointMax missing from configuration");
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
  public TalonConfiguration build() {
    TalonConfiguration tc = null;
    switch (mode) {
      case Voltage:
        tc = new VoltageTalonConfiguration(name, setpointMax, encoder, brakeInNeutral,
            outputReversed, velocityMeasurementPeriod, velocityMeasurementWindow,
            forwardLimitSwitch, reverseLimitSwitch, forwardSoftLimit, reverseSoftLimit,
            currentLimit);
        break;
      case Position:
        tc = new PositionTalonConfiguration(name, setpointMax, encoder, brakeInNeutral,
            outputReversed, velocityMeasurementPeriod, velocityMeasurementWindow,
            forwardLimitSwitch, reverseLimitSwitch, forwardSoftLimit, reverseSoftLimit,
            currentLimit, outputVoltageMax, forwardOutputVoltagePeak, reverseOutputVoltagePeak,
            forwardOutputVoltageNominal, reverseOutputVoltageNominal, allowableClosedLoopError,
            nominalClosedLoopVoltage, pGain, iGain, dGain, fGain, iZone);
        break;
      case Speed:
        tc = new SpeedTalonConfiguration(name, setpointMax, encoder, brakeInNeutral,
            outputReversed, velocityMeasurementPeriod, velocityMeasurementWindow,
            forwardLimitSwitch, reverseLimitSwitch, forwardSoftLimit, reverseSoftLimit,
            currentLimit, outputVoltageMax, forwardOutputVoltagePeak, reverseOutputVoltagePeak,
            forwardOutputVoltageNominal, reverseOutputVoltageNominal, allowableClosedLoopError,
            nominalClosedLoopVoltage, pGain, iGain, dGain, fGain, iZone);
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
  public TalonConfigurationBuilder name(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name must not be null");
    }
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
  public TalonConfigurationBuilder mode(CANTalon.TalonControlMode mode) {
    if (mode == null) {
      throw new IllegalArgumentException("mode must not be null");
    }
    this.mode = mode;
    return this;
  }

  /**
   * Set the Talon max setpoint.
   *
   * @param setpointMax the max setpoint.
   * @return this builder.
   */
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
  public TalonConfigurationBuilder encoder(CANTalon.FeedbackDevice feedbackDevice,
      boolean isReversed, Integer ticksPerRevolution) {
    if (feedbackDevice == null) {
      throw new IllegalArgumentException("feedbackDevice must not be null");
    }
    this.encoder = new Encoder(feedbackDevice, isReversed, ticksPerRevolution);
    return this;
  }

  /**
   * Configure the Talon encoder in use.
   *
   * @param feedbackDevice the encoder type, must not be null.
   * @return this builder.
   */
  public TalonConfigurationBuilder encoder(CANTalon.FeedbackDevice feedbackDevice) {
    if (encoder == null) {
      encoder = new Encoder(feedbackDevice);
    } else {
      encoder = encoder.copyWithEncoder(feedbackDevice);
    }
    return this;
  }

  public TalonConfigurationBuilder encoderReversed(boolean reversed) {
    if (encoder == null) {
      encoder = new Encoder(reversed);
    }
    return this;
  }

  /**
   * Configure if Talon brakes in neutral.
   *
   * @param brakeInNeutral true if Talon brakes in neutral
   * @return this builder.
   */
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
  public TalonConfigurationBuilder velocityMeasurementWindow(int velocityMeasurementWindow) {
    this.velocityMeasurementWindow = velocityMeasurementWindow;
    return this;
  }

  /**
   * Enable and configure the Talon forward limit switch.
   *
   * @param normallyOpen limit switch is normally open.
   * @return this builder.
   */
  public TalonConfigurationBuilder forwardLimitSwitch(boolean normallyOpen) {
    forwardLimitSwitch = new LimitSwitch(true, normallyOpen);
    return this;
  }

  /**
   * Enable and configure the Talon reverse limit switch.
   *
   * @param normallyOpen limit switch is normally open.
   * @return this builder.
   */
  public TalonConfigurationBuilder reverseLimitSwitch(boolean normallyOpen) {
    reverseLimitSwitch = new LimitSwitch(true, normallyOpen);
    return this;
  }

  /**
   * Enable and configure the Talon forward soft limit.
   *
   * @param forwardSoftLimit the soft limit, null to disable.
   * @return this builder.
   */
  public TalonConfigurationBuilder forwardSoftLimit(Double forwardSoftLimit) {
    this.forwardSoftLimit = new SoftLimit(forwardSoftLimit);
    return this;
  }

  /**
   * Enable and configure the Talon reverse soft limit.
   *
   * @param reverseSoftLimit the soft limit, null to disable
   * @return this builder.
   */
  public TalonConfigurationBuilder reverseSoftLimit(Double reverseSoftLimit) {
    this.reverseSoftLimit = new SoftLimit(reverseSoftLimit);
    return this;
  }

  /**
   * Configure the Talon current limit, enabled if greater than 0.
   *
   * @param currentLimit the current limit.
   * @return this builder.
   */
  public TalonConfigurationBuilder currentLimit(int currentLimit) {
    this.currentLimit = currentLimit;
    return this;
  }

  /**
   * Configure the maximum output voltage in closed-loop modes.
   *
   * @param outputVoltageMax the maximum output voltage.
   * @return this builder.
   */
  public TalonConfigurationBuilder outputVoltageMax(double outputVoltageMax) {
    this.outputVoltageMax = outputVoltageMax;
    return this;
  }

  /**
   * Configure the peak forward and reverse output voltages.
   *
   * @param forward the forward peak output voltage allowed.
   * @param reverse the reverse peak output voltage allowed.
   * @return this builder.
   */
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
  public TalonConfigurationBuilder F(double fGain) {
    this.fGain = fGain;
    return this;
  }

  /**
   * Configure I-zone.
   *
   * @param iZone value for I-zone.
   * @return this builder.
   */
  public TalonConfigurationBuilder iZone(int iZone) {
    this.iZone = iZone;
    return this;
  }
}
