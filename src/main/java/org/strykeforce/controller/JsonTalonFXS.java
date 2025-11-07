package org.strykeforce.controller;

import com.ctre.phoenix6.configs.*;
import com.ctre.phoenix6.signals.*;
import edu.wpi.first.math.MathUtil;

public class JsonTalonFXS {

    // Audio
    private boolean allowMusicDuringDisable = false;
    private boolean beepOnBoot = true;
    private boolean beepOnConfig = true;

    // Closed Loop Gen
    private boolean continuousWrap = false;

    // Closed Loop Ramp
    private double dutyCycleClosedLoopRampPeriod = 0; // seconds
    private double voltageClosedLoopRampPeriod = 0; // seconds
    private double torqueCurrentClosedLoopRampPeriod = 0; // seconds

    // Commutation
    private boolean advancedHallSupport = false;
    private String brushedMotorWiring = "Leads_A_and_B";
    private String motorArrangement = "Disabled";

    // Current Limit
    private double statorCurrentLimit = 120; // A
    private boolean statorCurrentLimitEnable = true;
    private double supplyCurrentLimit = 70; // A
    private boolean supplyCurrentLimitEnable = true;
    private double supplyCurrentLowerLimit = 40; // A
    private double supplyCurrentLowerTime = 1; // seconds

    // Custom
    private int customParam0 = 0;
    private int customParam1 = 0;

    // Differential
    private double peakDifferentialDutyCycle = 2; // percent
    private double peakDifferentialVoltage = 32; // V
    private double peakDifferentialTorqueCurrent = 1600; // A

    // Differential Sensors
    private String differentialSensorSource = "Disabled";
    private int differentialTalonFXSensorID = 0;
    private int differentialRemoteSensorID = 0;

    // External Feedback
    private double absSensorDiscontinuity = 0.5;
    private double absSensorOffset = 0;
    private String externalFeedbackSource = "Commutation";
    private int feedbackRemoteSensorID = 0;
    private int quadEdgesPerRot = 4096;
    private double rotorToSensorRatio = 1;
    private String sensorPhase = "Aligned";
    private double sensorToMechanismRatio = 1;
    private double velocityFilterTimeConstant = 0; // seconds

    // Future Proof
    private boolean futureProofConfigs = true;

    // Hardware Limit Switch
    private boolean forwardLimitAutosetPositionEnable = false;
    private double forwardLimitAutosetPositionValue = 0; // rotations
    private boolean forwardLimitEnable = true;
    private int forwardLimitRemoteSensorId = 0;
    private String forwardLimitSource = "LimitSwitchPin";
    private String forwardLimitType = "NormallyOpen";
    private boolean reverseLimitAutosetPositionEnable = false;
    private double reverseLimitAutosetPositionValue = 0; // rotations
    private boolean reverseLimitEnable = true;
    private int reverseLimitRemoteSensorId = 0;
    private String reverseLimitSource = "LimitSwitchPin";
    private String reverseLimitType = "NormallyOpen";

    // Motion Magic
    private double motionMagicAcceleration = 0; // rot/sec^2
    private double motionMagicCruiseVelocity = 0; // rot/s
    private double motionMagicExpo_kA = 0.1; // V/rps^2
    private double motionMagicExpo_kV = 0.12; // V/rps
    private double motionMagicJerk = 0; // rot/sec^3

    // Motor Output
    private double controlTimesyncFreqHz = 0; // Hz
    private double dutyCycleNeutralDeadband = 0; // percent
    private String inverted = "CounterClockwise_Positive";
    private String neutralMode = "Coast";
    private double peakForwardDutyCycle = 1; // percent
    private double peakReverseDutyCycle = -1; // percent

    // Open Loop Ramp
    private double dutyCycleOpenLoopRampPeriod = 0; // seconds
    private double torqueCurrentOpenLoopRampPeriod = 0; // seconds
    private double voltageOpenLoopRampPeriod = 0; // seconds

    // Slot 0 Configs
    private String slot0GravityType = "Elevator_Static";
    private double slot0kA = 0;
    private double slot0kD = 0;
    private double slot0kG = 0;
    private double slot0kI = 0;
    private double slot0kP = 0;
    private double slot0kS = 0;
    private double slot0kV = 0;
    private String slot0StaticFeedForwardSign = "UseVelocitySign";

