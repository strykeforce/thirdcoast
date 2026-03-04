package org.strykeforce.controller.motorControl;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.*;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.TalonFXS;
import com.ctre.phoenix6.signals.*;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.units.measure.*;
import edu.wpi.first.wpilibj.DriverStation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.strykeforce.json.JsonTalonFXS;

public class SF_TalonFXS {
  private TalonFXS talonFXS;
  private JsonTalonFXS config;
  private TalonFXSConfiguration talonConfig;
  private TalonFXSConfigurator configurator;
  private BaseStatusSignal[] registeredStatusSignals = new BaseStatusSignal[0];
  private boolean isFDBus = false;
  private String configFileSuffix = "";
  private double controlRequestUpdateFreq = 100.0;

  // Status Signals
  StatusSignal<BridgeOutputValue> bridgeOutput;
  StatusSignal<Double> dutyCycle;
  StatusSignal<Voltage> motorVolt;
  StatusSignal<Voltage> supplyVolt;
  StatusSignal<Angle> position;
  StatusSignal<Angle> rotorPos;
  StatusSignal<AngularVelocity> velocity;
  StatusSignal<AngularVelocity> rotorVelocity;
  StatusSignal<AngularAcceleration> acceleration;

  // Temperature
  StatusSignal<Temperature> ancillaryTemp;
  StatusSignal<Temperature> deviceTemp;
  StatusSignal<Temperature> processorTemp;
  StatusSignal<Temperature> externalMotorTemp;

  // Differential
  StatusSignal<Angle> diffAvgPos;
  StatusSignal<AngularVelocity> diffAvgVel;
  StatusSignal<Angle> diffDiffPos;
  StatusSignal<AngularVelocity> diffDiffVel;
  StatusSignal<Double> diffOutput;

  // Feedback
  StatusSignal<ForwardLimitValue> fwdLim;
  StatusSignal<ReverseLimitValue> revLim;
  StatusSignal<Current> statorCurrent;
  StatusSignal<Current> supplyCurrent;
  StatusSignal<Current> torqueCurrent;
  StatusSignal<Angle> rawPulseWidthPosition;
  StatusSignal<Angle> rawQuadraturePosition;
  StatusSignal<AngularVelocity> rawPulseWidthVelocity;
  StatusSignal<AngularVelocity> rawQuadratureVelocity;

  // Closed Loop Output
  StatusSignal<Double> closedLoopError;
  StatusSignal<Double> closedLoopPout;
  StatusSignal<Double> closedLoopIout;
  StatusSignal<Double> closedLoopDout;
  StatusSignal<Double> closedLoopFF;
  StatusSignal<Double> closedLoopOut;
  StatusSignal<Double> closedLoopRef;
  StatusSignal<Double> closedLoopRefSlope;
  StatusSignal<Integer> closedLoopSlot;

  // Differential Closed Loop
  StatusSignal<Double> closedLoopDiffError;
  StatusSignal<Double> closedLoopDiffPout;
  StatusSignal<Double> closedLoopDiffIout;
  StatusSignal<Double> closedLoopDiffDout;
  StatusSignal<Double> closedLoopDiffFF;
  StatusSignal<Double> closedLoopDiffOut;
  StatusSignal<Double> closedLoopDiffRef;
  StatusSignal<Double> closedLoopDiffRefSlope;
  StatusSignal<Integer> closedLoopDiffSlot;

  // Booleans
  StatusSignal<Boolean> isProLic;
  StatusSignal<Boolean> mmIsRunning;

  // Other
  StatusSignal<ControlModeValue> controlMode;
  StatusSignal<DifferentialControlModeValue> diffControlMode;
  StatusSignal<Voltage> fiveVoltRail;
  StatusSignal<Voltage> analogVoltRail;

  // Control Requests

  // Open Loop
  private DutyCycleOut dutyCycleOut = new DutyCycleOut(0.0);
  private VoltageOut voltageOut = new VoltageOut(0.0);
  private TorqueCurrentFOC torqueCurrentFOC = new TorqueCurrentFOC(0.0);

  // Follower
  private Follower follower;
  private StrictFollower strictFollower;

  // Position
  private PositionDutyCycle positionDutyCycle;
  private PositionVoltage positionVoltage;
  private PositionTorqueCurrentFOC positionTorqueCurrentFOC;

  // Velocity
  private VelocityDutyCycle velocityDutyCycle = new VelocityDutyCycle(0.0);
  private VelocityVoltage velocityVoltage = new VelocityVoltage(0.0);
  private VelocityTorqueCurrentFOC velocityTorqueCurrentFOC = new VelocityTorqueCurrentFOC(0.0);

  // Standard Motion Magic
  private MotionMagicDutyCycle motionMagicDutyCycle = new MotionMagicDutyCycle(0.0);
  private MotionMagicVoltage motionMagicVoltage = new MotionMagicVoltage(0.0);
  private MotionMagicTorqueCurrentFOC motionMagicTorqueCurrentFOC =
      new MotionMagicTorqueCurrentFOC(0.0);

  // Velocity Motion Magic
  private MotionMagicVelocityDutyCycle motionMagicVelocityDutyCycle =
      new MotionMagicVelocityDutyCycle(0.0);
  private MotionMagicVelocityVoltage motionMagicVelocityVoltage =
      new MotionMagicVelocityVoltage(0.0);
  private MotionMagicVelocityTorqueCurrentFOC motionMagicVelocityTorqueCurrentFOC =
      new MotionMagicVelocityTorqueCurrentFOC(0.0);

  // Expo Motion Magic
  private MotionMagicExpoDutyCycle motionMagicExpoDutyCycle = new MotionMagicExpoDutyCycle(0.0);
  private MotionMagicExpoVoltage motionMagicExpoVoltage = new MotionMagicExpoVoltage(0.0);
  private MotionMagicExpoTorqueCurrentFOC motionMagicExpoTorqueCurrentFOC =
      new MotionMagicExpoTorqueCurrentFOC(0.0);

  // Dynamic Motion Magic
  private DynamicMotionMagicDutyCycle dynamicMotionMagicDutyCycle =
      new DynamicMotionMagicDutyCycle(0.0, 0.0, 0.0);
  private DynamicMotionMagicVoltage dynamicMotionMagicVoltage =
      new DynamicMotionMagicVoltage(0.0, 0.0, 0.0);
  private DynamicMotionMagicTorqueCurrentFOC dynamicMotionMagicTorqueCurrentFOC =
      new DynamicMotionMagicTorqueCurrentFOC(0.0, 0.0, 0.0);

  // Dynamic Motion Magic Expo
  private DynamicMotionMagicExpoDutyCycle dynamicMotionMagicExpoDutyCycle =
      new DynamicMotionMagicExpoDutyCycle(0.0, 0.0, 0.0);
  private DynamicMotionMagicExpoVoltage dynamicMotionMagicExpoVoltage =
      new DynamicMotionMagicExpoVoltage(0.0, 0.0, 0.0);
  private DynamicMotionMagicExpoTorqueCurrentFOC dynamicMotionMagicExpoTorqueCurrentFOC =
      new DynamicMotionMagicExpoTorqueCurrentFOC(0.0, 0.0, 0.0);

  // Differential
  private DifferentialDutyCycle differentialDutyCycle = new DifferentialDutyCycle(0.0, 0.0);
  private DifferentialVoltage differentialVoltage = new DifferentialVoltage(0.0, 0.0);

  // Differential Position
  private DifferentialPositionDutyCycle differentialPositionDutyCycle =
      new DifferentialPositionDutyCycle(0.0, 0.0);
  private DifferentialPositionVoltage differentialPositionVoltage =
      new DifferentialPositionVoltage(0.0, 0.0);

  // Differential Velocity
  private DifferentialVelocityDutyCycle differentialVelocityDutyCycle =
      new DifferentialVelocityDutyCycle(0.0, 0.0);
  private DifferentialVelocityVoltage differentialVelocityVoltage =
      new DifferentialVelocityVoltage(0.0, 0.0);

  // Differential Motion Magic
  private DifferentialMotionMagicDutyCycle differentialMotionMagicDutyCycle =
      new DifferentialMotionMagicDutyCycle(0.0, 0.0);
  private DifferentialMotionMagicVoltage differentialMotionMagicVoltage =
      new DifferentialMotionMagicVoltage(0.0, 0.0);

  // Differential Follower
  private DifferentialFollower differentialFollower =
      new DifferentialFollower(0, MotorAlignmentValue.Aligned);
  private DifferentialStrictFollower differentialStrictFollower = new DifferentialStrictFollower(0);

  private CTRE_Units openLoopUnits;
  private CTRE_Units closedLoopUnits;
  private int activeSlot;
  private int differentialSlot;
  private MotionMagicType motionMagicType;
  private MotorAlignmentValue opposeMain;
  private CTRE_FollowerType followerType;
  private CTRE_FollowerConfig followerConfig;
  private int leaderID;
  private double torqueCurrentDeadband;
  private double torqueCurrentMax;
  private boolean useFOC;
  private CTRE_ClosedLoopType closedLoopType;
  private CTRE_DifferentialType differentialType;
  private boolean useTimesync;
  private boolean ignoreHWlimits;
  private boolean ignoreSWlimits;
  private boolean limitFwdMotion;
  private boolean limitRevMotion;
  private boolean overrideNeutral;
  private NeutralModeValue neutralOutput;
  private int id;

  /**
   * Constructor for a custom wrapper around a CTRE TalonFXS
   *
   * @param id - CAN bus id of the motor controller
   * @param canBus - CANBus the controller is wired to
   */
  public SF_TalonFXS(int id, CANBus canBus) {
    this(id, canBus, "");
  }

  //  public SF_TalonFXS(int id, String canBus) {
  //    this(id, canBus, "");
  //  }

