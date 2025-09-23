package org.strykeforce.controller;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.*;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.BridgeOutputValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.wpilibj.DriverStation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class SF_TalonFX {
  private TalonFX talonFX;
  private jsonTalonFX config;
  private TalonFXConfiguration talonConfig;
  private TalonFXConfigurator configurator;
  private BaseStatusSignal[] watchedStatusSignals = new BaseStatusSignal[0];

  //Status Signals
  StatusSignal<AngularAcceleration> acceleration;
  StatusSignal<BridgeOutputValue> bridgeOutput;
  StatusSignal<Temperature> ancillaryTemp;
  StatusSignal<Temperature> deviceTemp;
  StatusSignal<Angle> position;

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
  private MotionMagicTorqueCurrentFOC motionMagicTorqueCurrentFOC = new MotionMagicTorqueCurrentFOC(0.0);

  // Velocity Motion Magic
  private MotionMagicVelocityDutyCycle motionMagicVelocityDutyCycle = new MotionMagicVelocityDutyCycle(0.0);
  private MotionMagicVelocityVoltage motionMagicVelocityVoltage = new MotionMagicVelocityVoltage(0.0);
  private MotionMagicVelocityTorqueCurrentFOC motionMagicVelocityTorqueCurrentFOC = new MotionMagicVelocityTorqueCurrentFOC(0.0);

  // Expo Motion Magic
  private MotionMagicExpoDutyCycle motionMagicExpoDutyCycle = new MotionMagicExpoDutyCycle(0.0);
  private MotionMagicExpoVoltage motionMagicExpoVoltage = new MotionMagicExpoVoltage(0.0);
  private MotionMagicExpoTorqueCurrentFOC motionMagicExpoTorqueCurrentFOC = new MotionMagicExpoTorqueCurrentFOC(0.0);

  // Dynamic Motion Magic
  private DynamicMotionMagicDutyCycle dynamicMotionMagicDutyCycle = new DynamicMotionMagicDutyCycle(0.0, 0.0, 0.0, 0.0);
  private DynamicMotionMagicVoltage dynamicMotionMagicVoltage = new DynamicMotionMagicVoltage(0.0, 0.0, 0.0, 0.0);
  private DynamicMotionMagicTorqueCurrentFOC dynamicMotionMagicTorqueCurrentFOC = new DynamicMotionMagicTorqueCurrentFOC(0.0, 0.0, 0.0, 0.0);

  // Differential
  private DifferentialDutyCycle differentialDutyCycle = new DifferentialDutyCycle(0.0, 0.0);
  private DifferentialVoltage differentialVoltage = new DifferentialVoltage(0.0, 0.0);

  // Differential Position
  private DifferentialPositionDutyCycle differentialPositionDutyCycle = new DifferentialPositionDutyCycle(0.0, 0.0);
  private DifferentialPositionVoltage differentialPositionVoltage = new DifferentialPositionVoltage(0.0, 0.0);

  // Differential Velocity
  private DifferentialVelocityDutyCycle differentialVelocityDutyCycle = new DifferentialVelocityDutyCycle(0.0, 0.0);
  private DifferentialVelocityVoltage differentialVelocityVoltage = new DifferentialVelocityVoltage(0.0, 0.0);

  // Differential Motion Magic
  private DifferentialMotionMagicDutyCycle differentialMotionMagicDutyCycle = new DifferentialMotionMagicDutyCycle(0.0, 0.0);
  private DifferentialMotionMagicVoltage differentialMotionMagicVoltage = new DifferentialMotionMagicVoltage(0.0, 0.0);

  // Differential Follower
  private DifferentialFollower differentialFollower = new DifferentialFollower(0, false);
  private DifferentialStrictFollower differentialStrictFollower = new DifferentialStrictFollower(0);

  private CTRE_Units openLoopUnits;
  private CTRE_Units closedLoopUnits;
  private int activeSlot;
  private int differentialSlot;
  private MotionMagicType motionMagicType;
  private boolean opposeMain;
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
  private boolean limitFwdMotion;
  private boolean limitRevMotion;
  private boolean overrideNeutral;
  private NeutralModeValue neutralOutput;
  private int id;

  public SF_TalonFX(int id, CANBus canbus) {
    this(id, canbus, "");
  }

  public SF_TalonFX(int id, String canbus) {
    this(id, canbus, "");
  }

  public SF_TalonFX(int id, CANBus canbus, String configSuffix) {
    talonFX = new TalonFX(id, canbus);
    this.id = id;
  }

  public SF_TalonFX(int id, String canbus, String configSuffix) {
    talonFX = new TalonFX(id, canbus);
    this.id = id;
  }

  public boolean loadFromJSON(String suffix) {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<jsonTalonFX> jsonAdapter = moshi.adapter(jsonTalonFX.class);

    String configPath = "home/lvuser/deploy/talonFX" + id + suffix + ".json";
    Path filePath = Path.of(configPath);
    String fileParse = "";
    try {
      fileParse = Files.readString(filePath);
      //            config = JsonAdapter.fromJson(fileParse);
    } catch (IOException e) {
      config = new jsonTalonFX();
      String error =
          "Error loading json file for talonFX " + id + ": default values, " + e.toString();
      DriverStation.reportWarning(error, e.getStackTrace());
      return false;
    }
    try {
      config = jsonAdapter.fromJson(fileParse);

    } catch (IOException e) {
      config = new jsonTalonFX();
      String error =
          "Error parsing json file for talonFX " + id + ": default values, " + e.toString();
      DriverStation.reportWarning(error, e.getStackTrace());
      return false;
    }
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
    opposeMain = config.getOpposeMain();
    leaderID = config.getLeaderID();
    //    torqueCurrentDeadband;
    torqueCurrentMax = config.getTorquecCurrentMax();
    useFOC = config.getActiveFOC();
    closedLoopType = config.getClosedLoopType();
    differentialType = config.getDifferentialType();
    useTimesync = config.getUseTimesync();
    ignoreHWlimits = config.getIgnoreHwLimits();
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
                .withLimitForwardMotion(limitFwdMotion)
                .withLimitReverseMotion(limitRevMotion)
                .withOverrideBrakeDurNeutral(overrideNeutral);
      }
      case Torque_Current -> {
        torqueCurrentFOC =
            new TorqueCurrentFOC(0.0)
                .withUseTimesync(useTimesync)
                .withIgnoreHardwareLimits(ignoreHWlimits)
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
                new DynamicMotionMagicDutyCycle(0.0, 0.0, 0.0, 0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
          }
          case Voltage -> {
            dynamicMotionMagicVoltage =
                new DynamicMotionMagicVoltage(0.0, 0.0, 0.0, 0.0)
                    .withEnableFOC(useFOC)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
                    .withLimitForwardMotion(limitFwdMotion)
                    .withLimitReverseMotion(limitRevMotion)
                    .withOverrideBrakeDurNeutral(overrideNeutral)
                    .withSlot(activeSlot);
          }
          case Torque_Current -> {
            dynamicMotionMagicTorqueCurrentFOC =
                new DynamicMotionMagicTorqueCurrentFOC(0.0, 0.0, 0.0, 0.0)
                    .withUseTimesync(useTimesync)
                    .withIgnoreHardwareLimits(ignoreHWlimits)
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
      case Percent -> talonFX.setControl(dutyCycleOut.withOutput(setpoint));
      case Voltage -> talonFX.setControl(voltageOut.withOutput(setpoint));
      case Torque_Current -> talonFX.setControl(torqueCurrentFOC.withOutput(setpoint));
    }
  }

  public void runClosedLoop(double setpoint) {
    switch (closedLoopType) {
      case Position -> {
        switch (closedLoopUnits) {
          case Percent -> talonFX.setControl(positionDutyCycle.withPosition(setpoint).withSlot(activeSlot));
          case Voltage -> talonFX.setControl(positionVoltage.withPosition(setpoint).withSlot(activeSlot));
          case Torque_Current -> talonFX.setControl(
              positionTorqueCurrentFOC.withPosition(setpoint).withSlot(activeSlot));
        }
      }
      case Velocity -> {
        switch (closedLoopUnits) {
          case Percent -> talonFX.setControl(velocityDutyCycle.withVelocity(setpoint).withSlot(activeSlot));
          case Voltage -> talonFX.setControl(velocityVoltage.withVelocity(setpoint).withSlot(activeSlot));
          case Torque_Current -> talonFX.setControl(
              velocityTorqueCurrentFOC.withVelocity(setpoint).withSlot(activeSlot));
        }
      }
      case Motion_Magic -> {
        switch (motionMagicType) {
          case Standard -> {
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(motionMagicDutyCycle.withPosition(setpoint).withSlot(activeSlot));
              case Voltage -> talonFX.setControl(motionMagicVoltage.withPosition(setpoint).withSlot(activeSlot));
              case Torque_Current -> talonFX.setControl(
                  motionMagicTorqueCurrentFOC.withPosition(setpoint).withSlot(activeSlot));
            }
          }
          case Velocity -> {
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(
                  motionMagicVelocityDutyCycle.withVelocity(setpoint).withSlot(activeSlot));
              case Voltage -> talonFX.setControl(motionMagicVelocityVoltage.withVelocity(setpoint).withSlot(activeSlot));
              case Torque_Current -> talonFX.setControl(
                  motionMagicVelocityTorqueCurrentFOC.withVelocity(setpoint).withSlot(activeSlot));
            }
          }
          case Exponential -> {
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(motionMagicExpoDutyCycle.withPosition(setpoint).withSlot(activeSlot));
              case Voltage -> talonFX.setControl(motionMagicExpoVoltage.withPosition(setpoint).withSlot(activeSlot));
              case Torque_Current -> talonFX.setControl(
                  motionMagicExpoTorqueCurrentFOC.withPosition(setpoint).withSlot(activeSlot));
            }
          }
          case Dynamic -> {
            DriverStation.reportWarning(
                "Not supplying enough arguments for Dynamic Motion Magic", false);
            switch (closedLoopUnits) {
              case Percent -> talonFX.setControl(
                  dynamicMotionMagicDutyCycle.withPosition(setpoint));
              case Voltage -> talonFX.setControl(dynamicMotionMagicVoltage.withPosition(setpoint).withSlot(activeSlot));
              case Torque_Current -> talonFX.setControl(
                  dynamicMotionMagicTorqueCurrentFOC.withPosition(setpoint).withSlot(activeSlot));
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
              .withJerk(jerk).withSlot(activeSlot));
      case Voltage -> talonFX.setControl(
          dynamicMotionMagicVoltage
              .withPosition(position)
              .withVelocity(velocity)
              .withAcceleration(acceleration)
              .withJerk(jerk).withSlot(activeSlot));
      case Torque_Current -> talonFX.setControl(
          dynamicMotionMagicTorqueCurrentFOC
              .withPosition(position)
              .withVelocity(velocity)
              .withAcceleration(acceleration)
              .withJerk(jerk).withSlot(activeSlot));
    }
  }

  public void runDifferential(double target, double offset) {
    switch (differentialType) {
      case Follower -> {
        switch (followerType) {
          case Standard -> talonFX.setControl(differentialFollower.withMasterID(leaderID).withOpposeMasterDirection(opposeMain));
          case Strict -> talonFX.setControl(differentialStrictFollower.withMasterID(leaderID));
        }
      }
      case Open_Loop -> {
        switch (openLoopUnits) {
          case Percent -> talonFX.setControl(differentialDutyCycle.withTargetOutput(target).withDifferentialPosition(offset).withDifferentialSlot(differentialSlot));
          case Voltage -> talonFX.setControl(differentialVoltage.withTargetOutput(target).withDifferentialPosition(offset).withDifferentialSlot(differentialSlot));
          case Torque_Current -> DriverStation.reportError(
                  "Invalid Control Type: Differential Torque Current FOC", false);
        }
      }
      case Position -> {
        switch (closedLoopUnits) {
          case Percent -> talonFX.setControl(differentialPositionDutyCycle.withTargetPosition(target).withDifferentialPosition(offset).withDifferentialSlot(differentialSlot).withTargetSlot(activeSlot));
          case Voltage -> talonFX.setControl(differentialPositionVoltage.withTargetPosition(target).withDifferentialPosition(offset).withDifferentialSlot(differentialSlot).withTargetSlot(activeSlot));
          case Torque_Current -> DriverStation.reportError(
                  "Invalid Control Type: Differential Position Torque Current FOC", false);
        }
      }
      case Velocity -> {
        switch (openLoopUnits) {
          case Percent -> talonFX.setControl(differentialVelocityDutyCycle.withTargetVelocity(target).withDifferentialPosition(offset).withDifferentialSlot(differentialSlot).withTargetSlot(activeSlot));
          case Voltage -> talonFX.setControl(differentialVelocityVoltage.withTargetVelocity(target).withDifferentialPosition(offset).withDifferentialSlot(differentialSlot).withTargetSlot(activeSlot));
          case Torque_Current -> DriverStation.reportError(
                  "Invalid Control Type: Differential Velocity Torque Current FOC", false);
        }
      }
      case Motion_Magic -> {
        switch (openLoopUnits) {
          case Percent -> talonFX.setControl(differentialMotionMagicDutyCycle.withTargetPosition(target).withDifferentialPosition(offset).withDifferentialSlot(differentialSlot).withTargetSlot(activeSlot));
          case Voltage -> talonFX.setControl(differentialMotionMagicVoltage.withTargetPosition(target).withDifferentialPosition(offset).withDifferentialSlot(differentialSlot).withTargetSlot(activeSlot));
          case Torque_Current -> DriverStation.reportError(
                  "Invalid Control Type: Differential Motion Magic Torque Current FOC", false);
        }
      }
    }
  }

  public void forceCoast() {
    talonFX.setControl(new CoastOut());
  }

  public void forceBrake(){
    talonFX.setControl(new StaticBrake());
  }

  public void setupFollower(int leaderID){
    this.leaderID = leaderID;
    switch (followerType) {
      case Standard -> talonFX.setControl(follower.withMasterID(leaderID));
      case Strict -> talonFX.setControl(strictFollower.withMasterID(leaderID));
    }
  }

  public void setupDifferentialFollower(int leaderID) {
    this.leaderID = leaderID;
    switch (followerType) {
      case Standard -> talonFX.setControl(differentialFollower.withMasterID(leaderID));
      case Strict -> talonFX.setControl(differentialStrictFollower.withMasterID(leaderID));
    }
  }

  public void playTone(double freq) {
    talonFX.setControl(new MusicTone(freq));
  }

  //Setters
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

  public void setSupplyCurrentLimit(boolean enable, double limit, double lowerLimit, double lowerTime) {
    CurrentLimitsConfigs current = talonConfig.CurrentLimits;
    current.withSupplyCurrentLimit(limit).withSupplyCurrentLimitEnable(enable).withSupplyCurrentLowerLimit(lowerLimit).withSupplyCurrentLowerTime(lowerTime);
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

  //Getters
  public TalonFX getTalonFX() {
    return talonFX;
  }

  //Watchers
  public void refreshWatchedSignals(){
    BaseStatusSignal.refreshAll(watchedStatusSignals);
  }

  public BaseStatusSignal[] getRegisteredSignals() {
    return watchedStatusSignals;
  }

  private void registerSignal(BaseStatusSignal... signals) {
    BaseStatusSignal[] newSignals = new BaseStatusSignal[watchedStatusSignals.length + signals.length];
    System.arraycopy(watchedStatusSignals, 0, newSignals, 0, watchedStatusSignals.length);
    System.arraycopy(signals, 0, newSignals, watchedStatusSignals.length, signals.length);
    watchedStatusSignals = newSignals;
  }

  public void watchAcceleration(){
    acceleration = talonFX.getAcceleration();
    registerSignal(acceleration);
  }

  public void watchBridgeOutput() {
    bridgeOutput = talonFX.getBridgeOutput();
    registerSignal(bridgeOutput);
  }

  public void watchAncillaryDeviceTemp(){
    ancillaryTemp = talonFX.getAncillaryDeviceTemp();
    registerSignal(ancillaryTemp);
  }

  public void watchDeviceTemp() {
    deviceTemp = talonFX.getDeviceTemp();
    registerSignal(deviceTemp);

    talonFX.getProcessorTemp();

    talonFX.getDifferentialAveragePosition();
    talonFX.getDifferentialAverageVelocity();
    talonFX.getDifferentialDifferencePosition();
    talonFX.getDifferentialDifferenceVelocity();

    talonFX.getDutyCycle();
    talonFX.getForwardLimit();
    talonFX.getReverseLimit();
    talonFX.getIsProLicensed();
    talonFX.getMotionMagicIsRunning();
    talonFX.getMotorVoltage();

    talonFX.getPosition();
    talonFX.getRotorPosition();
    talonFX.getRotorVelocity();
    talonFX.getStatorCurrent();
    talonFX.getSupplyCurrent();
    talonFX.getSupplyVoltage();
    talonFX.getTorqueCurrent();
    talonFX.getVelocity();

    talonFX.getClosedLoopDerivativeOutput();
    talonFX.getClosedLoopError();
    talonFX.getClosedLoopFeedForward();
    talonFX.getClosedLoopIntegratedOutput();
    talonFX.getClosedLoopOutput();
    talonFX.getClosedLoopProportionalOutput();
    talonFX.getClosedLoopReference();
    talonFX.getClosedLoopReferenceSlope();
    talonFX.getClosedLoopSlot();

    talonFX.getDifferentialClosedLoopDerivativeOutput();
    talonFX.getDifferentialClosedLoopError();
    talonFX.getDifferentialClosedLoopFeedForward();
    talonFX.getDifferentialClosedLoopIntegratedOutput();
    talonFX.getDifferentialClosedLoopOutput();
    talonFX.getDifferentialClosedLoopProportionalOutput();
    talonFX.getDifferentialClosedLoopReference();

  }

  public void watch

}