    // Slot 1 Configs
    private String slot1GravityType = "Elevator_Static";
    private double slot1kA = 0;
    private double slot1kD = 0;
    private double slot1kG = 0;
    private double slot1kI = 0;
    private double slot1kP = 0;
    private double slot1kS = 0;
    private double slot1kV = 0;
    private String slot1StaticFeedForwardSign = "UseVelocitySign";

    // Slot 2 Configs
    private String slot2GravityType = "Elevator_Static";
    private double slot2kA = 0;
    private double slot2kD = 0;
    private double slot2kG = 0;
    private double slot2kI = 0;
    private double slot2kP = 0;
    private double slot2kS = 0;
    private double slot2kV = 0;
    private String slot2StaticFeedForwardSign = "UseVelocitySign";

    // Software Limit Switch
    private boolean forwardSoftLimitEnable = false;
    private double forwardSoftLimitThreshold = 0; // rotations
    private boolean reverseSoftLimitEnable = false;
    private double reverseSoftLimitThreshold = 0; // rotations

    // Voltage
    private double peakForwardVoltage = 16; // V
    private double peakReverseVoltage = -16; // V
    private double supplyVoltageTimeConstant = 0; // seconds
    //    private double setPosition = 0; //rotations

    // TCT
    //    private String activeUnit = "Percent";
    //    private int activeSlot = 0;
    //    private String activeMMtype = "Standard";
    //    private double activeFeedForward = 0;
    //    private double activeTorqueCurrentDeadband = 0;
    //    private double activeTorqueCurrentMaxOut = 1;
    //    private double activeVelocity = 0;
    //    private double activeAcceleration = 0;
    //    private double activeJerk = 0;
    //    private double activeDifferentialTarget = 0;
    //    private String activeFollowerType = "STrict";
    //    private boolean activeOpposeMain = false;
    //    private int activeDifferentialSlot = 0;
    //    private boolean activeFOC = false;
    //    private String setpointType = "Open_Loop";
    //    private String differentialType = "Open_Loop";
    //    private String activeNeutralOut = "Coast";
    //    private boolean activSeOverrideNeutral = false;
    //    private boolean limitFwdMotion = false;
    //    private boolean limitRevMotion = false;
    //    private double grapherStatusFrameHz = 20;

    // Future TCT
    private String openLoopUnits = "Percent";
    private String closedLoopUnits = "Voltage";
    private String mmType = "Standard";
    private String followerType = "Strict";
    private String followerConfig = "Standard";
    private String differentialType = "Open_Loop";
    private String closedLoopType = "Velocity";
    //  private String setpointType = "Open_Loop";
    private int leaderID = 0;
    private int activeSlot = 0;
    private int differentialSlot = 0;
    private boolean activeFOC = false;
    private boolean limitFwdMotion = false;
    private boolean limitRevMotion = false;
    private boolean ignoreHwLims = false;
    private boolean useTimesync = false;
    private boolean opposeMain = false;
    private double torquecCurrentMax = 0;
    private boolean overrideNeutral = false;

    public AudioConfigs getAudioConfigs() {
        return new AudioConfigs()
                .withAllowMusicDurDisable(allowMusicDuringDisable)
                .withBeepOnBoot(beepOnBoot)
                .withBeepOnConfig(beepOnConfig);
    }

    public ClosedLoopGeneralConfigs getClosedLoopGeneralConfigs() {
        return new ClosedLoopGeneralConfigs().withContinuousWrap(continuousWrap);
    }

    public ClosedLoopRampsConfigs getClosedLoopRampConfigs() {
        dutyCycleClosedLoopRampPeriod = MathUtil.clamp(dutyCycleClosedLoopRampPeriod, 0.0, 1.0);
        voltageClosedLoopRampPeriod = MathUtil.clamp(voltageClosedLoopRampPeriod, 0.0, 1.0);
        torqueCurrentClosedLoopRampPeriod = MathUtil.clamp(torqueCurrentClosedLoopRampPeriod, 0.0, 10.0);
        return new ClosedLoopRampsConfigs()
                .withDutyCycleClosedLoopRampPeriod(dutyCycleClosedLoopRampPeriod)
                .withVoltageClosedLoopRampPeriod(voltageClosedLoopRampPeriod)
                .withTorqueClosedLoopRampPeriod(torqueCurrentClosedLoopRampPeriod);
    }