  /**
   * Constructor for a custom wrapper around a CTRE TalonFXS
   *
   * @param id - CAN bus id of the motor controller
   * @param canBus - CANBus the controller is wired to
   * @param configSuffix - JSON config file suffix
   */
  public SF_TalonFXS(int id, CANBus canBus, String configSuffix) {
    talonFXS = new TalonFXS(id, canBus);
    this.id = id;
    this.configFileSuffix = configSuffix;
    isFDBus = canBus.isNetworkFD();
    loadFromJSON(configSuffix);
    setupControlRequests();
  }

  //  public SF_TalonFXS(int id, String canBus, String configSuffix) {
  //    talonFXS = new TalonFXS(id, canBus);
  //    this.id = id;
  //    this.configFileSuffix = configSuffix;
  //    isFDBus = canBus != "rio";
  //    loadFromJSON(configSuffix);
  //    setupControlRequests();
  //  }

  /**
   * Loads a full TalonFXS configuration from the config file
   *
   * @param suffix - JSON file suffix
   * @return true if successful
   */
  public boolean loadFromJSON(String suffix) {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<JsonTalonFXS> jsonAdapter = moshi.adapter(JsonTalonFXS.class);

    String configPath = "/home/lvuser/deploy/motorConfigs/" + id + "_FXS" + suffix + ".json";
    Path filePath = Path.of(configPath);
    String fileParse = "";
    try {
      fileParse = Files.readString(filePath);
      //            config = JsonAdapter.fromJson(fileParse);
    } catch (IOException e) {
      config = new JsonTalonFXS();
      applyJsonConfigs();
      String error =
          "Error loading json file for talonFX " + id + ": default values, " + e.toString();
      DriverStation.reportWarning(error, e.getStackTrace());
      return false;
    }
    try {
      config = jsonAdapter.fromJson(fileParse);

    } catch (IOException e) {
      config = new JsonTalonFXS();
      applyJsonConfigs();
      String error =
          "Error parsing json file for talonFX " + id + ": default values, " + e.toString();
      DriverStation.reportWarning(error, e.getStackTrace());
      return false;
    }
    applyJsonConfigs();
    return true;
  }

  /** Applies the configs loaded from the JSON file */
  private void applyJsonConfigs() {
    talonConfig = config.getTalonFXSConfig();
    //            .withAudio(config.getAudioConfigs())
    //            .withClosedLoopGeneral(config.getClosedLoopGeneralConfigs())
    //            .withClosedLoopRamps(config.getClosedLoopRampConfigs())
    //            .withCurrentLimits(config.getCurrentLimitConfigs())
    //            .withCustomParams(config.getCustomParamConfigs())
    //            .withDifferentialConstants(config.getDifferentialConstantsConfig())
    //            .withDifferentialSensors(config.getDifferentialSensorConfigs())
    //            .withExternalFeedback(config.getExternalFeedbackConfigs())
    //            .withHardwareLimitSwitch(config.getHardwareLimitSwitchConfigs())
    //            .withMotionMagic(config.getMotionMagicConfigs())
    //            .withMotorOutput(config.getMotorOutputConfigs())
    //            .withOpenLoopRamps(config.getOpenLoopRampConfigs())
    //            .withSlot0(config.getSlot0Configs())
    //            .withSlot1(config.getSlot1Configs())
    //            .withSlot2(config.getSlot2Configs())
    //            .withSoftwareLimitSwitch(config.getSoftwareLimitSwitchConfigs())
    //            .withCommutation(config.getCommutationConfigs())
    //            .withVoltage(config.getVoltageConfigs())
    //            .withExternalTemp(config.getExternalTempConfigs())
    //            .withCustomBrushlessMotor(config.getCustomBrushlessMotorConfigs());

    configurator = talonFXS.getConfigurator();
    configurator.apply(talonConfig);
    openLoopUnits = config.getOpenLoopUnits();
    closedLoopUnits = config.getClosedLoopUnits();
    activeSlot = config.getActiveSlot();
    differentialSlot = config.getDifferentialSlot();
    motionMagicType = config.getMotionMagicType();
    followerConfig = config.getFollowerConfig();
    followerType = config.getFollowerType();
    opposeMain = config.getOpposeMain() ? MotorAlignmentValue.Opposed : MotorAlignmentValue.Aligned;
    leaderID = config.getLeaderID();
    //    torqueCurrentDeadband;
    torqueCurrentMax = config.getTorqueCurrentMax();
    useFOC = config.getActiveFOC();
    closedLoopType = config.getClosedLoopType();
    differentialType = config.getDifferentialType();
    useTimesync = config.getUseTimesync();
    ignoreHWlimits = config.getIgnoreHwLimits();
    ignoreSWlimits = config.getIgnoreSwLimits();
    limitFwdMotion = config.getLimitFwdMotion();
    limitRevMotion = config.getLimitRevMotion();
    overrideNeutral = config.getOverrideNeutral();
    neutralOutput = config.getTalonFXSConfig().MotorOutput.NeutralMode;
  }

  /** Initializes all of the control request objects */
  private void setupControlRequests() {
    setupFollowerControlRequest();
    setupOpenLoopControlRequest();
    setupPositionControlRequest();
    setupVelocityControlRequest();
    setupMotionMagicControlRequest();
    setupDifferentialControlRequest();
  }

  /**
   * Sets the updatee frequency of all control requests as specified
   *
   * @param updateFreq - in Hz
   */
  public void setControlRequestUpdateFreq(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    controlRequestUpdateFreq = updateFreq;

    //    // open loop
    //    dutyCycleOut.UpdateFreqHz = updateFreq;
    //    voltageOut.UpdateFreqHz = updateFreq;
    //    torqueCurrentFOC.UpdateFreqHz = updateFreq;
    //
    //    // follower
    //    follower.UpdateFreqHz = updateFreq;
    //    strictFollower.UpdateFreqHz = updateFreq;
    //
    //    // position
    //    positionDutyCycle.UpdateFreqHz = updateFreq;
    //    positionVoltage.UpdateFreqHz = updateFreq;
    //    positionTorqueCurrentFOC.UpdateFreqHz = updateFreq;
    //
    //    // velocity
    //    velocityDutyCycle.UpdateFreqHz = updateFreq;
    //    velocityVoltage.UpdateFreqHz = updateFreq;
    //    velocityTorqueCurrentFOC.UpdateFreqHz = updateFreq;
    //
    //    // standard motion magic
    //    motionMagicDutyCycle.UpdateFreqHz = updateFreq;
    //    motionMagicVoltage.UpdateFreqHz = updateFreq;
    //    motionMagicTorqueCurrentFOC.UpdateFreqHz = updateFreq;
    //
    //    // velocity motion magic
    //    motionMagicVelocityDutyCycle.UpdateFreqHz = updateFreq;
    //    motionMagicVelocityVoltage.UpdateFreqHz = updateFreq;
    //    motionMagicVelocityTorqueCurrentFOC.UpdateFreqHz = updateFreq;
    //
    //    // expo motion magic
    //    motionMagicExpoDutyCycle.UpdateFreqHz = updateFreq;
    //    motionMagicExpoVoltage.UpdateFreqHz = updateFreq;
    //    motionMagicExpoTorqueCurrentFOC.UpdateFreqHz = updateFreq;
    //
    //    // dynamic motion magic
    //    dynamicMotionMagicDutyCycle.UpdateFreqHz = updateFreq;
    //    dynamicMotionMagicVoltage.UpdateFreqHz = updateFreq;
    //    dynamicMotionMagicTorqueCurrentFOC.UpdateFreqHz = updateFreq;
    //
    //    // dynamic motion magic expo
    //    dynamicMotionMagicExpoDutyCycle.UpdateFreqHz = updateFreq;
    //    dynamicMotionMagicExpoVoltage.UpdateFreqHz = updateFreq;
    //    dynamicMotionMagicExpoTorqueCurrentFOC.UpdateFreqHz = updateFreq;
    //
    //    // differential
    //    differentialDutyCycle.UpdateFreqHz = updateFreq;
    //    differentialVoltage.UpdateFreqHz = updateFreq;
    //
    //    // differential position
    //    differentialPositionDutyCycle.UpdateFreqHz = updateFreq;
    //    differentialPositionVoltage.UpdateFreqHz = updateFreq;
    //
    //    // differential velocity
    //    differentialVelocityDutyCycle.UpdateFreqHz = updateFreq;
    //    differentialVelocityVoltage.UpdateFreqHz = updateFreq;
    //
    //    // differential motion magic
    //    differentialMotionMagicDutyCycle.UpdateFreqHz = updateFreq;
    //    differentialMotionMagicVoltage.UpdateFreqHz = updateFreq;
    //
    //    // differential follower
    //    differentialFollower.UpdateFreqHz = updateFreq;
    //    differentialStrictFollower.UpdateFreqHz = updateFreq;
  }

  /** Sets up the control requests for follower modes */
  private void setupFollowerControlRequest() {
    switch (followerType) {
      case Standard ->
          follower = new Follower(leaderID, opposeMain).withUpdateFreqHz(controlRequestUpdateFreq);
      case Strict ->
          strictFollower = new StrictFollower(leaderID).withUpdateFreqHz(controlRequestUpdateFreq);
    }
  }

  /** Sets up the configured open loop control request object */
  private void setupOpenLoopControlRequest() {
    switch (openLoopUnits) {
      case Percent -> {
        dutyCycleOut =
            new DutyCycleOut(0.0)
                .withEnableFOC(useFOC)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWlimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideBrakeDurNeutral(overrideNeutral)
                .withUpdateFreqHz(controlRequestUpdateFreq);
      }
      case Voltage -> {
        voltageOut =
            new VoltageOut(0.0)
                .withEnableFOC(useFOC)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWlimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideBrakeDurNeutral(overrideNeutral)
                .withUpdateFreqHz(controlRequestUpdateFreq);
      }
      case Torque_Current -> {
        torqueCurrentFOC =
            new TorqueCurrentFOC(0.0)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWlimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideCoastDurNeutral(overrideNeutral)
                .withMaxAbsDutyCycle(torqueCurrentMax)
                .withDeadband(torqueCurrentDeadband)
                .withUpdateFreqHz(controlRequestUpdateFreq);
      }
    }
  }

