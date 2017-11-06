package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import com.ctre.CANTalon.VelocityMeasurementPeriod;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;

/**
 * Builder for {@link TalonConfiguration}.
 */
public class TalonConfigurationBuilder {

  public final static String DEFAULT_NAME = "DEFAULT";

  // TalonConfiguration
  public final static String NAME = "name";
  public final static String MODE = "mode";
  public final static String SETPOINT_MAX = "setpoint_max";
  public final static String FEEDBACK_DEVICE = "feedback_device";
  public final static String ENCODER_REVERSED = "encoder_reversed";
  public final static String TICKS_PER_REVOLUTION = "ticks_per_revolution";
  public final static String BRAKE_IN_NEUTRAL = "brake_in_neutral";
  public final static String OUTPUT_REVERSED = "output_reversed";
  public final static String VELOCITY_MEASUREMENT_PERIOD = "velocity_measurement_period";
  public final static String VELOCITY_MEASUREMENT_WINDOW = "velocity_measurement_window";
  public final static String FORWARD_LIMIT_SWITCH = "forward_limit_switch";
  public final static String REVERSE_LIMIT_SWITCH = "reverse_limit_switch";
  public final static String FORWARD_SOFT_LIMIT = "forward_soft_limit";
  public final static String REVERSE_SOFT_LIMIT = "reverse_soft_limit";
  public final static String CURRENT_LIMIT = "current_limit";

  // PIDTalonConfiguration
  public final static String OUTPUT_VOLTAGE_MAX = "output_voltage_max";
  public final static String FORWARD_OUTPUT_VOLTAGE_PEAK = "forward_output_voltage_peak";
  public final static String REVERSE_OUTPUT_VOLTAGE_PEAK = "reverse_output_voltage_peak";
  public final static String FORWARD_OUTPUT_VOLTAGE_NOMINAL = "forward_output_voltage_nominal";
  public final static String REVERSE_OUTPUT_VOLTAGE_NOMINAL = "reverse_output_voltage_nominal";
  public final static String ALLOWABLE_CLOSED_LOOP_ERROR = "allowable_closed_loop_error";
  public final static String NOMINAL_CLOSED_LOOP_VOLTAGE = "nominal_closed_loop_voltage";
  public final static String K_P = "P";
  public final static String K_I = "I";
  public final static String K_D = "D";
  public final static String K_F = "F";
  public final static String I_ZONE = "I_zone";


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
  public TalonConfigurationBuilder() {
  }

  /**
   * Create a builder based on supplied config.
   *
   * @param config the configuration
   */
  TalonConfigurationBuilder(UnmodifiableConfig config) {
    name = config.get(NAME);
    if (name == null) {
      throw new IllegalArgumentException("TALON configuration name parameter missing");
    }
    String mode = config.get(MODE);
    if (mode != null) {
      this.mode = TalonControlMode.valueOf(mode);
    }
    Double setpointMax = config.get(SETPOINT_MAX);
    if (setpointMax == null) {
      throw new IllegalArgumentException(
          String.format("TALON %s missing for %s", SETPOINT_MAX, name));
    }
    this.setpointMax = setpointMax;
    encoder = new Encoder((String) config.get(FEEDBACK_DEVICE), config.get(ENCODER_REVERSED),
        config.get(TICKS_PER_REVOLUTION));
    brakeInNeutral = config.get(BRAKE_IN_NEUTRAL);
    outputReversed = config.get(OUTPUT_REVERSED);
    Integer period = config.get(VELOCITY_MEASUREMENT_PERIOD);
    velocityMeasurementPeriod = period != null ? VelocityMeasurementPeriod.valueOf(period)
        : VelocityMeasurementPeriod.Period_100Ms;
    if (velocityMeasurementPeriod == null) {
      throw new IllegalArgumentException(
          "TALON " + VELOCITY_MEASUREMENT_PERIOD + " invalid: " + period);
    }
    velocityMeasurementWindow = config.get(VELOCITY_MEASUREMENT_WINDOW);
    forwardLimitSwitch = new LimitSwitch(config.get(FORWARD_LIMIT_SWITCH));
    reverseLimitSwitch = new LimitSwitch(config.get(REVERSE_LIMIT_SWITCH));
    forwardSoftLimit = new SoftLimit(config.get(FORWARD_SOFT_LIMIT));
    reverseSoftLimit = new SoftLimit(config.get(REVERSE_SOFT_LIMIT));
    currentLimit = config.get(CURRENT_LIMIT);

    // PIDTalonConfig
    outputVoltageMax = config.get(OUTPUT_VOLTAGE_MAX);
    forwardOutputVoltagePeak = config.get(FORWARD_OUTPUT_VOLTAGE_PEAK);
    reverseOutputVoltagePeak = config.get(REVERSE_OUTPUT_VOLTAGE_PEAK);
    forwardOutputVoltageNominal = config.get(FORWARD_OUTPUT_VOLTAGE_NOMINAL);
    reverseOutputVoltageNominal = config.get(REVERSE_OUTPUT_VOLTAGE_NOMINAL);
    allowableClosedLoopError = config.get(ALLOWABLE_CLOSED_LOOP_ERROR);
    nominalClosedLoopVoltage = config.get(NOMINAL_CLOSED_LOOP_VOLTAGE);
    // DisableNominalClosedLoopVoltage, SetNominalClosedLoopVoltage
    pGain = config.get(K_P);
    iGain = config.get(K_I);
    dGain = config.get(K_D);
    fGain = config.get(K_F);
    iZone = config.get(I_ZONE);
  }