    public CommutationConfigs getCommutationConfigs() {
        AdvancedHallSupportValue hallSupport = AdvancedHallSupportValue.Disabled;
        if(advancedHallSupport)
            hallSupport = AdvancedHallSupportValue.Enabled;

        BrushedMotorWiringValue brushWiring = BrushedMotorWiringValue.Leads_A_and_B;
        if(brushedMotorWiring.equals("Leads_A_and_C"))
            brushWiring = BrushedMotorWiringValue.Leads_A_and_C;
        else if(brushedMotorWiring.equals("Leads_B_and_C"))
            brushWiring = BrushedMotorWiringValue.Leads_B_and_C;

        MotorArrangementValue arrange = MotorArrangementValue.Disabled;
        if(motorArrangement.equals("Minion_JST"))
            arrange = MotorArrangementValue.Minion_JST;
        else if(motorArrangement.equals("Brushed_DC"))
            arrange = MotorArrangementValue.Brushed_DC;
        else if(motorArrangement.equals("NEO_JST"))
            arrange = MotorArrangementValue.NEO_JST;
        else if (motorArrangement.equals("NEO550_JST"))
            arrange = MotorArrangementValue.NEO550_JST;
        else if (motorArrangement.equals("VORTEX_JST"))
            arrange = MotorArrangementValue.VORTEX_JST;

        return new CommutationConfigs()
                .withAdvancedHallSupport(hallSupport)
                .withBrushedMotorWiring(brushWiring)
                .withMotorArrangement(arrange);
    }

    public CurrentLimitsConfigs getCurrentLimitConfigs() {
        statorCurrentLimit = MathUtil.clamp(statorCurrentLimit, 0.0, 800.0);
        supplyCurrentLimit = MathUtil.clamp(supplyCurrentLimit, 0.0, 800.0);
        supplyCurrentLowerLimit = MathUtil.clamp(supplyCurrentLowerLimit,.0, 500.0);
        supplyCurrentLowerTime = MathUtil.clamp(supplyCurrentLowerTime, 0.0, 2.5);
        return new CurrentLimitsConfigs()
                .withStatorCurrentLimit(statorCurrentLimit)
                .withStatorCurrentLimitEnable(statorCurrentLimitEnable)
                .withSupplyCurrentLimit(supplyCurrentLimit)
                .withSupplyCurrentLimitEnable(supplyCurrentLimitEnable)
                .withSupplyCurrentLowerLimit(supplyCurrentLowerLimit)
                .withSupplyCurrentLowerTime(supplyCurrentLowerTime);
    }

    public CustomParamsConfigs getCustomParamConfigs() {
        customParam0 = MathUtil.clamp(customParam0, -32768, 32767);
        customParam1 = MathUtil.clamp(customParam1, -32768, 32767);
        return new CustomParamsConfigs().withCustomParam0(customParam0).withCustomParam1(customParam1);
    }

    public DifferentialConstantsConfigs getDifferentialConstantsConfig() {
        peakDifferentialDutyCycle = MathUtil.clamp(peakDifferentialDutyCycle, 0.0, 2.0);
        peakDifferentialVoltage = MathUtil.clamp(peakDifferentialVoltage, 0.0, 32.0);
        peakDifferentialTorqueCurrent = MathUtil.clamp(peakDifferentialTorqueCurrent, 0.0, 1600.0);
        return new DifferentialConstantsConfigs()
                .withPeakDifferentialDutyCycle(peakDifferentialDutyCycle)
                .withPeakDifferentialVoltage(peakDifferentialVoltage)
                .withPeakDifferentialTorqueCurrent(peakDifferentialTorqueCurrent);
    }

    public DifferentialSensorsConfigs getDifferentialSensorConfigs() {
        DifferentialSensorSourceValue sensorSource = DifferentialSensorSourceValue.Disabled;
        if (differentialSensorSource.equals("RemoteTalonFX_Diff"))
            sensorSource = DifferentialSensorSourceValue.RemoteTalonFX_Diff;
        else if (differentialSensorSource.equals("RemotePigeon2_Yaw"))
            sensorSource = DifferentialSensorSourceValue.RemotePigeon2_Yaw;
        else if (differentialSensorSource.equals("RemotePigeon2_Pitch"))
            sensorSource = DifferentialSensorSourceValue.RemotePigeon2_Pitch;
        else if (differentialSensorSource.equals("RemotePigeon2_Roll"))
            sensorSource = DifferentialSensorSourceValue.RemotePigeon2_Roll;
        else if (differentialSensorSource.equals("RemoteCANcoder"))
            sensorSource = DifferentialSensorSourceValue.RemoteCANcoder;

        differentialTalonFXSensorID = MathUtil.clamp(differentialTalonFXSensorID, 0, 62);
        differentialRemoteSensorID = MathUtil.clamp(differentialRemoteSensorID, 0, 62);

        return new DifferentialSensorsConfigs()
                .withDifferentialSensorSource(sensorSource)
                .withDifferentialTalonFXSensorID(differentialTalonFXSensorID)
                .withDifferentialRemoteSensorID(differentialRemoteSensorID);
    }