  /** Sets up the configured position control request object */
  private void setupPositionControlRequest() {
    switch (closedLoopUnits) {
      case Percent -> {
        positionDutyCycle =
            new PositionDutyCycle(0.0)
                .withEnableFOC(useFOC)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWlimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideBrakeDurNeutral(overrideNeutral)
                .withSlot(activeSlot)
                .withUpdateFreqHz(controlRequestUpdateFreq);
      }
      case Voltage -> {
        positionVoltage =
            new PositionVoltage(0.0)
                .withEnableFOC(useFOC)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWlimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideBrakeDurNeutral(overrideNeutral)
                .withSlot(activeSlot)
                .withUpdateFreqHz(controlRequestUpdateFreq);
      }
      case Torque_Current -> {
        positionTorqueCurrentFOC =
            new PositionTorqueCurrentFOC(0.0)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWlimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideCoastDurNeutral(overrideNeutral)
                .withSlot(activeSlot)
                .withUpdateFreqHz(controlRequestUpdateFreq);
      }
    }
  }

  /** Sets up the configured velocity control request object */
  private void setupVelocityControlRequest() {
    switch (closedLoopUnits) {
      case Percent -> {
        velocityDutyCycle =
            new VelocityDutyCycle(0.0)
                .withEnableFOC(useFOC)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWlimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideBrakeDurNeutral(overrideNeutral)
                .withSlot(activeSlot)
                .withUpdateFreqHz(controlRequestUpdateFreq);
      }
      case Voltage -> {
        velocityVoltage =
            new VelocityVoltage(0.0)
                .withEnableFOC(useFOC)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWlimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideBrakeDurNeutral(overrideNeutral)
                .withSlot(activeSlot)
                .withUpdateFreqHz(controlRequestUpdateFreq);
      }
      case Torque_Current -> {
        velocityTorqueCurrentFOC =
            new VelocityTorqueCurrentFOC(0.0)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWlimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideCoastDurNeutral(overrideNeutral)
                .withSlot(activeSlot)
                .withUpdateFreqHz(controlRequestUpdateFreq);
      }
    }
  }