  private static void addIfPresent(Config config, String path, Object value) {
    if (value != null) {
      config.add(path, value);
    }
  }

  public UnmodifiableConfig getConfig() {
    Config config = Config.inMemory();
    config.add(NAME, name);
    config.add(MODE, mode.name());
    config.add(SETPOINT_MAX, setpointMax);
    if (encoder != null) {
      config.add(FEEDBACK_DEVICE, encoder.getFeedbackDevice().name());
      config.add(ENCODER_REVERSED, encoder.isReversed());
      if (encoder.isUnitScalingEnabled()) {
        config.add(TICKS_PER_REVOLUTION, encoder.getTicksPerRevolution());
      }
    }
    addIfPresent(config, BRAKE_IN_NEUTRAL, brakeInNeutral);
    addIfPresent(config, OUTPUT_REVERSED, outputReversed);
    if (velocityMeasurementPeriod != null) {
      config.add(VELOCITY_MEASUREMENT_PERIOD, velocityMeasurementPeriod.name());
    }
    addIfPresent(config, VELOCITY_MEASUREMENT_WINDOW, velocityMeasurementWindow);
    if (forwardLimitSwitch != null) {
      config.add(FORWARD_LIMIT_SWITCH, forwardLimitSwitch.configString());
    }
    if (reverseLimitSwitch != null) {
      config.add(REVERSE_LIMIT_SWITCH, reverseLimitSwitch.configString());
    }
    if (forwardSoftLimit != null && forwardSoftLimit.isEnabled()) {
      config.add(FORWARD_SOFT_LIMIT, forwardSoftLimit.getValue());
    }
    if (reverseSoftLimit != null && reverseSoftLimit.isEnabled()) {
      config.add(REVERSE_SOFT_LIMIT, reverseSoftLimit.getValue());
    }
    addIfPresent(config, CURRENT_LIMIT, currentLimit);
    addIfPresent(config, OUTPUT_VOLTAGE_MAX, outputVoltageMax);
    addIfPresent(config, FORWARD_OUTPUT_VOLTAGE_PEAK, forwardOutputVoltagePeak);
    addIfPresent(config, REVERSE_OUTPUT_VOLTAGE_PEAK, reverseOutputVoltagePeak);
    addIfPresent(config, FORWARD_OUTPUT_VOLTAGE_NOMINAL, forwardOutputVoltageNominal);
    addIfPresent(config, REVERSE_OUTPUT_VOLTAGE_NOMINAL, reverseOutputVoltageNominal);
    addIfPresent(config, ALLOWABLE_CLOSED_LOOP_ERROR, allowableClosedLoopError);
    addIfPresent(config, NOMINAL_CLOSED_LOOP_VOLTAGE, nominalClosedLoopVoltage);
    addIfPresent(config, K_P, pGain);
    addIfPresent(config, K_I, iGain);
    addIfPresent(config, K_D, dGain);
    addIfPresent(config, K_F, fGain);
    addIfPresent(config, I_ZONE, iZone);

    return config.unmodifiable();
  }

  /**
   * Creates a new {@link TalonConfiguration} with the provided settings.
   *
   * @return a new TalonConfiguration.
   */
  TalonConfiguration build() {
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
    forwardLimitSwitch = new LimitSwitch(normallyOpen ? "normallyOpen" : "normallyClosed");
    return this;
  }

  /**
   * Enable and configure the Talon reverse limit switch.
   *
   * @param normallyOpen limit switch is normally open.
   * @return this builder.
   */
  public TalonConfigurationBuilder reverseLimitSwitch(boolean normallyOpen) {
    reverseLimitSwitch = new LimitSwitch(normallyOpen ? "normallyOpen" : "normallyClosed");
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