    public ExternalFeedbackConfigs getExternalFeedbackConfigs() {
        ExternalFeedbackSensorSourceValue sensorSource = ExternalFeedbackSensorSourceValue.Commutation;
        if (externalFeedbackSource.equals("FusedCANcoder"))
            sensorSource = ExternalFeedbackSensorSourceValue.FusedCANcoder;
        else if (externalFeedbackSource.equals("FusedCANdiPWM1"))
            sensorSource = ExternalFeedbackSensorSourceValue.FusedCANdiPWM1;
        else if (externalFeedbackSource.equals("FusedCANdiPWM2"))
            sensorSource = ExternalFeedbackSensorSourceValue.FusedCANdiPWM2;
        else if (externalFeedbackSource.equals("FusedCANdiQuadrature"))
            sensorSource = ExternalFeedbackSensorSourceValue.FusedCANdiQuadrature;
        else if (externalFeedbackSource.equals("PulseWidth"))
            sensorSource = ExternalFeedbackSensorSourceValue.PulseWidth;
        else if (externalFeedbackSource.equals("Quadrature"))
            sensorSource = ExternalFeedbackSensorSourceValue.Quadrature;
        else if(externalFeedbackSource.equals("RemoteCANcoder"))
            sensorSource = ExternalFeedbackSensorSourceValue.RemoteCANcoder;
        else if (externalFeedbackSource.equals("RemoteCANdiPWM1"))
            sensorSource = ExternalFeedbackSensorSourceValue.RemoteCANdiPWM1;
        else if (externalFeedbackSource.equals("RemoteCANdiPWM2"))
            sensorSource = ExternalFeedbackSensorSourceValue.RemoteCANdiPWM2;
        else if (externalFeedbackSource.equals("RemoteCANdiQuadrature"))
            sensorSource = ExternalFeedbackSensorSourceValue.RemoteCANdiQuadrature;
        else if (externalFeedbackSource.equals("RemotePigeon2_Roll"))
            sensorSource = ExternalFeedbackSensorSourceValue.RemotePigeon2_Roll;
        else if (externalFeedbackSource.equals("RemotePigeon2_Pitch"))
            sensorSource = ExternalFeedbackSensorSourceValue.RemotePigeon2_Pitch;
        else if (externalFeedbackSource.equals("RemotePigeon2_Yaw"))
            sensorSource = ExternalFeedbackSensorSourceValue.RemotePigeon2_Yaw;
        else if(externalFeedbackSource.equals("SyncCANcoder"))
            sensorSource = ExternalFeedbackSensorSourceValue.SyncCANcoder;
        else if (externalFeedbackSource.equals("SyncCANdiPWM1"))
            sensorSource = ExternalFeedbackSensorSourceValue.SyncCANdiPWM1;
        else if (externalFeedbackSource.equals("SyncCANdiPWM2"))
            sensorSource = ExternalFeedbackSensorSourceValue.SyncCANdiPWM2;

        SensorPhaseValue phase = SensorPhaseValue.Aligned;
        if(sensorPhase.equals("Opposed"))
            phase = SensorPhaseValue.Opposed;

        absSensorDiscontinuity = MathUtil.clamp(absSensorDiscontinuity, 0.0, 1.0);
        quadEdgesPerRot = MathUtil.clamp(quadEdgesPerRot, 1, 1000000);
        absSensorOffset = MathUtil.clamp(absSensorOffset, -1.0, 1.0);
        velocityFilterTimeConstant = MathUtil.clamp(velocityFilterTimeConstant, 0.0, 1.0);
        feedbackRemoteSensorID = MathUtil.clamp(feedbackRemoteSensorID, 0, 62);
        rotorToSensorRatio = MathUtil.clamp(rotorToSensorRatio, -1000.0, 1000.0);
        sensorToMechanismRatio = MathUtil.clamp(sensorToMechanismRatio, -1000.0, 1000.0);

        return new ExternalFeedbackConfigs()
                .withAbsoluteSensorDiscontinuityPoint(absSensorDiscontinuity)
                .withAbsoluteSensorOffset(absSensorOffset)
                .withExternalFeedbackSensorSource(sensorSource)
                .withFeedbackRemoteSensorID(feedbackRemoteSensorID)
                .withQuadratureEdgesPerRotation(quadEdgesPerRot)
                .withRotorToSensorRatio(rotorToSensorRatio)
                .withSensorPhase(phase)
                .withSensorToMechanismRatio(sensorToMechanismRatio)
                .withVelocityFilterTimeConstant(velocityFilterTimeConstant);
    }