  /** Sets up the configured Motion Magic Control Request Object */
  private void setupMotionMagicControlRequest() {
    switch (motionMagicType) {
      case Standard -> {
        switch (closedLoopUnits) {
          case Percent -> {
            motionMagicDutyCycle =
                new MotionMagicDutyCycle(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Voltage -> {
            motionMagicVoltage =
                new MotionMagicVoltage(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Torque_Current -> {
            motionMagicTorqueCurrentFOC =
                new MotionMagicTorqueCurrentFOC(0.0)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideCoastDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
        }
      }
      case Velocity -> {
        switch (closedLoopUnits) {
          case Percent -> {
            motionMagicVelocityDutyCycle =
                new MotionMagicVelocityDutyCycle(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Voltage -> {
            motionMagicVelocityVoltage =
                new MotionMagicVelocityVoltage(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Torque_Current -> {
            motionMagicVelocityTorqueCurrentFOC =
                new MotionMagicVelocityTorqueCurrentFOC(0.0)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideCoastDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
        }
      }
      case Exponential -> {
        switch (closedLoopUnits) {
          case Percent -> {
            motionMagicExpoDutyCycle =
                new MotionMagicExpoDutyCycle(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Voltage -> {
            motionMagicExpoVoltage =
                new MotionMagicExpoVoltage(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Torque_Current -> {
            motionMagicExpoTorqueCurrentFOC =
                new MotionMagicExpoTorqueCurrentFOC(0.0)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideCoastDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
        }
      }
      case Dynamic -> {
        switch (closedLoopUnits) {
          case Percent -> {
            dynamicMotionMagicDutyCycle =
                new DynamicMotionMagicDutyCycle(0.0, 0.0, 0.0)
                    .withJerk(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Voltage -> {
            dynamicMotionMagicVoltage =
                new DynamicMotionMagicVoltage(0.0, 0.0, 0.0)
                    .withJerk(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Torque_Current -> {
            dynamicMotionMagicTorqueCurrentFOC =
                new DynamicMotionMagicTorqueCurrentFOC(0.0, 0.0, 0.0)
                    .withJerk(0.0)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideCoastDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
        }
      }
      case DynamicExponential -> {
        switch (closedLoopUnits) {
          case Percent -> {
            dynamicMotionMagicExpoDutyCycle =
                new DynamicMotionMagicExpoDutyCycle(0.0, 0.0, 0.0)
                    .withVelocity(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Voltage -> {
            dynamicMotionMagicExpoVoltage =
                new DynamicMotionMagicExpoVoltage(0.0, 0.0, 0.0)
                    .withVelocity(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Torque_Current -> {
            dynamicMotionMagicExpoTorqueCurrentFOC =
                new DynamicMotionMagicExpoTorqueCurrentFOC(0.0, 0.0, 0.0)
                    .withVelocity(0.0)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideCoastDurNeutral(overrideNeutral)
                    .withSlot(activeSlot)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
        }
      }
    }
  }

  /** Sets up the configured differential control request object */
  private void setupDifferentialControlRequest() {
    switch (differentialType) {
      case Follower -> {
        switch (followerType) {
          case Standard -> {
            differentialFollower =
                new DifferentialFollower(leaderID, opposeMain)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Strict -> {
            differentialStrictFollower =
                new DifferentialStrictFollower(leaderID).withUpdateFreqHz(controlRequestUpdateFreq);
          }
        }
      }
      case Open_Loop -> {
        switch (openLoopUnits) {
          case Percent -> {
            differentialDutyCycle =
                new DifferentialDutyCycle(0.0, 0.0)
                    .withUseTimesync(useTimesync)
                    .withEnableFOC(useFOC)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Voltage -> {
            differentialVoltage =
                new DifferentialVoltage(0.0, 0.0)
                    .withUseTimesync(useTimesync)
                    .withEnableFOC(useFOC)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Torque_Current -> {
            DriverStation.reportError(
                "Invalid Control Type: Differential Torque Current FOC", false);
          }
        }
      }
      case Position -> {
        switch (closedLoopUnits) {
          case Percent -> {
            differentialPositionDutyCycle =
                new DifferentialPositionDutyCycle(0.0, 0.0)
                    .withUseTimesync(useTimesync)
                    .withEnableFOC(useFOC)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Voltage -> {
            differentialPositionVoltage =
                new DifferentialPositionVoltage(0.0, 0.0)
                    .withUseTimesync(useTimesync)
                    .withEnableFOC(useFOC)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Torque_Current -> {
            DriverStation.reportError(
                "Invalid Control Type: Differential Position Torque Current FOC", false);
          }
        }
      }
      case Velocity -> {
        switch (closedLoopUnits) {
          case Percent -> {
            differentialVelocityDutyCycle =
                new DifferentialVelocityDutyCycle(0.0, 0.0)
                    .withUseTimesync(useTimesync)
                    .withEnableFOC(useFOC)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Voltage -> {
            differentialVelocityVoltage =
                new DifferentialVelocityVoltage(0.0, 0.0)
                    .withUseTimesync(useTimesync)
                    .withEnableFOC(useFOC)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Torque_Current -> {
            DriverStation.reportError(
                "Invalid Control Type: Differential Position Torque Current FOC", false);
          }
        }
      }
      case Motion_Magic -> {
        switch (closedLoopUnits) {
          case Percent -> {
            differentialMotionMagicDutyCycle =
                new DifferentialMotionMagicDutyCycle(0.0, 0.0)
                    .withUseTimesync(useTimesync)
                    .withEnableFOC(useFOC)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Voltage -> {
            differentialMotionMagicVoltage =
                new DifferentialMotionMagicVoltage(0.0, 0.0)
                    .withUseTimesync(useTimesync)
                    .withEnableFOC(useFOC)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withUpdateFreqHz(controlRequestUpdateFreq);
          }
          case Torque_Current -> {
            DriverStation.reportError(
                "Invalid Control Type: Differential Position Torque Current FOC", false);
          }
        }
      }
    }
  }

  /**
   * Runs the motor controller in the configured open loop mode based on the Open Loop Units
   *
   * @param setpoint open loop setpoint (units depend on configured unit of %, V, A)
   */
  public void runOpenLoop(double setpoint) {
    switch (openLoopUnits) {
      case Percent ->
          talonFXS.setControl(
              dutyCycleOut
                  .withOutput(setpoint)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWlimits)
                  .withUpdateFreqHz(controlRequestUpdateFreq));
      case Voltage ->
          talonFXS.setControl(
              voltageOut
                  .withOutput(setpoint)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWlimits)
                  .withUpdateFreqHz(controlRequestUpdateFreq));
      case Torque_Current ->
          talonFXS.setControl(
              torqueCurrentFOC
                  .withOutput(setpoint)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWlimits)
                  .withUpdateFreqHz(controlRequestUpdateFreq));
    }
  }

  /**
   * Runs the motor controller in the configured closed loop mode based on teh Closed Loop Units and
   * Mode
   *
   * @param setpoint the closed-looop setpoint (units depend on configured unit %, V, A)
   */
  public void runClosedLoop(double setpoint) {
    switch (closedLoopType) {
      case Position -> {
        switch (closedLoopUnits) {
          case Percent ->
              talonFXS.setControl(
                  positionDutyCycle
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Voltage ->
              talonFXS.setControl(
                  positionVoltage
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Torque_Current ->
              talonFXS.setControl(
                  positionTorqueCurrentFOC
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
        }
      }
      case Velocity -> {
        switch (closedLoopUnits) {
          case Percent ->
              talonFXS.setControl(
                  velocityDutyCycle
                      .withVelocity(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Voltage ->
              talonFXS.setControl(
                  velocityVoltage
                      .withVelocity(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Torque_Current ->
              talonFXS.setControl(
                  velocityTorqueCurrentFOC
                      .withVelocity(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
        }
      }
      case Motion_Magic -> {
        switch (motionMagicType) {
          case Standard -> {
            switch (closedLoopUnits) {
              case Percent ->
                  talonFXS.setControl(
                      motionMagicDutyCycle
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Voltage ->
                  talonFXS.setControl(
                      motionMagicVoltage
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Torque_Current ->
                  talonFXS.setControl(
                      motionMagicTorqueCurrentFOC
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
            }
          }
          case Velocity -> {
            switch (closedLoopUnits) {
              case Percent ->
                  talonFXS.setControl(
                      motionMagicVelocityDutyCycle
                          .withVelocity(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Voltage ->
                  talonFXS.setControl(
                      motionMagicVelocityVoltage
                          .withVelocity(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Torque_Current ->
                  talonFXS.setControl(
                      motionMagicVelocityTorqueCurrentFOC
                          .withVelocity(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
            }
          }
          case Exponential -> {
            switch (closedLoopUnits) {
              case Percent ->
                  talonFXS.setControl(
                      motionMagicExpoDutyCycle
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Voltage ->
                  talonFXS.setControl(
                      motionMagicExpoVoltage
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Torque_Current ->
                  talonFXS.setControl(
                      motionMagicExpoTorqueCurrentFOC
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
            }
          }
          case Dynamic -> {
            DriverStation.reportWarning(
                "Not supplying enough arguments for Dynamic Motion Magic", false);
            switch (closedLoopUnits) {
              case Percent ->
                  talonFXS.setControl(
                      dynamicMotionMagicDutyCycle
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Voltage ->
                  talonFXS.setControl(
                      dynamicMotionMagicVoltage
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Torque_Current ->
                  talonFXS.setControl(
                      dynamicMotionMagicTorqueCurrentFOC
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
            }
          }
          case DynamicExponential -> {
            DriverStation.reportWarning(
                "Not supplying enough arguments for Dynamic Motion Magic", false);
            switch (closedLoopUnits) {
              case Percent ->
                  talonFXS.setControl(
                      dynamicMotionMagicExpoDutyCycle
                          .withPosition(setpoint)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Voltage ->
                  talonFXS.setControl(
                      dynamicMotionMagicExpoVoltage
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Torque_Current ->
                  talonFXS.setControl(
                      dynamicMotionMagicExpoTorqueCurrentFOC
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
            }
          }
        }
      }
      case Follower -> {
        setupFollower(leaderID);
      }
    }
  }

  /**
   * Runs the motor controller in the configured closed loop mode based on teh Closed Loop Units and
   * Mode
   *
   * @param setpoint the closed-looop setpoint (units depend on configured unit %, V, A)
   * @param secondarySetpoint the secondary closed-loop setpoint (velocity for position,
   *     acceleration for velocity)
   */
  public void runClosedLoop(double setpoint, double secondarySetpoint) {
    switch (closedLoopType) {
      case Position -> {
        switch (closedLoopUnits) {
          case Percent ->
              talonFXS.setControl(
                  positionDutyCycle
                      .withPosition(setpoint)
                      .withVelocity(secondarySetpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Voltage ->
              talonFXS.setControl(
                  positionVoltage
                      .withPosition(setpoint)
                      .withVelocity(secondarySetpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Torque_Current ->
              talonFXS.setControl(
                  positionTorqueCurrentFOC
                      .withPosition(setpoint)
                      .withVelocity(secondarySetpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
        }
      }
      case Velocity -> {
        switch (closedLoopUnits) {
          case Percent ->
              talonFXS.setControl(
                  velocityDutyCycle
                      .withVelocity(setpoint)
                      .withAcceleration(secondarySetpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Voltage ->
              talonFXS.setControl(
                  velocityVoltage
                      .withVelocity(setpoint)
                      .withAcceleration(secondarySetpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Torque_Current ->
              talonFXS.setControl(
                  velocityTorqueCurrentFOC
                      .withVelocity(setpoint)
                      .withAcceleration(secondarySetpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
        }
      }
      case Motion_Magic -> {
        switch (motionMagicType) {
          case Standard -> {
            switch (closedLoopUnits) {
              case Percent ->
                  talonFXS.setControl(
                      motionMagicDutyCycle
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Voltage ->
                  talonFXS.setControl(
                      motionMagicVoltage
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Torque_Current ->
                  talonFXS.setControl(
                      motionMagicTorqueCurrentFOC
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
            }
          }
          case Velocity -> {
            switch (closedLoopUnits) {
              case Percent ->
                  talonFXS.setControl(
                      motionMagicVelocityDutyCycle
                          .withVelocity(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Voltage ->
                  talonFXS.setControl(
                      motionMagicVelocityVoltage
                          .withVelocity(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Torque_Current ->
                  talonFXS.setControl(
                      motionMagicVelocityTorqueCurrentFOC
                          .withVelocity(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
            }
          }
          case Exponential -> {
            switch (closedLoopUnits) {
              case Percent ->
                  talonFXS.setControl(
                      motionMagicExpoDutyCycle
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Voltage ->
                  talonFXS.setControl(
                      motionMagicExpoVoltage
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Torque_Current ->
                  talonFXS.setControl(
                      motionMagicExpoTorqueCurrentFOC
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
            }
          }
          case Dynamic -> {
            DriverStation.reportWarning(
                "Not supplying enough arguments for Dynamic Motion Magic", false);
            switch (closedLoopUnits) {
              case Percent ->
                  talonFXS.setControl(
                      dynamicMotionMagicDutyCycle
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Voltage ->
                  talonFXS.setControl(
                      dynamicMotionMagicVoltage
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Torque_Current ->
                  talonFXS.setControl(
                      dynamicMotionMagicTorqueCurrentFOC
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
            }
          }
          case DynamicExponential -> {
            DriverStation.reportWarning(
                "Not supplying enough arguments for Dynamic Motion Magic", false);
            switch (closedLoopUnits) {
              case Percent ->
                  talonFXS.setControl(
                      dynamicMotionMagicExpoDutyCycle
                          .withPosition(setpoint)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Voltage ->
                  talonFXS.setControl(
                      dynamicMotionMagicExpoVoltage
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
              case Torque_Current ->
                  talonFXS.setControl(
                      dynamicMotionMagicExpoTorqueCurrentFOC
                          .withPosition(setpoint)
                          .withSlot(activeSlot)
                          .withLimitReverseMotion(limitRevMotion)
                          .withLimitForwardMotion(limitFwdMotion)
                          .withIgnoreHardwareLimits(ignoreHWlimits)
                          .withIgnoreSoftwareLimits(ignoreSWlimits)
                          .withUpdateFreqHz(controlRequestUpdateFreq));
            }
          }
        }
      }
      case Follower -> {
        setupFollower(leaderID);
      }
    }
  }

  /**
   * Runs the motor in dynamic motion magic mode
   *
   * @param position position in rot
   * @param velocity velocity in rot/s
   * @param acceleration acceleration in rot/s^2
   * @param jerk jerk in rot/s^3
   */
  public void runDynamicMotionMagic(
      double position, double velocity, double acceleration, double jerk) {
    switch (closedLoopUnits) {
      case Percent ->
          talonFXS.setControl(
              dynamicMotionMagicDutyCycle
                  .withPosition(position)
                  .withVelocity(velocity)
                  .withAcceleration(acceleration)
                  .withJerk(jerk)
                  .withSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWlimits)
                  .withUpdateFreqHz(controlRequestUpdateFreq));
      case Voltage ->
          talonFXS.setControl(
              dynamicMotionMagicVoltage
                  .withPosition(position)
                  .withVelocity(velocity)
                  .withAcceleration(acceleration)
                  .withJerk(jerk)
                  .withSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWlimits)
                  .withUpdateFreqHz(controlRequestUpdateFreq));
      case Torque_Current ->
          talonFXS.setControl(
              dynamicMotionMagicTorqueCurrentFOC
                  .withPosition(position)
                  .withVelocity(velocity)
                  .withAcceleration(acceleration)
                  .withJerk(jerk)
                  .withSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWlimits)
                  .withUpdateFreqHz(controlRequestUpdateFreq));
    }
  }

  /**
   * Runs the motor in Dynamic Motion Magic Expo Mode
   *
   * @param position position in rots
   * @param kV request kV in V/rps
   * @param kA request kA in V/rps^2
   * @param velocity velocity in rot/s
   */
  public void runDynamicMotionMagicExpo(double position, double kV, double kA, double velocity) {
    switch (closedLoopUnits) {
      case Percent ->
          talonFXS.setControl(
              dynamicMotionMagicExpoDutyCycle
                  .withPosition(position)
                  .withKV(kV)
                  .withKA(kA)
                  .withVelocity(velocity)
                  .withSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWlimits)
                  .withUpdateFreqHz(controlRequestUpdateFreq));
      case Voltage ->
          talonFXS.setControl(
              dynamicMotionMagicExpoVoltage
                  .withPosition(position)
                  .withKV(kV)
                  .withKA(kA)
                  .withVelocity(velocity)
                  .withSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWlimits)
                  .withUpdateFreqHz(controlRequestUpdateFreq));
      case Torque_Current ->
          talonFXS.setControl(
              dynamicMotionMagicExpoTorqueCurrentFOC
                  .withPosition(position)
                  .withKV(kV)
                  .withKA(kA)
                  .withVelocity(velocity)
                  .withSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWlimits)
                  .withUpdateFreqHz(controlRequestUpdateFreq));
    }
  }

  /**
   * Run the motor controller in differential control mode
   *
   * @param average the average differential target
   * @param offset the difference differential target
   */
  public void runDifferential(double average, double offset) {
    switch (differentialType) {
      case Follower -> {
        switch (followerType) {
          case Standard ->
              talonFXS.setControl(
                  differentialFollower
                      .withLeaderID(leaderID)
                      .withMotorAlignment(opposeMain)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Strict ->
              talonFXS.setControl(
                  differentialStrictFollower
                      .withLeaderID(leaderID)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
        }
      }
      case Open_Loop -> {
        switch (openLoopUnits) {
          case Percent ->
              talonFXS.setControl(
                  differentialDutyCycle
                      .withAverageOutput(average)
                      .withDifferentialPosition(offset)
                      .withDifferentialSlot(differentialSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Voltage ->
              talonFXS.setControl(
                  differentialVoltage
                      .withAverageOutput(average)
                      .withDifferentialPosition(offset)
                      .withDifferentialSlot(differentialSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Torque_Current ->
              DriverStation.reportError(
                  "Invalid Control Type: Differential Torque Current FOC", false);
        }
      }
      case Position -> {
        switch (closedLoopUnits) {
          case Percent ->
              talonFXS.setControl(
                  differentialPositionDutyCycle
                      .withAveragePosition(average)
                      .withDifferentialPosition(offset)
                      .withDifferentialSlot(differentialSlot)
                      .withAverageSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Voltage ->
              talonFXS.setControl(
                  differentialPositionVoltage
                      .withAveragePosition(average)
                      .withDifferentialPosition(offset)
                      .withDifferentialSlot(differentialSlot)
                      .withAverageSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Torque_Current ->
              DriverStation.reportError(
                  "Invalid Control Type: Differential Position Torque Current FOC", false);
        }
      }
      case Velocity -> {
        switch (openLoopUnits) {
          case Percent ->
              talonFXS.setControl(
                  differentialVelocityDutyCycle
                      .withAverageVelocity(average)
                      .withDifferentialPosition(offset)
                      .withDifferentialSlot(differentialSlot)
                      .withAverageSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Voltage ->
              talonFXS.setControl(
                  differentialVelocityVoltage
                      .withAverageVelocity(average)
                      .withDifferentialPosition(offset)
                      .withDifferentialSlot(differentialSlot)
                      .withAverageSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Torque_Current ->
              DriverStation.reportError(
                  "Invalid Control Type: Differential Velocity Torque Current FOC", false);
        }
      }
      case Motion_Magic -> {
        switch (openLoopUnits) {
          case Percent ->
              talonFXS.setControl(
                  differentialMotionMagicDutyCycle
                      .withAveragePosition(average)
                      .withDifferentialPosition(offset)
                      .withDifferentialSlot(differentialSlot)
                      .withAverageSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Voltage ->
              talonFXS.setControl(
                  differentialMotionMagicVoltage
                      .withAveragePosition(average)
                      .withDifferentialPosition(offset)
                      .withDifferentialSlot(differentialSlot)
                      .withAverageSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWlimits)
                      .withUpdateFreqHz(controlRequestUpdateFreq));
          case Torque_Current ->
              DriverStation.reportError(
                  "Invalid Control Type: Differential Motion Magic Torque Current FOC", false);
        }
      }
    }
  }

  /** Force the motor controller into coast mode */
  public void forceCoast() {
    talonFXS.setControl(new CoastOut());
  }

  /** Forces the motor controller into brake mode */
  public void forceBrake() {
    talonFXS.setControl(new StaticBrake());
  }

  /**
   * Sets up the motor controller to follow the specified leader
   *
   * @param leaderID CAN id of leader talonFX
   */
  public void setupFollower(int leaderID) {
    this.leaderID = leaderID;
    switch (followerType) {
      case Standard ->
          talonFXS.setControl(
              follower.withLeaderID(leaderID).withUpdateFreqHz(controlRequestUpdateFreq));
      case Strict ->
          talonFXS.setControl(
              strictFollower.withLeaderID(leaderID).withUpdateFreqHz(controlRequestUpdateFreq));
    }
  }

  /**
   * Sets up the motor controller to differentially follow the specified leader
   *
   * @param leaderID the CAN id of the leader TalonFX
   */
  public void setupDifferentialFollower(int leaderID) {
    this.leaderID = leaderID;
    switch (followerType) {
      case Standard ->
          talonFXS.setControl(
              differentialFollower
                  .withLeaderID(leaderID)
                  .withUpdateFreqHz(controlRequestUpdateFreq));
      case Strict ->
          talonFXS.setControl(
              differentialStrictFollower
                  .withLeaderID(leaderID)
                  .withUpdateFreqHz(controlRequestUpdateFreq));
    }
  }

  /**
   * Plays an output tone at specified frequency
   *
   * @param freq in Hz
   */
  public void playTone(double freq) {
    talonFXS.setControl(new MusicTone(freq));
  }

  // Setters
  /**
   * Sets the active differential difference slot for differential control requests
   *
   * @param slot id 0-2
   */
  public void setDifferentialSlot(int slot) {
    this.differentialSlot = slot;
  }

  /**
   * Sets the active slot for closed loop requests
   *
   * @param slot id 0-2
   */
  public void setActiveSlot(int slot) {
    this.activeSlot = slot;
  }

  /**
   * Sets the open loop units used
   *
   * @param units %, V, A
   */
  public void setOpenLoopUnits(CTRE_Units units) {
    this.openLoopUnits = units;
  }

  /**
   * Sets the closed loop units used
   *
   * @param units %, V, A
   */
  public void setClosedLoopUnits(CTRE_Units units) {
    this.closedLoopUnits = units;
  }

  public void setClosedLoopType(CTRE_ClosedLoopType type) {
    this.closedLoopType = type;
  }

  public void setDifferentialType(CTRE_DifferentialType type) {
    differentialType = type;
  }

  public void setFollowerConfig(CTRE_FollowerConfig config) {
    followerConfig = config;
  }

  public void setFollowerType(CTRE_FollowerType type) {
    followerType = type;
  }

  public void setMotionMagicType(MotionMagicType type) {
    motionMagicType = type;
  }

  public void setUseFOC(Boolean useFOC) {
    this.useFOC = useFOC;
  }

  public void setOpposeMain(Boolean oppose) {
    if (oppose) opposeMain = MotorAlignmentValue.Opposed;
    else opposeMain = MotorAlignmentValue.Aligned;
  }

  public void setOverrideNeutral(Boolean override) {
    overrideNeutral = override;
  }

  /**
   * Updates the stator current config, calls to the configurator and could cause loop overruns
   *
   * @param enable true if current limit is enabled
   * @param limit in A
   */
  public void setStatorCurrentLimit(boolean enable, double limit) {
    CurrentLimitsConfigs current = talonConfig.CurrentLimits;
    current.withStatorCurrentLimit(limit).withStatorCurrentLimitEnable(enable);
    talonConfig.withCurrentLimits(current);
    configurator.apply(current);
  }

  /**
   * Updates the supply current limit config, calls to the configurator and could cause loop
   * overruns
   *
   * @param enable true if limit is enabled
   * @param limit in A
   * @param lowerLimit in A if limit is exceeded for lower time
   * @param lowerTime in sec
   */
  public void setSupplyCurrentLimit(
      boolean enable, double limit, double lowerLimit, double lowerTime) {
    CurrentLimitsConfigs current = talonConfig.CurrentLimits;
    current
        .withSupplyCurrentLimit(limit)
        .withSupplyCurrentLimitEnable(enable)
        .withSupplyCurrentLowerLimit(lowerLimit)
        .withSupplyCurrentLowerTime(lowerTime);
    talonConfig.withCurrentLimits(current);
    configurator.apply(current);
  }

  /**
   * Applies the supplied current limit config to the motor controller, can cause loop overruns
   *
   * @param config specified config ot apply
   */
  public void setCurrentLimits(CurrentLimitsConfigs config) {
    talonConfig.withCurrentLimits(config);
    configurator.apply(config);
  }

  /**
   * Turns on/off the hardware limit switch causing motor output to stop, can cause loop overruns
   *
   * @param fwdLim true if limit switch should stop fwd output
   * @param revLim true if limit switch should stop rev output
   */
  public void enableHardLimits(boolean fwdLim, boolean revLim) {
    HardwareLimitSwitchConfigs config = talonConfig.HardwareLimitSwitch;
    config.withForwardLimitEnable(fwdLim).withReverseLimitEnable(revLim);
    talonConfig.withHardwareLimitSwitch(config);
    configurator.apply(config);
  }

  /**
   * Update the limit fwd motion param for control requests to force a limit of fwd output No calls
   * to configurator - no loop overrun
   *
   * @param enable true if should limit
   */
  public void forceFwdLimit(boolean enable) {
    limitFwdMotion = enable;
  }

  /**
   * Update the limit rev motion param for control requests to force a limit of rev output No calls
   * to configurator - no loop overrun
   *
   * @param enable true if should limit
   */
  public void forceRevLimit(boolean enable) {
    limitRevMotion = enable;
  }

  /**
   * Updates the ignore software limits param for control requests, will cause the motor controller
   * to ignore ALL soft limits No calls to configurator - no loop overrun
   *
   * @param ignore true to ignore limits
   */
  public void dynamicIgnoreSwLimits(boolean ignore) {
    ignoreSWlimits = ignore;
  }

  /**
   * Updates the ignore hardware limits param for control requests, will cause the motor controller
   * to ignore ALL hard limits No calls to configurator - no loop overrun
   *
   * @param ignore true to ignore limit
   */
  public void dynamicIgnoreHwLimits(boolean ignore) {
    ignoreHWlimits = ignore;
  }

  /**
   * Updates to match suppplied motion magic config - calls to configurator, loop overruns
   *
   * @param config supplied config to update to
   */
  public void setMotionMagicConfigs(MotionMagicConfigs config) {
    talonConfig.withMotionMagic(config);
    configurator.apply(config);
  }

  /**
   * Updates the peak FWD and peak REV output to the specified values, calls to configurator - loop
   * overruns
   *
   * @param peakFwd new max fwd %
   * @param peakRev new max rev %
   */
  public void setPeakOutputPercent(double peakFwd, double peakRev) {
    MotorOutputConfigs motorOut = talonConfig.MotorOutput;
    motorOut.withPeakForwardDutyCycle(peakFwd).withPeakReverseDutyCycle(peakRev);
    talonConfig.withMotorOutput(motorOut);
    configurator.apply(motorOut);
  }

  /**
   * Updates the peak FWD and peak REV output to the specified values, calls to configurator - loop
   * overruns
   *
   * @param peakFwd new max fwd V
   * @param peakRev new max rev V
   */
  public void setPeakOutputVolt(double peakFwd, double peakRev) {
    VoltageConfigs config = talonConfig.Voltage;
    config.withPeakForwardVoltage(peakFwd).withPeakReverseVoltage(peakRev);
    talonConfig.withVoltage(config);
    configurator.apply(config);
  }

  /**
   * Updates the configured soft limits to the specified config, calls to configurator - loop
   * overruns
   *
   * @param config new configuration
   */
  public void setSoftLimits(SoftwareLimitSwitchConfigs config) {
    talonConfig.withSoftwareLimitSwitch(config);
    configurator.apply(config);
  }

  /**
   * Changes the enable state of the specified soft limits, calls to the configurator - loop
   * overruns
   *
   * @param enableFwd true if fwd enabled
   * @param enableRev true if rev enabled
   */
  public void enableSoftLimits(boolean enableFwd, boolean enableRev) {
    SoftwareLimitSwitchConfigs config = talonConfig.SoftwareLimitSwitch;
    config.withForwardSoftLimitEnable(enableFwd).withReverseSoftLimitEnable(enableRev);
    talonConfig.withSoftwareLimitSwitch(config);
    configurator.apply(config);
  }

  /**
   * Sets the current position of the motor controller, calls to configurator
   *
   * @param position new position in rotations
   */
  public void setPosition(double position) {
    talonFXS.setPosition(position);
  }

  // Getters
  /**
   * Returns the internal TalonFX - avoid using, kept for edge case situations
   *
   * @return internal talonFX object
   */
  public TalonFXS getTalonFXS() {
    return talonFXS;
  }

  /**
   * Returns the device ID of the motor controller
   *
   * @return integer id
   */
  public int getDeviceID() {
    return id;
  }

  /**
   * @return active slot integer [0,2]
   */
  public int getActiveSlot() {
    return activeSlot;
  }

  /**
   * @return differential slot [0,2]
   */
  public int getDifferentialSlot() {
    return differentialSlot;
  }

  /**
   * @return openLoop Units %, V, A
   */
  public CTRE_Units getOpenLoopUnits() {
    return openLoopUnits;
  }

  /**
   * @return closed loop units %, V, A
   */
  public CTRE_Units getClosedLoopUnits() {
    return closedLoopUnits;
  }

  public CTRE_ClosedLoopType getClosedLoopType() {
    return closedLoopType;
  }

  public CTRE_DifferentialType getDifferentialType() {
    return differentialType;
  }

  public CTRE_FollowerConfig getFollowerConfig() {
    return followerConfig;
  }

  public CTRE_FollowerType getFollowerType() {
    return followerType;
  }

  public MotionMagicType getMotionMagicType() {
    return motionMagicType;
  }

  public Boolean getActiveFOC() {
    return useFOC;
  }

  public Boolean getLimitFwdMotion() {
    return limitFwdMotion;
  }

  public Boolean getLimitRevMotion() {
    return limitRevMotion;
  }

  public Boolean getIgnoreHwLimits() {
    return ignoreHWlimits;
  }

  public Boolean getIgnoreSwLimits() {
    return ignoreSWlimits;
  }

  public Boolean getOpposeMain() {
    return opposeMain == MotorAlignmentValue.Opposed;
  }

  public Boolean getOverrideNeutral() {
    return overrideNeutral;
  }

  public double getControlRequestUpdateFreq() {
    return controlRequestUpdateFreq;
  }

  // Watchers
  /** Refreshes all registered status signals in one CAN bus call */
  public void refreshRegisteredSignals() {
    BaseStatusSignal.refreshAll(registeredStatusSignals);
  }

  /**
   * Returns an array of registered status signals
   *
   * @return BaseStatusSignal[]
   */
  public BaseStatusSignal[] getRegisteredSignals() {
    return registeredStatusSignals;
  }

  /**
   * Adds the supplied signal(s) to the registered status signals
   *
   * @param signals list of status signals to register
   */
  private void registerSignal(BaseStatusSignal... signals) {
    BaseStatusSignal[] newSignals =
        new BaseStatusSignal[registeredStatusSignals.length + signals.length];
    System.arraycopy(registeredStatusSignals, 0, newSignals, 0, registeredStatusSignals.length);
    System.arraycopy(signals, 0, newSignals, registeredStatusSignals.length, signals.length);
    registeredStatusSignals = newSignals;
  }

  /**
   * Checks if the listed status signal is registered
   *
   * @param signal item to check
   * @return true if registered
   */
  private boolean isRegistered(BaseStatusSignal signal) {
    for (int i = 0; i < registeredStatusSignals.length; i++) {
      if (registeredStatusSignals[i].getName() == signal.getName()) return true;
    }
    return false;
  }

  /** Adds the Bridge Output to the registered status signals, default update frequency */
  public void registerBridgeOutput() {
    registerBridgeOutput(100.0);
  }

  /**
   * Adds the Bridge Output to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerBridgeOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    bridgeOutput.setUpdateFrequency(updateFreq);
    bridgeOutput = talonFXS.getBridgeOutput();
    registerSignal(bridgeOutput);
  }

  /** Adds the duty cycle to the registered status signals, default update frequency */
  public void registerDutyCycle() {
    registerDutyCycle(100.0);
  }

  /**
   * Adds the duty cycle to teh registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDutyCycle(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    dutyCycle.setUpdateFrequency(updateFreq);
    dutyCycle = talonFXS.getDutyCycle();
    registerSignal(dutyCycle);
  }

  /** Adds the motor voltage to the registered status signals, default update freq */
  public void registerMotorVoltage() {
    registerMotorVoltage(100.0);
  }

  /**
   * Adds the motor voltage to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerMotorVoltage(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    motorVolt = talonFXS.getMotorVoltage();
    motorVolt.setUpdateFrequency(updateFreq);
    registerSignal(motorVolt);
  }

  /** Adds the supply voltage to the registered status signals, default update freq */
  public void registerSupplyVoltage() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerSupplyVoltage(updateFreq);
  }

  /**
   * Adds the supply voltage to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerSupplyVoltage(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    supplyVolt = talonFXS.getSupplyVoltage();
    supplyVolt.setUpdateFrequency(updateFreq);
    registerSignal(supplyVolt);
  }

  /** Adds the position to the registered status signals, default update freq */
  public void registerPosition() {
    double updateFreq = isFDBus ? 100.0 : 50.0;
    registerPosition(updateFreq);
  }

  /**
   * Adds the position to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerPosition(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    position = talonFXS.getPosition();
    position.setUpdateFrequency(updateFreq);
    registerSignal(position);
  }

  /** Adds the rotor position to the registered status signals, default update freq */
  public void registerRotorPosition() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerRotorPosition(updateFreq);
  }

  /**
   * Adds the rotor position to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerRotorPosition(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    rotorPos = talonFXS.getRotorPosition();
    rotorPos.setUpdateFrequency(updateFreq);
    registerSignal(rotorPos);
  }

  /** Adds the velocity to the registered status signals, default update freq */
  public void registerVelocity() {
    double updateFreq = isFDBus ? 100.0 : 50.0;
    registerVelocity(updateFreq);
  }

  /**
   * Adds the velocity to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerVelocity(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    velocity = talonFXS.getVelocity();
    velocity.setUpdateFrequency(updateFreq);
    registerSignal(velocity);
  }

  /** Adds the rotor velocity to the registered status signals, default update freq */
  public void registerRotorVelocity() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerRotorVelocity(updateFreq);
  }

  /**
   * Adds the rotor velocity to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerRotorVelocity(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    rotorVelocity = talonFXS.getRotorVelocity();
    rotorVelocity.setUpdateFrequency(updateFreq);
    registerSignal(rotorVelocity);
  }

  /** Adds the acceleration to the registered status signals, default update freq */
  public void registerAcceleration() {
    double updateFreq = isFDBus ? 100.0 : 50.0;
    registerAcceleration(updateFreq);
  }

  /**
   * Adds the acceleration to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerAcceleration(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    acceleration = talonFXS.getAcceleration();
    acceleration.setUpdateFrequency(updateFreq);
    registerSignal(acceleration);
  }

  /** Adds the ancillary device temp to the registered status signals, default update freq */
  public void registerAncillaryDeviceTemp() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerAncillaryDeviceTemp(updateFreq);
  }

  /**
   * Adds the ancillary device temp to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerAncillaryDeviceTemp(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    ancillaryTemp = talonFXS.getAncillaryDeviceTemp();
    ancillaryTemp.setUpdateFrequency(updateFreq);
    registerSignal(ancillaryTemp);
  }

  /** Adds the device temperature to the registered status signals, default update freq */
  public void registerDeviceTemp() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDeviceTemp(updateFreq);
  }

  /**
   * Adds the device temperature to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDeviceTemp(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    deviceTemp = talonFXS.getDeviceTemp();
    deviceTemp.setUpdateFrequency(updateFreq);
    registerSignal(deviceTemp);
  }

  /** Adds the processor temp to the registered status signals, default update freq */
  public void registerProcessorTemp() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerProcessorTemp(updateFreq);
  }

  /**
   * Adds the processor temp to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerProcessorTemp(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    processorTemp = talonFXS.getProcessorTemp();
    processorTemp.setUpdateFrequency(updateFreq);
    registerSignal(processorTemp);
  }

  /** Adds the external temp to the registered status signals, default update freq */
  public void registerExternalMotorTemp() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerExternalMotorTemp(updateFreq);
  }

  /**
   * Adds the external temp to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerExternalMotorTemp(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0);
    externalMotorTemp = talonFXS.getExternalMotorTemp();
    externalMotorTemp.setUpdateFrequency(updateFreq);
    registerSignal(externalMotorTemp);
  }

  /**
   * Adds the differential average position to the registered status signals, default update freq
   */
  public void registerDifferentialAveragePosition() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialAveragePosition(updateFreq);
  }

  /**
   * Adds the differential average position to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialAveragePosition(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    diffAvgPos = talonFXS.getDifferentialAveragePosition();
    diffAvgPos.setUpdateFrequency(updateFreq);
    registerSignal(diffAvgPos);
  }

  /**
   * Adds the differential average velocity to the registered status signals, default update freq
   */
  public void registerDifferentialAverageVelocity() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialAverageVelocity(updateFreq);
  }

  /**
   * Adds the differential average velocity to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialAverageVelocity(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    diffAvgVel = talonFXS.getDifferentialAverageVelocity();
    diffAvgVel.setUpdateFrequency(updateFreq);
    registerSignal(diffAvgVel);
  }

  /**
   * Adds the differential difference position to the registered status signals, default update freq
   */
  public void registerDifferentialDifferencePosition() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialDifferencePosition(updateFreq);
  }

  /**
   * Adds the differential difference position to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialDifferencePosition(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    diffDiffPos = talonFXS.getDifferentialDifferencePosition();
    diffDiffPos.setUpdateFrequency(updateFreq);
    registerSignal(diffDiffPos);
  }

  /**
   * Adds the differential difference velocity the registered status signals, default update freq
   */
  public void registerDifferentialDifferenceVelocity() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialDifferenceVelocity(updateFreq);
  }

  /**
   * Adds the differential difference velocity to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialDifferenceVelocity(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    diffDiffVel = talonFXS.getDifferentialDifferenceVelocity();
    diffDiffVel.setUpdateFrequency(updateFreq);
    registerSignal(diffDiffVel);
  }

  /** Adds the differential output to the registered status signals, default update freq */
  public void registerDifferentialOutput() {
    registerDifferentialOutput(100.0);
  }

  /**
   * Adds the differential output to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    diffOutput = talonFXS.getDifferentialOutput();
    diffOutput.setUpdateFrequency(updateFreq);
    registerSignal(diffOutput);
  }

  /** Adds the forward limit to the registered status signals, default update freq */
  public void registerForwardLimit() {
    registerForwardLimit(100.0);
  }

  /**
   * Adds the forward limit to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerForwardLimit(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    fwdLim = talonFXS.getForwardLimit();
    fwdLim.setUpdateFrequency(updateFreq);
    registerSignal(fwdLim);
  }

  /** Adds the reverse limit to the registered status signals, default update freq */
  public void registerReverseLimit() {
    registerReverseLimit(100.0);
  }

  /**
   * Adds the reverse limit to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerReverseLimit(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    revLim = talonFXS.getReverseLimit();
    revLim.setUpdateFrequency(updateFreq);
    registerSignal(revLim);
  }

  /** Adds the stator current to the registered status signals, default update freq */
  public void registerStatorCurrent() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerStatorCurrent(updateFreq);
  }

  /**
   * Adds the stator current to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerStatorCurrent(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    statorCurrent = talonFXS.getStatorCurrent();
    statorCurrent.setUpdateFrequency(updateFreq);
    registerSignal(statorCurrent);
  }

  /** Adds the supply current to the registered status signals, default update freq */
  public void registerSupplyCurrent() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerSupplyCurrent(updateFreq);
  }

  /**
   * Adds the supply current to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerSupplyCurrent(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    supplyCurrent = talonFXS.getSupplyCurrent();
    supplyCurrent.setUpdateFrequency(updateFreq);
    registerSignal(supplyCurrent);
  }

  /** Adds the torque current to the registered status signals, default update freq */
  public void registerTorqueCurrent() {
    registerTorqueCurrent(100.0);
  }

  /**
   * Adds the torque current to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerTorqueCurrent(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    torqueCurrent = talonFXS.getTorqueCurrent();
    torqueCurrent.setUpdateFrequency(updateFreq);
    registerSignal(torqueCurrent);
  }

  /**
   * Adds the raw pulse width position to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerRawPulseWidthPosition(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0);
    rawPulseWidthPosition = talonFXS.getRawPulseWidthPosition();
    rawPulseWidthPosition.setUpdateFrequency(updateFreq);
    registerSignal(rawPulseWidthPosition);
  }

  /**
   * Adds the raw quadrature position to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerRawQuadraturePosition(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0);
    rawQuadraturePosition = talonFXS.getRawQuadraturePosition();
    rawQuadraturePosition.setUpdateFrequency(updateFreq);
    registerSignal(rawQuadraturePosition);
  }

  /**
   * Adds the raw pulse width velocity to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerRawPulseWidthVelocity(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0);
    rawPulseWidthVelocity = talonFXS.getRawPulseWidthVelocity();
    rawPulseWidthVelocity.setUpdateFrequency(updateFreq);
    registerSignal(rawPulseWidthVelocity);
  }

  /**
   * Adds the raw quadrature velocity to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerRawQuadratureVelocity(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0);
    rawQuadratureVelocity = talonFXS.getRawQuadratureVelocity();
    rawQuadratureVelocity.setUpdateFrequency(updateFreq);
    registerSignal(rawQuadratureVelocity);
  }

  /** Adds the closed loop error to the registered status signals, default update freq */
  public void registerClosedLoopError() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopError(updateFreq);
  }

  /**
   * Adds the closed loop error to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerClosedLoopError(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopError = talonFXS.getClosedLoopError();
    closedLoopError.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopError);
  }

  /** Adds the closed loop P output to the registered status signals, default update freq */
  public void registerClosedLoopProportionalOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopProportionalOutput(updateFreq);
  }

  /**
   * Adds the closed loop P output to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerClosedLoopProportionalOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopPout = talonFXS.getClosedLoopProportionalOutput();
    closedLoopPout.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopPout);
  }

  /** Adds the closed loop I output to the registered status signals, default update freq */
  public void registerClosedLoopIntegratedOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopIntegratedOutput(updateFreq);
  }

  /**
   * Adds the closed loop I output to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerClosedLoopIntegratedOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopIout = talonFXS.getClosedLoopIntegratedOutput();
    closedLoopIout.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopIout);
  }

  /** Adds the closed loop D output to the registered status signals, default update freq */
  public void registerClosedLoopDerivativeOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopDerivativeOutput(updateFreq);
  }

  /**
   * Adds the closed loop D output to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerClosedLoopDerivativeOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDout = talonFXS.getClosedLoopDerivativeOutput();
    closedLoopDout.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDout);
  }

  /** Adds the closed loop feed forward to the registered status signals, default update freq */
  public void registerClosedLoopFeedForward() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopFeedForward(updateFreq);
  }

  /**
   * Adds the closed loop feed forward to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerClosedLoopFeedForward(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopFF = talonFXS.getClosedLoopFeedForward();
    closedLoopFF.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopFF);
  }

  /** Adds the closed loop output to the registered status signals, default update freq */
  public void registerClosedLoopOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopOutput(updateFreq);
  }

  /**
   * Adds the closed loop output to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerClosedLoopOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopOut = talonFXS.getClosedLoopOutput();
    closedLoopOut.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopOut);
  }

  /**
   * Adds the closed loop reference (setpoint) to the registered status signals, default update freq
   */
  public void registerClosedLoopReference() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopReference(updateFreq);
  }

  /**
   * Adds the closed loop reference (setpoint) to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerClosedLoopReference(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopRef = talonFXS.getClosedLoopReference();
    closedLoopRef.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopRef);
  }

  /** Adds the closed loop reference slope to the registered status signals, default update freq */
  public void registerClosedLoopReferenceSlope() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopReferenceSlope(updateFreq);
  }

  /**
   * Adds the closed loop reference slope to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerClosedLoopReferenceSlope(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopRefSlope = talonFXS.getClosedLoopReferenceSlope();
    closedLoopRefSlope.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopRefSlope);
  }

  /** Adds the closed loop slot to the registered status signals, default update freq */
  public void registerClosedLoopSlot() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopSlot(updateFreq);
  }

  /**
   * Adds the closed loop slot to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerClosedLoopSlot(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopSlot = talonFXS.getClosedLoopSlot();
    closedLoopSlot.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopSlot);
  }

  /**
   * Adds the differential closed loop error to the registered status signals, default update freq
   */
  public void registerDifferentialClosedLoopError() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopError(updateFreq);
  }

  /**
   * Adds the differential closed loop error to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialClosedLoopError(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffError = talonFXS.getDifferentialClosedLoopError();
    closedLoopDiffError.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffError);
  }

  /**
   * Adds the differential closed loop P out to the registered status signals, default update freq
   */
  public void registerDifferentialClosedLoopProportionalOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopProportionalOutput(updateFreq);
  }

  /**
   * Adds the differential closed loop P out to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialClosedLoopProportionalOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffPout = talonFXS.getDifferentialClosedLoopProportionalOutput();
    closedLoopDiffPout.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffPout);
  }

  /**
   * Adds the differential closed loop I out to the registered status signals, default update freq
   */
  public void registerDifferentialClosedLoopIntegratedOutput() {
    registerDifferentialClosedLoopIntegratedOutput(100.0);
  }

  /**
   * Adds the differential closed loop I out to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialClosedLoopIntegratedOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffIout = talonFXS.getDifferentialClosedLoopIntegratedOutput();
    closedLoopDiffIout.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffIout);
  }

  /**
   * Adds the differential closed loop D out to the registered status signals, default update freq
   */
  public void registerDifferentialClosedLoopDerivativeOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopDerivativeOutput(updateFreq);
  }

  /**
   * Adds the differential closed loop D out to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialClosedLoopDerivativeOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffDout = talonFXS.getDifferentialClosedLoopDerivativeOutput();
    closedLoopDiffDout.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffDout);
  }

  /**
   * Adds the differential closed loop feed forward to the registered status signals, default update
   * freq
   */
  public void registerDifferentialClosedLoopFeedForward() {
    registerDifferentialClosedLoopFeedForward(100.0);
  }

  /**
   * Adds the differential closed loop feed forward to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialClosedLoopFeedForward(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffFF = talonFXS.getDifferentialClosedLoopFeedForward();
    closedLoopDiffFF.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffFF);
  }

  /**
   * Adds the differential closed loop output to the registered status signals, default update freq
   */
  public void registerDifferentialClosedLoopOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopOutput(updateFreq);
  }

  /**
   * Adds the differential closed loop output to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialClosedLoopOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffOut = talonFXS.getDifferentialClosedLoopOutput();
    closedLoopDiffOut.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffOut);
  }

  /**
   * Adds the differential closed loop reference (setpoint) to the registered status signals,
   * default update freq
   */
  public void registerDifferentialClosedLoopReference() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopReference(updateFreq);
  }

  /**
   * Adds the differential closed loop reference (setpoint) to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialClosedLoopReference(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffRef = talonFXS.getDifferentialClosedLoopReference();
    closedLoopDiffRef.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffRef);
  }

  /**
   * Adds the differential closed loop reference slope to the registered status signals, default
   * update freq
   */
  public void registerDifferentialClosedLoopReferenceSlope() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopReferenceSlope(updateFreq);
  }

  /**
   * Adds the differential closed loop reference slope to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialClosedLoopReferenceSlope(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffRefSlope = talonFXS.getDifferentialClosedLoopReferenceSlope();
    closedLoopDiffRefSlope.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffRefSlope);
  }

  /**
   * Adds the differential closed loop slot to the registered status signals, default update freq
   */
  public void registerDifferentialClosedLoopSlot() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopSlot(updateFreq);
  }

  /**
   * Adds the differential closed loop slot to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialClosedLoopSlot(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffSlot = talonFXS.getDifferentialClosedLoopSlot();
    closedLoopDiffSlot.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffSlot);
  }

  /** Adds the is Pro Licensed to the registered status signals, default update freq */
  public void registerIsProLicensed() {
    registerIsProLicensed(4.0);
  }

  /**
   * Adds the is Pro Licensed to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerIsProLicensed(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    isProLic = talonFXS.getIsProLicensed();
    isProLic.setUpdateFrequency(updateFreq);
    registerSignal(isProLic);
  }

  /** Adds the motion magic is running to the registered status signals, default update freq */
  public void registerMotionMagicIsRunning() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerMotionMagicIsRunning(updateFreq);
  }

  /**
   * Adds the motion magic is running to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerMotionMagicIsRunning(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    mmIsRunning = talonFXS.getMotionMagicIsRunning();
    mmIsRunning.setUpdateFrequency(updateFreq);
    registerSignal(mmIsRunning);
  }

  /** Adds the control mode to the registered status signals, default update freq */
  public void registerControlMode() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerControlMode(updateFreq);
  }

  /**
   * Adds the control mode to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerControlMode(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    controlMode = talonFXS.getControlMode();
    controlMode.setUpdateFrequency(updateFreq);
    registerSignal(controlMode);
  }

  /** Adds the differential control mode to the registered status signals, default update freq */
  public void registerDifferentialControlMode() {
    registerDifferentialControlMode(100.0);
  }

  /**
   * Adds the differential control mode is running to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerDifferentialControlMode(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    diffControlMode = talonFXS.getDifferentialControlMode();
    diffControlMode.setUpdateFrequency(updateFreq);
    registerSignal(diffControlMode);
  }

  /** Adds the 5V rail voltage to the registered status signals, default update freq */
  public void registerFiveVoltRail() {
    registerFiveVoltRail(4.0);
  }

  /**
   * Adds the 5V rail voltage is running to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerFiveVoltRail(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0);
    fiveVoltRail = talonFXS.getFiveVRailVoltage();
    fiveVoltRail.setUpdateFrequency(updateFreq);
    registerSignal(fiveVoltRail);
  }

  /** Adds the analog rail voltage to the registered status signals, default update freq */
  public void registerAnalogRail() {
    registerAnalogRail(4.0);
  }

  /**
   * Adds the analog rail voltage is running to the registered status signals
   *
   * @param updateFreq in Hz
   */
  public void registerAnalogRail(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0);
    analogVoltRail = talonFXS.getAnalogVoltage();
    analogVoltRail.setUpdateFrequency(updateFreq);
    registerSignal(analogVoltRail);
  }

  // Getters
  /**
   * @return current bridge output
   */
  public int getBridgeOutput() {
    return bridgeOutput.getValue().value;
  }

  /**
   * @return current applied duty cycle in %
   */
  public double getDutyCycle() {
    return dutyCycle.getValue();
  }

  /**
   * @return applied motor voltage in volts
   */
  public double getMotorVoltage() {
    return motorVolt.getValueAsDouble();
  }

  /**
   * @return measured supply voltage in volts
   */
  public double getSupplyVoltage() {
    return supplyVolt.getValueAsDouble();
  }

  /**
   * @return current position in rotations
   */
  public double getPosition() {
    return position.getValueAsDouble();
  }

  /**
   * @return current rotor position in rotations
   */
  public double getRotorPosition() {
    return rotorPos.getValueAsDouble();
  }

  /**
   * @return current velocity in rot/s
   */
  public double getVelocity() {
    return velocity.getValueAsDouble();
  }

  /**
   * @return rotor velocity in rot/s
   */
  public double getRotorVelocity() {
    return rotorVelocity.getValueAsDouble();
  }

  /**
   * @return current acceleration in rot/sec^2
   */
  public double getAcceleration() {
    return acceleration.getValueAsDouble();
  }

  /**
   * @return ancillary device temperature in celsius
   */
  public double getAncillaryDeviceTemp() {
    return ancillaryTemp.getValueAsDouble();
  }

  /**
   * @return current device temp in celsius
   */
  public double getDeviceTemp() {
    return deviceTemp.getValueAsDouble();
  }

  /**
   * @return current processor temp in celsius
   */
  public double getProcessorTemp() {
    return processorTemp.getValueAsDouble();
  }

  /**
   * @return external temperature in celsius
   */
  public double getExternalTemp() {
    return externalMotorTemp.getValueAsDouble();
  }

  /**
   * @return differential average position in rotations
   */
  public double getDifferentialAvgPosition() {
    return diffAvgPos.getValueAsDouble();
  }

  /**
   * @return differential average velocity in rot/s
   */
  public double getDifferentialAvgVelocity() {
    return diffAvgVel.getValueAsDouble();
  }

  /**
   * @return differential difference position in rotations
   */
  public double getDifferentialDiffPosition() {
    return diffDiffPos.getValueAsDouble();
  }

  /**
   * @return differential difference velocity in rot/s
   */
  public double getDifferentialDiffVelocity() {
    return diffDiffVel.getValueAsDouble();
  }

  /**
   * @return differential output in V or %
   */
  public double getDifferentialOutput() {
    return diffOutput.getValue();
  }

  /**
   * @return if forward limit switch is closed to ground
   */
  public boolean isFwdLimitTripped() {
    return fwdLim.getValue() == ForwardLimitValue.ClosedToGround;
  }

  /**
   * @return if reverse limit is closed to ground
   */
  public boolean isRevLimitTripped() {
    return revLim.getValue() == ReverseLimitValue.ClosedToGround;
  }

  /**
   * @return measured stator current in A
   */
  public double getStatorCurrent() {
    return statorCurrent.getValueAsDouble();
  }

  /**
   * @return supply current in A
   */
  public double getSupplyCurrent() {
    return supplyCurrent.getValueAsDouble();
  }

  /**
   * @return torque current output in A
   */
  public double getTorqueCurrent() {
    return torqueCurrent.getValueAsDouble();
  }

  /**
   * @return raw pulse width position in rotations
   */
  public double getRawPulseWidthPosition() {
    return rawPulseWidthPosition.getValueAsDouble();
  }

  /**
   * @return raw quadrature position in rotations
   */
  public double getRawQuadraturePosition() {
    return rawQuadraturePosition.getValueAsDouble();
  }

  /**
   * @return raw pulse width velocity in rot/sec
   */
  public double getRawPulseWidthVelocity() {
    return rawPulseWidthVelocity.getValueAsDouble();
  }

  /**
   * @return raw quadrature veloicty in rot/sec
   */
  public double getRawQuadratureVelocity() {
    return rawQuadratureVelocity.getValueAsDouble();
  }

  /**
   * @return closed loop error in closed loop units
   */
  public double getClosedLoopError() {
    return closedLoopError.getValue();
  }

  /**
   * @return closed loop proportional output in closed loop units
   */
  public double getClosedLoopProportionalOut() {
    return closedLoopPout.getValue();
  }

  /**
   * @return closed loop integrated output in closed loop units
   */
  public double getClosedLoopIntegratedOut() {
    return closedLoopIout.getValue();
  }

  /**
   * @return closed loop derivative output in closed loop units
   */
  public double getClosedLoopDerivativeOut() {
    return closedLoopDout.getValue();
  }

  /**
   * @return closed loop feed forward in closed loop units
   */
  public double getClosedLoopFeedForward() {
    return closedLoopFF.getValue();
  }

  /**
   * @return closed loop output in closed loop units
   */
  public double getClosedLoopOutput() {
    return closedLoopOut.getValue();
  }

  /**
   * @return closed loop reference (setpoint) in closed loop units
   */
  public double getClosedLoopReference() {
    return closedLoopRef.getValue();
  }

  /**
   * @return closed loop reference slope in closed loop units/sec
   */
  public double getClosedLoopReferenceSlope() {
    return closedLoopRefSlope.getValue();
  }

  /**
   * @return closed loop slot [0,2]
   */
  public int getClosedLoopSlot() {
    return closedLoopSlot.getValue();
  }

  /**
   * @return differential closed loop error in closed loop units
   */
  public double getDifferentialClosedLoopError() {
    return closedLoopDiffError.getValue();
  }
}
