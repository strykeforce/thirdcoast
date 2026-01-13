package org.strykeforce.controller;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.*;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.*;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.units.measure.*;
import edu.wpi.first.wpilibj.DriverStation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SF_TalonFX {
  private TalonFX talonFX;
  private JsonTalonFX config;
  private TalonFXConfiguration talonConfig;
  private TalonFXConfigurator configurator;
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
  private boolean ignoreSWLimits;
  private boolean limitFwdMotion;
  private boolean limitRevMotion;
  private boolean overrideNeutral;
  private NeutralModeValue neutralOutput;
  private int id;

  public SF_TalonFX(int id, CANBus canbus) {
    this(id, canbus, "");
  }

  //  public SF_TalonFX(int id, String canbus) {
  //    this(id, canbus, "");
  //  }

  public SF_TalonFX(int id, CANBus canbus, String configSuffix) {
    talonFX = new TalonFX(id, canbus);
    this.id = id;
    this.configFileSuffix = configSuffix;
    isFDBus = canbus.isNetworkFD();
    loadFromJSON(configSuffix);
    setupControlRequests();
  }

  //  public SF_TalonFX(int id, String canbus, String configSuffix) {
  //    talonFX = new TalonFX(id, canbus);
  //    this.id = id;
  //    this.configFileSuffix = configSuffix;
  //    isFDBus = canbus != "rio";
  //    loadFromJSON(configSuffix);
  //    setupControlRequests();
  //  }

  public boolean loadFromJSON(String suffix) {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<JsonTalonFX> jsonAdapter = moshi.adapter(JsonTalonFX.class);

    String configPath = "home/lvuser/deploy/talonFX" + id + suffix + ".json";
    Path filePath = Path.of(configPath);
    String fileParse = "";
    try {
      fileParse = Files.readString(filePath);
      //            config = JsonAdapter.fromJson(fileParse);
    } catch (IOException e) {
      config = new JsonTalonFX();
      applyJsonConfigs();
      String error =
          "Error loading json file for talonFX " + id + ": default values, " + e.toString();
      DriverStation.reportWarning(error, e.getStackTrace());
      return false;
    }
    try {
      config = jsonAdapter.fromJson(fileParse);

    } catch (IOException e) {
      config = new JsonTalonFX();
      applyJsonConfigs();
      String error =
          "Error parsing json file for talonFX " + id + ": default values, " + e.toString();
      DriverStation.reportWarning(error, e.getStackTrace());
      return false;
    }
    applyJsonConfigs();
    return true;
  }

  private void applyJsonConfigs() {
    talonConfig =
        new TalonFXConfiguration()
            .withAudio(config.getAudioConfigs())
            .withClosedLoopGeneral(config.getClosedLoopGeneralConfigs())
            .withClosedLoopRamps(config.getClosedLoopRampConfigs())
            .withCurrentLimits(config.getCurrentLimitConfigs())
            .withCustomParams(config.getCustomParamConfigs())
            .withDifferentialConstants(config.getDifferentialConstantsConfig())
            .withDifferentialSensors(config.getDifferentialSensorConfigs())
            .withFeedback(config.getFeedbackConfigs())
            .withHardwareLimitSwitch(config.getHardwareLimitSwitchConfigs())
            .withMotionMagic(config.getMotionMagicConfigs())
            .withMotorOutput(config.getMotorOutputConfigs())
            .withOpenLoopRamps(config.getOpenLoopRampConfigs())
            .withSlot0(config.getSlot0Configs())
            .withSlot1(config.getSlot1Configs())
            .withSlot2(config.getSlot2Configs())
            .withSoftwareLimitSwitch(config.getSoftwareLimitSwitchConfigs())
            .withTorqueCurrent(config.getTorqueCurrentConfigs())
            .withVoltage(config.getVoltageConfigs());

    configurator = talonFX.getConfigurator();
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
    torqueCurrentMax = config.getTorquecCurrentMax();
    useFOC = config.getActiveFOC();
    closedLoopType = config.getClosedLoopType();
    differentialType = config.getDifferentialType();
    useTimesync = config.getUseTimesync();
    ignoreHWlimits = config.getIgnoreHwLimits();
    ignoreSWLimits = config.getIgnoreSwLimits();
    limitFwdMotion = config.getLimitFwdMotion();
    limitRevMotion = config.getLimRevMotion();
    overrideNeutral = config.getOverrieNeutral();
    neutralOutput = config.getMotorOutputConfigs().NeutralMode;
  }

  private void setupControlRequests() {
    setupFollowerControlRequest();
    setupOpenLoopControlRequest();
    setupPositionControlRequest();
    setupVelocityControlRequest();
    setupMotionMagicControlRequest();
    setupDifferentialControlRequest();
  }

  public void setControlRequestUpdateFreq(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    controlRequestUpdateFreq = updateFreq;

    // open loop
    dutyCycleOut.UpdateFreqHz = updateFreq;
    voltageOut.UpdateFreqHz = updateFreq;
    torqueCurrentFOC.UpdateFreqHz = updateFreq;

    // follower
    follower.UpdateFreqHz = updateFreq;
    strictFollower.UpdateFreqHz = updateFreq;

    // position
    positionDutyCycle.UpdateFreqHz = updateFreq;
    positionVoltage.UpdateFreqHz = updateFreq;
    positionTorqueCurrentFOC.UpdateFreqHz = updateFreq;

    // velocity
    velocityDutyCycle.UpdateFreqHz = updateFreq;
    velocityVoltage.UpdateFreqHz = updateFreq;
    velocityTorqueCurrentFOC.UpdateFreqHz = updateFreq;

    // standard motion magic
    motionMagicDutyCycle.UpdateFreqHz = updateFreq;
    motionMagicVoltage.UpdateFreqHz = updateFreq;
    motionMagicTorqueCurrentFOC.UpdateFreqHz = updateFreq;

    // velocity motion magic
    motionMagicVelocityDutyCycle.UpdateFreqHz = updateFreq;
    motionMagicVelocityVoltage.UpdateFreqHz = updateFreq;
    motionMagicVelocityTorqueCurrentFOC.UpdateFreqHz = updateFreq;

    // expo motion magic
    motionMagicExpoDutyCycle.UpdateFreqHz = updateFreq;
    motionMagicExpoVoltage.UpdateFreqHz = updateFreq;
    motionMagicExpoTorqueCurrentFOC.UpdateFreqHz = updateFreq;

    // dynamic motion magic
    dynamicMotionMagicDutyCycle.UpdateFreqHz = updateFreq;
    dynamicMotionMagicVoltage.UpdateFreqHz = updateFreq;
    dynamicMotionMagicTorqueCurrentFOC.UpdateFreqHz = updateFreq;

    // dynamic motion magic expo
    dynamicMotionMagicExpoDutyCycle.UpdateFreqHz = updateFreq;
    dynamicMotionMagicExpoVoltage.UpdateFreqHz = updateFreq;
    dynamicMotionMagicExpoTorqueCurrentFOC.UpdateFreqHz = updateFreq;

    // differential
    differentialDutyCycle.UpdateFreqHz = updateFreq;
    differentialVoltage.UpdateFreqHz = updateFreq;

    // differential position
    differentialPositionDutyCycle.UpdateFreqHz = updateFreq;
    differentialPositionVoltage.UpdateFreqHz = updateFreq;

    // differential velocity
    differentialVelocityDutyCycle.UpdateFreqHz = updateFreq;
    differentialVelocityVoltage.UpdateFreqHz = updateFreq;

    // differential motion magic
    differentialMotionMagicDutyCycle.UpdateFreqHz = updateFreq;
    differentialMotionMagicVoltage.UpdateFreqHz = updateFreq;

    // differential follower
    differentialFollower.UpdateFreqHz = updateFreq;
    differentialStrictFollower.UpdateFreqHz = updateFreq;
  }

  private void setupFollowerControlRequest() {
    switch (followerType) {
      case Standard -> follower = new Follower(leaderID, opposeMain);
      case Strict -> strictFollower = new StrictFollower(leaderID);
    }
  }

  private void setupOpenLoopControlRequest() {
    switch (openLoopUnits) {
      case Percent -> {
        dutyCycleOut =
            new DutyCycleOut(0.0)
                .withEnableFOC(useFOC)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWLimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideBrakeDurNeutral(overrideNeutral);
      }
      case Voltage -> {
        voltageOut =
            new VoltageOut(0.0)
                .withEnableFOC(useFOC)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWLimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideBrakeDurNeutral(overrideNeutral);
      }
      case Torque_Current -> {
        torqueCurrentFOC =
            new TorqueCurrentFOC(0.0)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWLimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideCoastDurNeutral(overrideNeutral)
                .withMaxAbsDutyCycle(torqueCurrentMax)
                .withDeadband(torqueCurrentDeadband);
      }
    }
  }

  private void setupPositionControlRequest() {
    switch (closedLoopUnits) {
      case Percent -> {
        positionDutyCycle =
            new PositionDutyCycle(0.0)
                .withEnableFOC(useFOC)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWLimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideBrakeDurNeutral(overrideNeutral)
                .withSlot(activeSlot);
      }
      case Voltage -> {
        positionVoltage =
            new PositionVoltage(0.0)
                .withEnableFOC(useFOC)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWLimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideBrakeDurNeutral(overrideNeutral)
                .withSlot(activeSlot);
      }
      case Torque_Current -> {
        positionTorqueCurrentFOC =
            new PositionTorqueCurrentFOC(0.0)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWLimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideCoastDurNeutral(overrideNeutral)
                .withSlot(activeSlot);
      }
    }
  }

  private void setupVelocityControlRequest() {
    switch (closedLoopUnits) {
      case Percent -> {
        velocityDutyCycle =
            new VelocityDutyCycle(0.0)
                .withEnableFOC(useFOC)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWLimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideBrakeDurNeutral(overrideNeutral)
                .withSlot(activeSlot);
      }
      case Voltage -> {
        velocityVoltage =
            new VelocityVoltage(0.0)
                .withEnableFOC(useFOC)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWLimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideBrakeDurNeutral(overrideNeutral)
                .withSlot(activeSlot);
      }
      case Torque_Current -> {
        velocityTorqueCurrentFOC =
            new VelocityTorqueCurrentFOC(0.0)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
                .withIgnoreSoftwareLimits(ignoreSWLimits)
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideCoastDurNeutral(overrideNeutral)
                .withSlot(activeSlot);
      }
    }
  }

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
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
          }
          case Voltage -> {
            motionMagicVoltage =
                new MotionMagicVoltage(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
          }
          case Torque_Current -> {
            motionMagicTorqueCurrentFOC =
                new MotionMagicTorqueCurrentFOC(0.0)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideCoastDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
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
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
          }
          case Voltage -> {
            motionMagicVelocityVoltage =
                new MotionMagicVelocityVoltage(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
          }
          case Torque_Current -> {
            motionMagicVelocityTorqueCurrentFOC =
                new MotionMagicVelocityTorqueCurrentFOC(0.0)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideCoastDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
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
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
          }
          case Voltage -> {
            motionMagicExpoVoltage =
                new MotionMagicExpoVoltage(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
          }
          case Torque_Current -> {
            motionMagicExpoTorqueCurrentFOC =
                new MotionMagicExpoTorqueCurrentFOC(0.0)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideCoastDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
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
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
          }
          case Voltage -> {
            dynamicMotionMagicVoltage =
                new DynamicMotionMagicVoltage(0.0, 0.0, 0.0)
                    .withJerk(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
          }
          case Torque_Current -> {
            dynamicMotionMagicTorqueCurrentFOC =
                new DynamicMotionMagicTorqueCurrentFOC(0.0, 0.0, 0.0)
                    .withJerk(0.0)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideCoastDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
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
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
          }
          case Voltage -> {
            dynamicMotionMagicExpoVoltage =
                new DynamicMotionMagicExpoVoltage(0.0, 0.0, 0.0)
                    .withVelocity(0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
          }
          case Torque_Current -> {
            dynamicMotionMagicExpoTorqueCurrentFOC =
                new DynamicMotionMagicExpoTorqueCurrentFOC(0.0, 0.0, 0.0)
                    .withVelocity(0.0)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideCoastDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
          }
        }
      }
    }
  }

  private void setupDifferentialControlRequest() {
    switch (differentialType) {
      case Follower -> {
        switch (followerType) {
          case Standard -> {
            differentialFollower = new DifferentialFollower(leaderID, opposeMain);
          }
          case Strict -> {
            differentialStrictFollower = new DifferentialStrictFollower(leaderID);
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
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral);
          }
          case Voltage -> {
            differentialVoltage =
                new DifferentialVoltage(0.0, 0.0)
                    .withUseTimesync(useTimesync)
                    .withEnableFOC(useFOC)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral);
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
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral);
          }
          case Voltage -> {
            differentialPositionVoltage =
                new DifferentialPositionVoltage(0.0, 0.0)
                    .withUseTimesync(useTimesync)
                    .withEnableFOC(useFOC)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral);
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
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral);
          }
          case Voltage -> {
            differentialVelocityVoltage =
                new DifferentialVelocityVoltage(0.0, 0.0)
                    .withUseTimesync(useTimesync)
                    .withEnableFOC(useFOC)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral);
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
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral);
          }
          case Voltage -> {
            differentialMotionMagicVoltage =
                new DifferentialMotionMagicVoltage(0.0, 0.0)
                    .withUseTimesync(useTimesync)
                    .withEnableFOC(useFOC)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withIgnoreSoftwareLimits(ignoreSWLimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral);
          }
          case Torque_Current -> {
            DriverStation.reportError(
                "Invalid Control Type: Differential Position Torque Current FOC", false);
          }
        }
      }
    }
  }

  public void runOpenLoop(double setpoint) {
    switch (openLoopUnits) {
      case Percent -> talonFX.setControl(
          dutyCycleOut
              .withOutput(setpoint)
              .withLimitReverseMotion(limitRevMotion)
              .withLimitForwardMotion(limitFwdMotion)
              .withIgnoreHardwareLimits(ignoreHWlimits)
              .withIgnoreSoftwareLimits(ignoreSWLimits));
      case Voltage -> talonFX.setControl(
          voltageOut
              .withOutput(setpoint)
              .withLimitReverseMotion(limitRevMotion)
              .withLimitForwardMotion(limitFwdMotion)
              .withIgnoreHardwareLimits(ignoreHWlimits)
              .withIgnoreSoftwareLimits(ignoreSWLimits));
      case Torque_Current -> talonFX.setControl(
          torqueCurrentFOC
              .withOutput(setpoint)
              .withLimitReverseMotion(limitRevMotion)
              .withLimitForwardMotion(limitFwdMotion)
              .withIgnoreHardwareLimits(ignoreHWlimits)
              .withIgnoreSoftwareLimits(ignoreSWLimits));
    }
  }

  public void runClosedLoop(double setpoint) {
    switch (closedLoopType) {
      case Position -> {
        switch (closedLoopUnits) {
          case Percent -> talonFX.setControl(
              positionDutyCycle
                  .withPosition(setpoint)
                  .withSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Voltage -> talonFX.setControl(
              positionVoltage
                  .withPosition(setpoint)
                  .withSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Torque_Current -> talonFX.setControl(
              positionTorqueCurrentFOC
                  .withPosition(setpoint)
                  .withSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
        }
      }
      case Velocity -> {
        switch (closedLoopUnits) {
          case Percent -> talonFX.setControl(
              velocityDutyCycle
                  .withVelocity(setpoint)
                  .withSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Voltage -> talonFX.setControl(
              velocityVoltage
                  .withVelocity(setpoint)
                  .withSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Torque_Current -> talonFX.setControl(
              velocityTorqueCurrentFOC
                  .withVelocity(setpoint)
                  .withSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
        }
      }
      case Motion_Magic -> {
        switch (motionMagicType) {
          case Standard -> {
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(
                  motionMagicDutyCycle
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Voltage -> talonFX.setControl(
                  motionMagicVoltage
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Torque_Current -> talonFX.setControl(
                  motionMagicTorqueCurrentFOC
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
            }
          }
          case Velocity -> {
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(
                  motionMagicVelocityDutyCycle
                      .withVelocity(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Voltage -> talonFX.setControl(
                  motionMagicVelocityVoltage
                      .withVelocity(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Torque_Current -> talonFX.setControl(
                  motionMagicVelocityTorqueCurrentFOC
                      .withVelocity(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
            }
          }
          case Exponential -> {
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(
                  motionMagicExpoDutyCycle
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Voltage -> talonFX.setControl(
                  motionMagicExpoVoltage
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Torque_Current -> talonFX.setControl(
                  motionMagicExpoTorqueCurrentFOC
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
            }
          }
          case Dynamic -> {
            DriverStation.reportWarning(
                "Not supplying enough arguments for Dynamic Motion Magic", false);
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(
                  dynamicMotionMagicDutyCycle
                      .withPosition(setpoint)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Voltage -> talonFX.setControl(
                  dynamicMotionMagicVoltage
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Torque_Current -> talonFX.setControl(
                  dynamicMotionMagicTorqueCurrentFOC
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
            }
          }
          case DynamicExponential -> {
            DriverStation.reportWarning(
                "Not supplying enough arguments for Dynamic Motion Magic", false);
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(
                  dynamicMotionMagicExpoDutyCycle
                      .withPosition(setpoint)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Voltage -> talonFX.setControl(
                  dynamicMotionMagicExpoVoltage
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Torque_Current -> talonFX.setControl(
                  dynamicMotionMagicExpoTorqueCurrentFOC
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
            }
          }
        }
      }
    }
  }

  public void runClosedLoop(double setpoint, double secondarySetpoint) {
    switch (closedLoopType) {
      case Position -> {
        switch (closedLoopUnits) {
          case Percent -> talonFX.setControl(
              positionDutyCycle
                  .withPosition(setpoint)
                  .withSlot(activeSlot)
                  .withVelocity(secondarySetpoint)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Voltage -> talonFX.setControl(
              positionVoltage
                  .withPosition(setpoint)
                  .withSlot(activeSlot)
                  .withVelocity(secondarySetpoint)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Torque_Current -> talonFX.setControl(
              positionTorqueCurrentFOC
                  .withPosition(setpoint)
                  .withSlot(activeSlot)
                  .withVelocity(secondarySetpoint)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
        }
      }
      case Velocity -> {
        switch (closedLoopUnits) {
          case Percent -> talonFX.setControl(
              velocityDutyCycle
                  .withVelocity(setpoint)
                  .withSlot(activeSlot)
                  .withAcceleration(secondarySetpoint)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Voltage -> talonFX.setControl(
              velocityVoltage
                  .withVelocity(setpoint)
                  .withSlot(activeSlot)
                  .withAcceleration(secondarySetpoint)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Torque_Current -> talonFX.setControl(
              velocityTorqueCurrentFOC
                  .withVelocity(setpoint)
                  .withSlot(activeSlot)
                  .withAcceleration(secondarySetpoint)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
        }
      }
      case Motion_Magic -> {
        switch (motionMagicType) {
          case Standard -> {
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(
                  motionMagicDutyCycle
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Voltage -> talonFX.setControl(
                  motionMagicVoltage
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Torque_Current -> talonFX.setControl(
                  motionMagicTorqueCurrentFOC
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
            }
          }
          case Velocity -> {
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(
                  motionMagicVelocityDutyCycle
                      .withVelocity(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Voltage -> talonFX.setControl(
                  motionMagicVelocityVoltage
                      .withVelocity(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Torque_Current -> talonFX.setControl(
                  motionMagicVelocityTorqueCurrentFOC
                      .withVelocity(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
            }
          }
          case Exponential -> {
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(
                  motionMagicExpoDutyCycle
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Voltage -> talonFX.setControl(
                  motionMagicExpoVoltage
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Torque_Current -> talonFX.setControl(
                  motionMagicExpoTorqueCurrentFOC
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
            }
          }
          case Dynamic -> {
            DriverStation.reportWarning(
                "Not supplying enough arguments for Dynamic Motion Magic", false);
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(
                  dynamicMotionMagicDutyCycle
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Voltage -> talonFX.setControl(
                  dynamicMotionMagicVoltage
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Torque_Current -> talonFX.setControl(
                  dynamicMotionMagicTorqueCurrentFOC
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
            }
          }
          case DynamicExponential -> {
            DriverStation.reportWarning(
                "Not supplying enough arguments for Dynamic Motion Magic", false);
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(
                  dynamicMotionMagicExpoDutyCycle
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Voltage -> talonFX.setControl(
                  dynamicMotionMagicExpoVoltage
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
              case Torque_Current -> talonFX.setControl(
                  dynamicMotionMagicExpoTorqueCurrentFOC
                      .withPosition(setpoint)
                      .withSlot(activeSlot)
                      .withLimitReverseMotion(limitRevMotion)
                      .withLimitForwardMotion(limitFwdMotion)
                      .withIgnoreHardwareLimits(ignoreHWlimits)
                      .withIgnoreSoftwareLimits(ignoreSWLimits));
            }
          }
        }
      }
    }
  }

  public void runDynamicMotionMagic(
      double position, double velocity, double acceleration, double jerk) {
    switch (closedLoopUnits) {
      case Percent -> talonFX.setControl(
          dynamicMotionMagicDutyCycle
              .withPosition(position)
              .withVelocity(velocity)
              .withAcceleration(acceleration)
              .withJerk(jerk)
              .withSlot(activeSlot)
              .withLimitReverseMotion(limitRevMotion)
              .withLimitForwardMotion(limitFwdMotion)
              .withIgnoreHardwareLimits(ignoreHWlimits)
              .withIgnoreSoftwareLimits(ignoreSWLimits));
      case Voltage -> talonFX.setControl(
          dynamicMotionMagicVoltage
              .withPosition(position)
              .withVelocity(velocity)
              .withAcceleration(acceleration)
              .withJerk(jerk)
              .withSlot(activeSlot)
              .withLimitReverseMotion(limitRevMotion)
              .withLimitForwardMotion(limitFwdMotion)
              .withIgnoreHardwareLimits(ignoreHWlimits)
              .withIgnoreSoftwareLimits(ignoreSWLimits));
      case Torque_Current -> talonFX.setControl(
          dynamicMotionMagicTorqueCurrentFOC
              .withPosition(position)
              .withVelocity(velocity)
              .withAcceleration(acceleration)
              .withJerk(jerk)
              .withSlot(activeSlot)
              .withLimitReverseMotion(limitRevMotion)
              .withLimitForwardMotion(limitFwdMotion)
              .withIgnoreHardwareLimits(ignoreHWlimits)
              .withIgnoreSoftwareLimits(ignoreSWLimits));
    }
  }

  public void runDynamicMotionMagicExpo(double position, double kV, double kA, double velocity) {
    switch (closedLoopUnits) {
      case Percent -> talonFX.setControl(
          dynamicMotionMagicExpoDutyCycle
              .withPosition(position)
              .withKV(kV)
              .withKA(kA)
              .withVelocity(velocity)
              .withSlot(activeSlot)
              .withLimitReverseMotion(limitRevMotion)
              .withLimitForwardMotion(limitFwdMotion)
              .withIgnoreHardwareLimits(ignoreHWlimits)
              .withIgnoreSoftwareLimits(ignoreSWLimits));
      case Voltage -> talonFX.setControl(
          dynamicMotionMagicExpoVoltage
              .withPosition(position)
              .withKV(kV)
              .withKA(kA)
              .withVelocity(velocity)
              .withSlot(activeSlot)
              .withLimitReverseMotion(limitRevMotion)
              .withLimitForwardMotion(limitFwdMotion)
              .withIgnoreHardwareLimits(ignoreHWlimits)
              .withIgnoreSoftwareLimits(ignoreSWLimits));
      case Torque_Current -> talonFX.setControl(
          dynamicMotionMagicExpoTorqueCurrentFOC
              .withPosition(position)
              .withKV(kV)
              .withKA(kA)
              .withVelocity(velocity)
              .withSlot(activeSlot)
              .withLimitReverseMotion(limitRevMotion)
              .withLimitForwardMotion(limitFwdMotion)
              .withIgnoreHardwareLimits(ignoreHWlimits)
              .withIgnoreSoftwareLimits(ignoreSWLimits));
    }
  }

  public void runDifferential(double target, double offset) {
    switch (differentialType) {
      case Follower -> {
        switch (followerType) {
          case Standard -> talonFX.setControl(
              differentialFollower.withLeaderID(leaderID).withMotorAlignment(opposeMain));
          case Strict -> talonFX.setControl(differentialStrictFollower.withLeaderID(leaderID));
        }
      }
      case Open_Loop -> {
        switch (openLoopUnits) {
          case Percent -> talonFX.setControl(
              differentialDutyCycle
                  .withAverageOutput(target)
                  .withDifferentialPosition(offset)
                  .withDifferentialSlot(differentialSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Voltage -> talonFX.setControl(
              differentialVoltage
                  .withAverageOutput(target)
                  .withDifferentialPosition(offset)
                  .withDifferentialSlot(differentialSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Torque_Current -> DriverStation.reportError(
              "Invalid Control Type: Differential Torque Current FOC", false);
        }
      }
      case Position -> {
        switch (closedLoopUnits) {
          case Percent -> talonFX.setControl(
              differentialPositionDutyCycle
                  .withAveragePosition(target)
                  .withDifferentialPosition(offset)
                  .withDifferentialSlot(differentialSlot)
                  .withAverageSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Voltage -> talonFX.setControl(
              differentialPositionVoltage
                  .withAveragePosition(target)
                  .withDifferentialPosition(offset)
                  .withDifferentialSlot(differentialSlot)
                  .withAverageSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Torque_Current -> DriverStation.reportError(
              "Invalid Control Type: Differential Position Torque Current FOC", false);
        }
      }
      case Velocity -> {
        switch (openLoopUnits) {
          case Percent -> talonFX.setControl(
              differentialVelocityDutyCycle
                  .withAverageVelocity(target)
                  .withDifferentialPosition(offset)
                  .withDifferentialSlot(differentialSlot)
                  .withAverageSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Voltage -> talonFX.setControl(
              differentialVelocityVoltage
                  .withAverageVelocity(target)
                  .withDifferentialPosition(offset)
                  .withDifferentialSlot(differentialSlot)
                  .withAverageSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Torque_Current -> DriverStation.reportError(
              "Invalid Control Type: Differential Velocity Torque Current FOC", false);
        }
      }
      case Motion_Magic -> {
        switch (openLoopUnits) {
          case Percent -> talonFX.setControl(
              differentialMotionMagicDutyCycle
                  .withAveragePosition(target)
                  .withDifferentialPosition(offset)
                  .withDifferentialSlot(differentialSlot)
                  .withAverageSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Voltage -> talonFX.setControl(
              differentialMotionMagicVoltage
                  .withAveragePosition(target)
                  .withDifferentialPosition(offset)
                  .withDifferentialSlot(differentialSlot)
                  .withAverageSlot(activeSlot)
                  .withLimitReverseMotion(limitRevMotion)
                  .withLimitForwardMotion(limitFwdMotion)
                  .withIgnoreHardwareLimits(ignoreHWlimits)
                  .withIgnoreSoftwareLimits(ignoreSWLimits));
          case Torque_Current -> DriverStation.reportError(
              "Invalid Control Type: Differential Motion Magic Torque Current FOC", false);
        }
      }
    }
  }

  public void forceCoast() {
    talonFX.setControl(new CoastOut());
  }

  public void forceBrake() {
    talonFX.setControl(new StaticBrake());
  }

  public void setupFollower(int leaderID) {
    this.leaderID = leaderID;
    switch (followerType) {
      case Standard -> talonFX.setControl(follower.withLeaderID(leaderID));
      case Strict -> talonFX.setControl(strictFollower.withLeaderID(leaderID));
    }
  }

  public void setupDifferentialFollower(int leaderID) {
    this.leaderID = leaderID;
    switch (followerType) {
      case Standard -> talonFX.setControl(differentialFollower.withLeaderID(leaderID));
      case Strict -> talonFX.setControl(differentialStrictFollower.withLeaderID(leaderID));
    }
  }

  public void playTone(double freq) {
    talonFX.setControl(new MusicTone(freq));
  }

  // Setters
  public void setDifferentialSlot(int slot) {
    this.differentialSlot = slot;
  }

  public void setActiveSlot(int slot) {
    this.activeSlot = slot;
  }

  public void setStatorCurrentLimit(boolean enable, double limit) {
    CurrentLimitsConfigs current = talonConfig.CurrentLimits;
    current.withStatorCurrentLimit(limit).withStatorCurrentLimitEnable(enable);
    talonConfig.withCurrentLimits(current);
    configurator.apply(current);
  }

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

  public void setCurrentLimits(CurrentLimitsConfigs config) {
    talonConfig.withCurrentLimits(config);
    configurator.apply(config);
  }

  public void enableHardLimits(boolean fwdLim, boolean revLim) {
    HardwareLimitSwitchConfigs config = talonConfig.HardwareLimitSwitch;
    config.withForwardLimitEnable(fwdLim).withReverseLimitEnable(revLim);
    talonConfig.withHardwareLimitSwitch(config);
    configurator.apply(config);
  }

  public void forceFwdLimit(boolean enable) {
    limitFwdMotion = enable;
  }

  public void forceRevLimit(boolean enable) {
    limitRevMotion = enable;
  }

  public void dynamicIgnoreSwLimits(boolean ignore) {
    ignoreSWLimits = ignore;
  }

  public void dynamicIgnoreHwLimits(boolean ignore) {
    ignoreHWlimits = ignore;
  }

  public void setMotionMagicConfigs(MotionMagicConfigs config) {
    talonConfig.withMotionMagic(config);
    configurator.apply(config);
  }

  public void setPeakOutputPercent(double peakFwd, double peakRev) {
    MotorOutputConfigs motorOut = talonConfig.MotorOutput;
    motorOut.withPeakForwardDutyCycle(peakFwd).withPeakReverseDutyCycle(peakRev);
    talonConfig.withMotorOutput(motorOut);
    configurator.apply(motorOut);
  }

  public void setPeakOutputVolt(double peakFwd, double peakRev) {
    VoltageConfigs config = talonConfig.Voltage;
    config.withPeakForwardVoltage(peakFwd).withPeakReverseVoltage(peakRev);
    talonConfig.withVoltage(config);
    configurator.apply(config);
  }

  public void setSoftLimits(SoftwareLimitSwitchConfigs config) {
    talonConfig.withSoftwareLimitSwitch(config);
    configurator.apply(config);
  }

  public void enableSoftLimits(boolean enableFwd, boolean enableRev) {
    SoftwareLimitSwitchConfigs config = talonConfig.SoftwareLimitSwitch;
    config.withForwardSoftLimitEnable(enableFwd).withReverseSoftLimitEnable(enableRev);
    talonConfig.withSoftwareLimitSwitch(config);
    configurator.apply(config);
  }

  public void setPosition(double position) {
    talonFX.setPosition(position);
  }

  // Getters
  public TalonFX getTalonFX() {
    return talonFX;
  }

  // Watchers
  public void refreshRegisteredSignals() {
    BaseStatusSignal.refreshAll(registeredStatusSignals);
  }

  public BaseStatusSignal[] getRegisteredSignals() {
    return registeredStatusSignals;
  }

  private void registerSignal(BaseStatusSignal... signals) {
    BaseStatusSignal[] newSignals =
        new BaseStatusSignal[registeredStatusSignals.length + signals.length];
    System.arraycopy(registeredStatusSignals, 0, newSignals, 0, registeredStatusSignals.length);
    System.arraycopy(signals, 0, newSignals, registeredStatusSignals.length, signals.length);
    registeredStatusSignals = newSignals;
  }

  private boolean isRegistered(BaseStatusSignal signal) {
    for (int i = 0; i < registeredStatusSignals.length; i++) {
      if (registeredStatusSignals[i].getName() == signal.getName()) return true;
    }
    return false;
  }

  public void registerBridgeOutput() {
    registerBridgeOutput(100.0);
  }

  public void registerBridgeOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    bridgeOutput.setUpdateFrequency(updateFreq);
    bridgeOutput = talonFX.getBridgeOutput();
    registerSignal(bridgeOutput);
  }

  public void registerDutyCycle() {
    registerDutyCycle(100.0);
  }

  public void registerDutyCycle(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    dutyCycle.setUpdateFrequency(updateFreq);
    dutyCycle = talonFX.getDutyCycle();
    registerSignal(dutyCycle);
  }

  public void registerMotorVoltage() {
    registerMotorVoltage(100.0);
  }

  public void registerMotorVoltage(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    motorVolt.setUpdateFrequency(updateFreq);
    motorVolt = talonFX.getMotorVoltage();
    registerSignal(motorVolt);
  }

  public void registerSupplyVoltage() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerSupplyVoltage(updateFreq);
  }

  public void registerSupplyVoltage(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    supplyVolt.setUpdateFrequency(updateFreq);
    supplyVolt = talonFX.getSupplyVoltage();
    registerSignal(supplyVolt);
  }

  public void registerPosition() {
    double updateFreq = isFDBus ? 100.0 : 50.0;
    registerPosition(updateFreq);
  }

  public void registerPosition(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    position.setUpdateFrequency(updateFreq);
    position = talonFX.getPosition();
    registerSignal(position);
  }

  public void registerRotorPosition() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerRotorPosition(updateFreq);
  }

  public void registerRotorPosition(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    rotorPos.setUpdateFrequency(updateFreq);
    rotorPos = talonFX.getRotorPosition();
    registerSignal(rotorPos);
  }

  public void registerVelocity() {
    double updateFreq = isFDBus ? 100.0 : 50.0;
    registerVelocity(updateFreq);
  }

  public void registerVelocity(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    velocity.setUpdateFrequency(updateFreq);
    velocity = talonFX.getVelocity();
    registerSignal(velocity);
  }

  public void registerRotorVelocity() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerRotorVelocity(updateFreq);
  }

  public void registerRotorVelocity(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    rotorVelocity.setUpdateFrequency(updateFreq);
    rotorVelocity = talonFX.getRotorVelocity();
    registerSignal(rotorVelocity);
  }

  public void registerAcceleration() {
    double updateFreq = isFDBus ? 100.0 : 50.0;
    registerAcceleration(updateFreq);
  }

  public void registerAcceleration(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    acceleration.setUpdateFrequency(updateFreq);
    acceleration = talonFX.getAcceleration();
    registerSignal(acceleration);
  }

  public void registerAncillaryDeviceTemp() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerAncillaryDeviceTemp(updateFreq);
  }

  public void registerAncillaryDeviceTemp(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    ancillaryTemp.setUpdateFrequency(updateFreq);
    ancillaryTemp = talonFX.getAncillaryDeviceTemp();
    registerSignal(ancillaryTemp);
  }

  public void registerDeviceTemp() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDeviceTemp(updateFreq);
  }

  public void registerDeviceTemp(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    deviceTemp.setUpdateFrequency(updateFreq);
    deviceTemp = talonFX.getDeviceTemp();
    registerSignal(deviceTemp);
  }

  public void registerProcessorTemp() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerProcessorTemp(updateFreq);
  }

  public void registerProcessorTemp(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    processorTemp.setUpdateFrequency(updateFreq);
    processorTemp = talonFX.getProcessorTemp();
    registerSignal(processorTemp);
  }

  public void registerDifferentialAveragePosition() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialAveragePosition(updateFreq);
  }

  public void registerDifferentialAveragePosition(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    diffAvgPos.setUpdateFrequency(updateFreq);
    diffAvgPos = talonFX.getDifferentialAveragePosition();
    registerSignal(diffAvgPos);
  }

  public void registerDifferentialAverageVelocity() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialAverageVelocity(updateFreq);
  }

  public void registerDifferentialAverageVelocity(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    diffAvgVel.setUpdateFrequency(updateFreq);
    diffAvgVel = talonFX.getDifferentialAverageVelocity();
    registerSignal(diffAvgVel);
  }

  public void registerDifferentialDifferencePosition() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialDifferencePosition(updateFreq);
  }

  public void registerDifferentialDifferencePosition(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    diffDiffPos.setUpdateFrequency(updateFreq);
    diffDiffPos = talonFX.getDifferentialDifferencePosition();
    registerSignal(diffDiffPos);
  }

  public void registerDifferentialDifferenceVelocity() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialDifferenceVelocity(updateFreq);
  }

  public void registerDifferentialDifferenceVelocity(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    diffDiffVel.setUpdateFrequency(updateFreq);
    diffDiffVel = talonFX.getDifferentialDifferenceVelocity();
    registerSignal(diffDiffVel);
  }

  public void registerDifferentialOutput() {
    registerDifferentialOutput(100.0);
  }

  public void registerDifferentialOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    diffOutput.setUpdateFrequency(updateFreq);
    diffOutput = talonFX.getDifferentialOutput();
    registerSignal(diffOutput);
  }

  public void registerForwardLimit() {
    registerForwardLimit(100.0);
  }

  public void registerForwardLimit(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    fwdLim.setUpdateFrequency(updateFreq);
    fwdLim = talonFX.getForwardLimit();
    registerSignal(fwdLim);
  }

  public void registerReverseLimit() {
    registerReverseLimit(100.0);
  }

  public void registerReverseLimit(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    revLim.setUpdateFrequency(updateFreq);
    revLim = talonFX.getReverseLimit();
    registerSignal(revLim);
  }

  public void registerStatorCurrent() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerStatorCurrent(updateFreq);
  }

  public void registerStatorCurrent(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    statorCurrent.setUpdateFrequency(updateFreq);
    statorCurrent = talonFX.getStatorCurrent();
    registerSignal(statorCurrent);
  }

  public void registerSupplyCurrent() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerSupplyCurrent(updateFreq);
  }

  public void registerSupplyCurrent(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    supplyCurrent = talonFX.getSupplyCurrent();
    supplyCurrent.setUpdateFrequency(updateFreq);
    registerSignal(supplyCurrent);
  }

  public void registerTorqueCurrent() {
    registerTorqueCurrent(100.0);
  }

  public void registerTorqueCurrent(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    torqueCurrent = talonFX.getTorqueCurrent();
    torqueCurrent.setUpdateFrequency(updateFreq);
    registerSignal(torqueCurrent);
  }

  public void registerClosedLoopError() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopError(updateFreq);
  }

  public void registerClosedLoopError(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopError = talonFX.getClosedLoopError();
    closedLoopError.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopError);
  }

  public void registerClosedLoopProportionalOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopProportionalOutput(updateFreq);
  }

  public void registerClosedLoopProportionalOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopPout = talonFX.getClosedLoopProportionalOutput();
    closedLoopPout.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopPout);
  }

  public void registerClosedLoopIntegratedOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopIntegratedOutput(updateFreq);
  }

  public void registerClosedLoopIntegratedOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopIout = talonFX.getClosedLoopIntegratedOutput();
    closedLoopIout.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopIout);
  }

  public void registerClosedLoopDerivativeOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopDerivativeOutput(updateFreq);
  }

  public void registerClosedLoopDerivativeOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDout = talonFX.getClosedLoopDerivativeOutput();
    closedLoopDout.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDout);
  }

  public void registerClosedLoopFeedForward() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopFeedForward(updateFreq);
  }

  public void registerClosedLoopFeedForward(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopFF = talonFX.getClosedLoopFeedForward();
    closedLoopFF.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopFF);
  }

  public void registerClosedLoopOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopOutput(updateFreq);
  }

  public void registerClosedLoopOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopOut = talonFX.getClosedLoopOutput();
    closedLoopOut.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopOut);
  }

  public void registerClosedLoopReference() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopReference(updateFreq);
  }

  public void registerClosedLoopReference(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopRef = talonFX.getClosedLoopReference();
    closedLoopRef.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopRef);
  }

  public void registerClosedLoopReferenceSlope() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopReferenceSlope(updateFreq);
  }

  public void registerClosedLoopReferenceSlope(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopRefSlope = talonFX.getClosedLoopReferenceSlope();
    closedLoopRefSlope.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopRefSlope);
  }

  public void registerClosedLoopSlot() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerClosedLoopSlot(updateFreq);
  }

  public void registerClosedLoopSlot(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopSlot = talonFX.getClosedLoopSlot();
    closedLoopSlot.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopSlot);
  }

  public void registerDifferentialClosedLoopError() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopError(updateFreq);
  }

  public void registerDifferentialClosedLoopError(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffError = talonFX.getDifferentialClosedLoopError();
    closedLoopDiffError.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffError);
  }

  public void registerDifferentialClosedLoopProportionalOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopProportionalOutput(updateFreq);
  }

  public void registerDifferentialClosedLoopProportionalOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffPout = talonFX.getDifferentialClosedLoopProportionalOutput();
    closedLoopDiffPout.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffPout);
  }

  public void registerDifferentialClosedLoopIntegratedOutput() {
    registerDifferentialClosedLoopIntegratedOutput(100.0);
  }

  public void registerDifferentialClosedLoopIntegratedOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffIout = talonFX.getDifferentialClosedLoopIntegratedOutput();
    closedLoopDiffIout.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffIout);
  }

  public void registerDifferentialClosedLoopDerivativeOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopDerivativeOutput(updateFreq);
  }

  public void registerDifferentialClosedLoopDerivativeOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffDout = talonFX.getDifferentialClosedLoopDerivativeOutput();
    closedLoopDiffDout.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffDout);
  }

  public void registerDifferentialClosedLoopFeedForward() {
    registerDifferentialClosedLoopFeedForward(100.0);
  }

  public void registerDifferentialClosedLoopFeedForward(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffFF = talonFX.getDifferentialClosedLoopFeedForward();
    closedLoopDiffFF.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffFF);
  }

  public void registerDifferentialClosedLoopOutput() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopOutput(updateFreq);
  }

  public void registerDifferentialClosedLoopOutput(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffOut = talonFX.getDifferentialClosedLoopOutput();
    closedLoopDiffOut.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffOut);
  }

  public void registerDifferentialClosedLoopReference() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopReference(updateFreq);
  }

  public void registerDifferentialClosedLoopReference(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffRef = talonFX.getDifferentialClosedLoopReference();
    closedLoopDiffRef.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffRef);
  }

  public void registerDifferentialClosedLoopReferenceSlope() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopReferenceSlope(updateFreq);
  }

  public void registerDifferentialClosedLoopReferenceSlope(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffRefSlope = talonFX.getDifferentialClosedLoopReferenceSlope();
    closedLoopDiffRefSlope.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffRefSlope);
  }

  public void registerDifferentialClosedLoopSlot() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerDifferentialClosedLoopSlot(updateFreq);
  }

  public void registerDifferentialClosedLoopSlot(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    closedLoopDiffSlot = talonFX.getDifferentialClosedLoopSlot();
    closedLoopDiffSlot.setUpdateFrequency(updateFreq);
    registerSignal(closedLoopDiffSlot);
  }

  public void registerIsProLicensed() {
    registerIsProLicensed(4.0);
  }

  public void registerIsProLicensed(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    isProLic = talonFX.getIsProLicensed();
    isProLic.setUpdateFrequency(updateFreq);
    registerSignal(isProLic);
  }

  public void registerMotionMagicIsRunning() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerMotionMagicIsRunning(updateFreq);
  }

  public void registerMotionMagicIsRunning(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    mmIsRunning = talonFX.getMotionMagicIsRunning();
    registerSignal(mmIsRunning);
  }

  public void registerControlMode() {
    double updateFreq = isFDBus ? 100.0 : 4.0;
    registerControlMode(updateFreq);
  }

  public void registerControlMode(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    controlMode = talonFX.getControlMode();
    controlMode.setUpdateFrequency(updateFreq);
    registerSignal(controlMode);
  }

  public void registerDifferentialControlMode() {
    registerDifferentialControlMode(100.0);
  }

  public void registerDifferentialControlMode(double updateFreq) {
    updateFreq = MathUtil.clamp(updateFreq, 0.0, 1000.0); // 4Hz = min updating
    diffControlMode = talonFX.getDifferentialControlMode();
    diffControlMode.setUpdateFrequency(updateFreq);
    registerSignal(diffControlMode);
  }

  // Getters
  public int getBridgeOutput() {
    return bridgeOutput.getValue().value;
  }

  public double getDutyCycle() {
    return dutyCycle.getValue();
  }

  public double getMotorVoltage() {
    return motorVolt.getValueAsDouble();
  }

  public double getSupplyVoltage() {
    return supplyVolt.getValueAsDouble();
  }

  public double getPosition() {
    return position.getValueAsDouble();
  }

  public double getRotorPosition() {
    return rotorPos.getValueAsDouble();
  }

  public double getVelocity() {
    return velocity.getValueAsDouble();
  }

  public double getRotorVelocity() {
    return rotorVelocity.getValueAsDouble();
  }

  public double getAcceleration() {
    return acceleration.getValueAsDouble();
  }

  public double getAncillaryDeviceTemp() {
    return ancillaryTemp.getValueAsDouble();
  }

  public double getDeviceTemp() {
    return deviceTemp.getValueAsDouble();
  }

  public double getProcessorTemp() {
    return processorTemp.getValueAsDouble();
  }

  public double getDifferentialAvgPosition() {
    return diffAvgPos.getValueAsDouble();
  }

  public double getDifferentialAvgVelocity() {
    return diffAvgVel.getValueAsDouble();
  }

  public double getDifferentialDiffPosition() {
    return diffDiffPos.getValueAsDouble();
  }

  public double getDifferentialDiffVelocity() {
    return diffDiffVel.getValueAsDouble();
  }

  public double getDifferentialOutput() {
    return diffOutput.getValue();
  }

  public boolean isFwdLimitTripped() {
    return fwdLim.getValue() == ForwardLimitValue.ClosedToGround;
  }

  public boolean isRevLimitTripped() {
    return revLim.getValue() == ReverseLimitValue.ClosedToGround;
  }

  public double getStatorCurrent() {
    return statorCurrent.getValueAsDouble();
  }

  public double getSupplyCurrent() {
    return supplyCurrent.getValueAsDouble();
  }

  public double getTorqueCurrent() {
    return torqueCurrent.getValueAsDouble();
  }

  public double getClosedLoopError() {
    return closedLoopError.getValue();
  }

  public double getClosedLoopProportionalOut() {
    return closedLoopPout.getValue();
  }

  public double getClosedLoopIntegratedOut() {
    return closedLoopIout.getValue();
  }

  public double getClosedLoopDerivativeOut() {
    return closedLoopDout.getValue();
  }

  public double getClosedLoopFeedForward() {
    return closedLoopFF.getValue();
  }

  public double getClosedLoopOutput() {
    return closedLoopOut.getValue();
  }

  public double getClosedLoopReference() {
    return closedLoopRef.getValue();
  }

  public double getClosedLoopReferenceSlope() {
    return closedLoopRefSlope.getValue();
  }

  public int getClosedLoopSlot() {
    return closedLoopSlot.getValue();
  }

  public double getDifferentialClosedLoopError() {
    return closedLoopDiffError.getValue();
  }
}