    public boolean getFutureProofConfigs() {
        return futureProofConfigs;
    }

    public HardwareLimitSwitchConfigs getHardwareLimitSwitchConfigs() {
        ForwardLimitSourceValue fwdSource = ForwardLimitSourceValue.LimitSwitchPin;
        if (forwardLimitSource.equals("RemoteTalonFX"))
            fwdSource = ForwardLimitSourceValue.RemoteTalonFX;
        else if (forwardLimitSource.equals("RemoteCANifier"))
            fwdSource = ForwardLimitSourceValue.RemoteCANifier;
        else if (forwardLimitSource.equals("RemoteCANcoder"))
            fwdSource = ForwardLimitSourceValue.RemoteCANcoder;
        else if (forwardLimitSource.equals("RemoteCANrange"))
            fwdSource = ForwardLimitSourceValue.RemoteCANrange;
        else if (forwardLimitSource.equals("RemoteCANdiS1"))
            fwdSource = ForwardLimitSourceValue.RemoteCANdiS1;
        else if (forwardLimitSource.equals("RemoteCANdiS2"))
            fwdSource = ForwardLimitSourceValue.RemoteCANdiS2;
        else if (forwardLimitSource.equals("Disabled")) fwdSource = ForwardLimitSourceValue.Disabled;

        ForwardLimitTypeValue fwdType = ForwardLimitTypeValue.NormallyOpen;
        if (forwardLimitType.equals("NormallyClosed")) fwdType = ForwardLimitTypeValue.NormallyClosed;

        ReverseLimitSourceValue revSource = ReverseLimitSourceValue.LimitSwitchPin;
        if (reverseLimitSource.equals("RemoteTalonFX"))
            revSource = ReverseLimitSourceValue.RemoteTalonFX;
        else if (reverseLimitSource.equals("RemoteCANifier"))
            revSource = ReverseLimitSourceValue.RemoteCANifier;
        else if (reverseLimitSource.equals("RemoteCANcoder"))
            revSource = ReverseLimitSourceValue.RemoteCANcoder;
        else if (reverseLimitSource.equals("RemoteCANrange"))
            revSource = ReverseLimitSourceValue.RemoteCANrange;
        else if (reverseLimitSource.equals("RemoteCANdiS1"))
            revSource = ReverseLimitSourceValue.RemoteCANdiS1;
        else if (reverseLimitSource.equals("RemoteCANdiS2"))
            revSource = ReverseLimitSourceValue.RemoteCANdiS2;
        else if (reverseLimitSource.equals("Disabled")) revSource = ReverseLimitSourceValue.Disabled;

        ReverseLimitTypeValue revType = ReverseLimitTypeValue.NormallyOpen;
        if (reverseLimitType.equals("NormallyClosed")) revType = ReverseLimitTypeValue.NormallyClosed;

        forwardLimitAutosetPositionValue = MathUtil.clamp(forwardLimitAutosetPositionValue, -3.4e38, 3.4e38);
        forwardLimitRemoteSensorId = MathUtil.clamp(forwardLimitRemoteSensorId, 0, 62);
        reverseLimitAutosetPositionValue = MathUtil.clamp(reverseLimitAutosetPositionValue, -3.4e38, 3.4e38);
        reverseLimitRemoteSensorId = MathUtil.clamp(reverseLimitRemoteSensorId, 0, 62);

        return new HardwareLimitSwitchConfigs()
                .withForwardLimitAutosetPositionEnable(forwardLimitAutosetPositionEnable)
                .withForwardLimitAutosetPositionValue(forwardLimitAutosetPositionValue)
                .withForwardLimitEnable(forwardLimitEnable)
                .withForwardLimitRemoteSensorID(forwardLimitRemoteSensorId)
                .withForwardLimitSource(fwdSource)
                .withForwardLimitType(fwdType)
                .withReverseLimitAutosetPositionEnable(reverseLimitAutosetPositionEnable)
                .withReverseLimitAutosetPositionValue(reverseLimitAutosetPositionValue)
                .withReverseLimitEnable(reverseLimitEnable)
                .withReverseLimitRemoteSensorID(reverseLimitRemoteSensorId)
                .withReverseLimitSource(revSource)
                .withReverseLimitType(revType);
    }

    public MotionMagicConfigs getMotionMagicConfigs() {
        motionMagicCruiseVelocity = MathUtil.clamp(motionMagicCruiseVelocity, 0.0, 9999.0);
        motionMagicAcceleration = MathUtil.clamp(motionMagicAcceleration, 0.0, 9999.0);
        motionMagicJerk = MathUtil.clamp(motionMagicJerk, 0.0, 9999.0);
        motionMagicExpo_kV = MathUtil.clamp(motionMagicExpo_kV, 0.001, 100.0);
        motionMagicExpo_kA = MathUtil.clamp(motionMagicExpo_kA, 1e-5, 100.0);

        return new MotionMagicConfigs()
                .withMotionMagicAcceleration(motionMagicAcceleration)
                .withMotionMagicCruiseVelocity(motionMagicCruiseVelocity)
                .withMotionMagicExpo_kA(motionMagicExpo_kA)
                .withMotionMagicExpo_kV(motionMagicExpo_kV)
                .withMotionMagicJerk(motionMagicJerk);
    }

    public MotorOutputConfigs getMotorOutputConfigs() {
        InvertedValue isInverted = InvertedValue.CounterClockwise_Positive;
        if (inverted.equals("Clockwise_Positive")) isInverted = InvertedValue.Clockwise_Positive;

        NeutralModeValue setNeutralMode = NeutralModeValue.Coast;
        if (neutralMode.equals("Brake")) setNeutralMode = NeutralModeValue.Brake;

        controlTimesyncFreqHz = MathUtil.clamp(controlTimesyncFreqHz, 50.0, 500.0);
        peakReverseDutyCycle = MathUtil.clamp(peakReverseDutyCycle, -1.0, 1.0);
        peakForwardDutyCycle = MathUtil.clamp(peakForwardDutyCycle, -1.0, 1.0);
        dutyCycleNeutralDeadband = MathUtil.clamp(dutyCycleNeutralDeadband, 0.0, 0.25);

        return new MotorOutputConfigs()
                .withControlTimesyncFreqHz(controlTimesyncFreqHz)
                .withDutyCycleNeutralDeadband(dutyCycleNeutralDeadband)
                .withInverted(isInverted)
                .withNeutralMode(setNeutralMode)
                .withPeakForwardDutyCycle(peakForwardDutyCycle)
                .withPeakReverseDutyCycle(peakReverseDutyCycle);
    }

    public OpenLoopRampsConfigs getOpenLoopRampConfigs() {
        dutyCycleOpenLoopRampPeriod = MathUtil.clamp(dutyCycleOpenLoopRampPeriod, 0.0, 1.0);
        voltageOpenLoopRampPeriod = MathUtil.clamp(voltageOpenLoopRampPeriod, 0.0, 1.0);
        torqueCurrentOpenLoopRampPeriod = MathUtil.clamp(torqueCurrentOpenLoopRampPeriod, 0.0, 10.0);

        return new OpenLoopRampsConfigs()
                .withDutyCycleOpenLoopRampPeriod(dutyCycleOpenLoopRampPeriod)
                .withTorqueOpenLoopRampPeriod(torqueCurrentOpenLoopRampPeriod)
                .withVoltageOpenLoopRampPeriod(voltageOpenLoopRampPeriod);
    }

    public Slot0Configs getSlot0Configs() {
        GravityTypeValue gravityType = GravityTypeValue.Elevator_Static;
        if (slot0GravityType.equals("Arm_Cosine")) gravityType = GravityTypeValue.Arm_Cosine;

        StaticFeedforwardSignValue feedFwdSign = StaticFeedforwardSignValue.UseVelocitySign;
        if (slot0StaticFeedForwardSign.equals("UseClosedLoopSign"))
            feedFwdSign = StaticFeedforwardSignValue.UseClosedLoopSign;

        slot0kA = MathUtil.clamp(slot0kA, 0.0, 3.4e38);
        slot0kG = MathUtil.clamp(slot0kG, -512.0, 511.0);
        slot0kV = MathUtil.clamp(slot0kV, 0.0, 3.4e38);
        slot0kS = MathUtil.clamp(slot0kS, -512.0, 511.0);
        slot0kD = MathUtil.clamp(slot0kD, 0.0, 3.4e38);
        slot0kI = MathUtil.clamp(slot0kI, 0.0, 3.4e38);
        slot0kP = MathUtil.clamp(slot0kP, 0.0, 3.4e38);

        return new Slot0Configs()
                .withGravityType(gravityType)
                .withKA(slot0kA)
                .withKD(slot0kD)
                .withKG(slot0kG)
                .withKI(slot0kI)
                .withKP(slot0kP)
                .withKS(slot0kS)
                .withKV(slot0kV)
                .withStaticFeedforwardSign(feedFwdSign);
    }

    public Slot1Configs getSlot1Configs() {
        GravityTypeValue gravityType = GravityTypeValue.Elevator_Static;
        if (slot1GravityType.equals("Arm_Cosine")) gravityType = GravityTypeValue.Arm_Cosine;

        StaticFeedforwardSignValue feedFwdSign = StaticFeedforwardSignValue.UseVelocitySign;
        if (slot1StaticFeedForwardSign.equals("UseClosedLoopSign"))
            feedFwdSign = StaticFeedforwardSignValue.UseClosedLoopSign;

        slot1kA = MathUtil.clamp(slot1kA, 0.0, 3.4e38);
        slot1kG = MathUtil.clamp(slot1kG, -512.0, 511.0);
        slot1kV = MathUtil.clamp(slot1kV, 0.0, 3.4e38);
        slot1kS = MathUtil.clamp(slot1kS, -512.0, 511.0);
        slot1kD = MathUtil.clamp(slot1kD, 0.0, 3.4e38);
        slot1kP = MathUtil.clamp(slot1kP, 0.0, 3.4e38);
        slot1kI = MathUtil.clamp(slot1kI, 0.0, 3.4e38);

        return new Slot1Configs()
                .withGravityType(gravityType)
                .withKA(slot1kA)
                .withKD(slot1kD)
                .withKG(slot1kG)
                .withKI(slot1kI)
                .withKP(slot1kP)
                .withKS(slot1kS)
                .withKV(slot1kV)
                .withStaticFeedforwardSign(feedFwdSign);
    }

    public Slot2Configs getSlot2Configs() {
        GravityTypeValue gravityType = GravityTypeValue.Elevator_Static;
        if (slot2GravityType.equals("Arm_Cosine")) gravityType = GravityTypeValue.Arm_Cosine;

        StaticFeedforwardSignValue feedFwdSign = StaticFeedforwardSignValue.UseVelocitySign;
        if (slot2StaticFeedForwardSign.equals("UseClosedLoopSign"))
            feedFwdSign = StaticFeedforwardSignValue.UseClosedLoopSign;

        slot2kA = MathUtil.clamp(slot2kA, 0.0, 3.4e38);
        slot2kG = MathUtil.clamp(slot2kG, -512.0, 511.0);
        slot2kV = MathUtil.clamp(slot2kV, 0.0, 3.4e38);
        slot2kS = MathUtil.clamp(slot2kS, -512.0, 511.0);
        slot2kD = MathUtil.clamp(slot2kD, 0.0, 3.4e38);
        slot2kP = MathUtil.clamp(slot2kP, 0.0, 3.4e38);
        slot2kI = MathUtil.clamp(slot2kI, 0.0, 3.4e38);

        return new Slot2Configs()
                .withGravityType(gravityType)
                .withKA(slot2kA)
                .withKD(slot2kD)
                .withKG(slot2kG)
                .withKI(slot2kI)
                .withKP(slot2kP)
                .withKS(slot2kS)
                .withKV(slot2kV)
                .withStaticFeedforwardSign(feedFwdSign);
    }

    public SoftwareLimitSwitchConfigs getSoftwareLimitSwitchConfigs() {
        forwardSoftLimitThreshold = MathUtil.clamp(forwardSoftLimitThreshold, -3.4e38, 3.4e38);
        reverseSoftLimitThreshold = MathUtil.clamp(reverseSoftLimitThreshold, -3.4e38, 3.4e38);

        return new SoftwareLimitSwitchConfigs()
                .withForwardSoftLimitEnable(forwardSoftLimitEnable)
                .withForwardSoftLimitThreshold(forwardSoftLimitThreshold)
                .withReverseSoftLimitEnable(reverseSoftLimitEnable)
                .withReverseSoftLimitThreshold(reverseSoftLimitThreshold);
    }

    public VoltageConfigs getVoltageConfigs() {
        peakForwardVoltage = MathUtil.clamp(peakForwardVoltage, -16.0, 16.0);
        peakReverseVoltage = MathUtil.clamp(peakReverseVoltage, -16.0, 16.0);
        supplyVoltageTimeConstant = MathUtil.clamp(supplyVoltageTimeConstant, 0.0, 0.1);

        return new VoltageConfigs()
                .withPeakForwardVoltage(peakForwardVoltage)
                .withPeakReverseVoltage(peakReverseVoltage)
                .withSupplyVoltageTimeConstant(supplyVoltageTimeConstant);
    }

    public CTRE_Units getOpenLoopUnits() {
        CTRE_Units units = CTRE_Units.Percent;
        if (openLoopUnits.equals("Voltage")) units = CTRE_Units.Voltage;
        else if (openLoopUnits.equals("Torque_Current")) units = CTRE_Units.Torque_Current;

        return units;
    }

    public CTRE_Units getClosedLoopUnits() {
        CTRE_Units units = CTRE_Units.Voltage;
        if (closedLoopUnits.equals("Percent")) units = CTRE_Units.Percent;
        else if (closedLoopUnits.equals("Torque_Current")) units = CTRE_Units.Torque_Current;

        return units;
    }

    public MotionMagicType getMotionMagicType() {
        MotionMagicType type = MotionMagicType.Standard;
        if (mmType.equals("Velocity")) type = MotionMagicType.Velocity;
        else if (mmType.equals("Dynamic")) type = MotionMagicType.Dynamic;
        else if (mmType.equals("Exponential")) type = MotionMagicType.Exponential;

        return type;
    }

    public CTRE_FollowerType getFollowerType() {
        CTRE_FollowerType type = CTRE_FollowerType.Strict;
        if (followerType.equals("Standard")) type = CTRE_FollowerType.Standard;
        return type;
    }

    public CTRE_FollowerConfig getFollowerConfig() {
        CTRE_FollowerConfig config = CTRE_FollowerConfig.Normal;
        if (followerConfig.equals("Differential")) config = CTRE_FollowerConfig.Differential;
        return config;
    }

    public CTRE_DifferentialType getDifferentialType() {
        CTRE_DifferentialType type = CTRE_DifferentialType.Open_Loop;
        if (differentialType.equals("Position")) type = CTRE_DifferentialType.Position;
        else if (differentialType.equals("Velocity")) type = CTRE_DifferentialType.Velocity;
        else if (differentialType.equals("Motion_Magic")) type = CTRE_DifferentialType.Motion_Magic;
        else if (differentialType.equals("Follower")) type = CTRE_DifferentialType.Follower;
        return type;
    }

    public CTRE_ClosedLoopType getClosedLoopType() {
        CTRE_ClosedLoopType type = CTRE_ClosedLoopType.Velocity;
        if (closedLoopType.equals("Position")) type = CTRE_ClosedLoopType.Position;
        else if (closedLoopType.equals("Motion_Magic")) type = CTRE_ClosedLoopType.Motion_Magic;
        return type;
    }

    public int getLeaderID() {
        leaderID = MathUtil.clamp(leaderID, 0, 62);
        return leaderID;
    }

    public int getActiveSlot() {
        activeSlot = MathUtil.clamp(activeSlot, 0, 2);
        return activeSlot;
    }

    public int getDifferentialSlot() {
        differentialSlot = MathUtil.clamp(differentialSlot, 0, 2);
        return differentialSlot;
    }

    public boolean getActiveFOC() {
        return activeFOC;
    }

    public boolean getLimitFwdMotion() {
        return limitFwdMotion;
    }

    public boolean getLimRevMotion() {
        return limitRevMotion;
    }

    public boolean getIgnoreHwLimits() {
        return ignoreHwLims;
    }

    public boolean getUseTimesync() {
        return useTimesync;
    }

    public boolean getOpposeMain() {
        return opposeMain;
    }

    public double getTorquecCurrentMax() {
        torquecCurrentMax = MathUtil.clamp(torquecCurrentMax, -800, 800);
        return torquecCurrentMax;
    }

    public boolean getOverrieNeutral() {
        return overrideNeutral;
    }
}
