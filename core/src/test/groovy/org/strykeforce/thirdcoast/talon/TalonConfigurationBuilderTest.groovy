package org.strykeforce.thirdcoast.talon

import com.ctre.CANTalon
import com.electronwill.nightconfig.core.file.FileConfig
import spock.lang.Specification

class TalonConfigurationBuilderTest extends Specification {

    final static CANTalon.TalonControlMode SPEED = CANTalon.TalonControlMode.Speed
    final static CANTalon.TalonControlMode POSITION = CANTalon.TalonControlMode.Position

    TalonConfigurationBuilder tcb = new TalonConfigurationBuilder()

    def "builds Voltage with defaults"() {
        when:
        def tc = tcb.build()

        then:
        tc instanceof VoltageTalonConfiguration
        tc.name == TalonConfigurationBuilder.DEFAULT_NAME
        tc.setpointMax == 12.0
        tc.currentLimit == null
        tc.encoder == Encoder.DEFAULT
        tc.brakeInNeutral == null
        tc.outputReversed == null
        tc.velocityMeasurementPeriod == null
        tc.velocityMeasurementWindow == null
        tc.forwardLimitSwitch == LimitSwitch.DEFAULT
        tc.reverseLimitSwitch == LimitSwitch.DEFAULT
        tc.forwardSoftLimit == SoftLimit.DEFAULT
        tc.reverseSoftLimit == SoftLimit.DEFAULT
    }

    def "builds Position with defaults"() {
        when:
        def tc = tcb.mode(POSITION).build()

        then:
        tc instanceof PositionTalonConfiguration
    }

    def "builds Speed with defaults"() {
        when:
        def tc = tcb.mode(SPEED).build()

        then:
        tc instanceof SpeedTalonConfiguration
    }

    def "configures name"() {
        when:
        def tc = tcb.name("Stryke Force").build()

        then:
        tc.name == "Stryke Force"
    }

    def "configures max setpoint"() {
        when:
        def tc = tcb.setpointMax(27.67).build()

        then:
        tc.setpointMax == 27.67
    }

    def "configures encoder"() {
        when:
        def tc = tcb.encoder(CANTalon.FeedbackDevice.EncRising, true, 2767).build()

        then:
        tc.encoder.feedbackDevice == CANTalon.FeedbackDevice.EncRising
        tc.encoder.reversed
        tc.encoder.ticksPerRevolution == 2767
    }

    def "configure brake in neutral"() {
        when:
        def tc = tcb.brakeInNeutral(false).build()

        then:
        !tc.brakeInNeutral
    }

    def "configure velocity measurement period"() {
        when:
        def tc = tcb.velocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_25Ms).build()

        then:
        tc.velocityMeasurementPeriod == CANTalon.VelocityMeasurementPeriod.Period_25Ms
    }

    def "configure velocity measurement window"() {
        when:
        def tc = tcb.velocityMeasurementWindow(16).build()

        then:
        tc.getVelocityMeasurementWindow() == 16
    }

    def "configure forward limit switch normally closed"() {
        when:
        def tc = tcb.forwardLimitSwitch(false).build()

        then:
        tc.forwardLimitSwitch.enabled
        !tc.forwardLimitSwitch.normallyOpen
    }

    def "configure reverse limit switch normally open"() {
        when:
        def tc = tcb.reverseLimitSwitch(true).build()

        then:
        tc.reverseLimitSwitch.enabled
        tc.reverseLimitSwitch.normallyOpen
    }

    def "configure foward soft limit"() {
        when:
        def tc = tcb.forwardSoftLimit(27.67).build()

        then:
        tc.forwardSoftLimit.enabled
        tc.forwardSoftLimit.value == 27.67
    }

    def "configure reverse soft limit"() {
        when:
        def tc = tcb.reverseSoftLimit(27.67).build()

        then:
        tc.reverseSoftLimit.enabled
        tc.reverseSoftLimit.value == 27.67
    }

    def "configure current limit"() {
        when:
        def tc = tcb.currentLimit(2767).build()

        then:
        tc.currentLimit == 2767
    }

    // PIDTalonConfig
    def "configure max output voltage limit"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(SPEED).outputVoltageMax(27.67).build()

        then:
        tc.outputVoltageMax == 27.67
    }

    def "configure peak output voltage limit"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(SPEED).outputVoltagePeak(2.7, 6.7).build()

        then:
        tc.forwardOutputVoltagePeak == 2.7
        tc.reverseOutputVoltagePeak == 6.7
    }

    def "configure nominal output voltage limit"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(SPEED).outputVoltageNominal(2.7, 6.7).build()

        then:
        tc.forwardOutputVoltageNominal == 2.7
        tc.reverseOutputVoltageNominal == 6.7
    }

    def "configure allowable closed loop error"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(SPEED).allowableClosedLoopError(2767).build()

        then:
        tc.allowableClosedLoopError == 2767
    }

    def "configure nominal closed-loop voltage"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(SPEED).nominalClosedLoopVoltage(27.67).build()

        then:
        tc.nominalClosedLoopVoltage == 27.67
    }

    def "configure P"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(SPEED).P(27.67).build()

        then:
        tc.pGain == 27.67
    }

    def "configure I"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(SPEED).I(27.67).build()

        then:
        tc.iGain == 27.67
    }

    def "configure D"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(SPEED).D(27.67).build()

        then:
        tc.dGain == 27.67
    }

    def "configure F"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(SPEED).F(27.67).build()

        then:
        tc.fGain == 27.67
    }

    def "configure I-zone"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(SPEED).iZone(2767).build()

        then:
        tc.iZone == 2767
    }

    def "creates a config"() {
        when:
        def config = tcb.name("SF")
                .mode(CANTalon.TalonControlMode.Position)
                .setpointMax(27.67)
                .encoder(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute, false, null)
                .brakeInNeutral(false)
                .forwardLimitSwitch(false)
                .forwardSoftLimit(null)
                .reverseSoftLimit(0)
                .currentLimit(50)
                .config

        then:
        config.get(TalonConfigurationBuilder.NAME) == "SF"
        config.get(TalonConfigurationBuilder.MODE) == CANTalon.TalonControlMode.Position.name()
        config.get(TalonConfigurationBuilder.SETPOINT_MAX) == 27.67
        config.get(TalonConfigurationBuilder.BRAKE_IN_NEUTRAL) == false
        config.get(TalonConfigurationBuilder.FORWARD_LIMIT_SWITCH) == "NormallyClosed"
        !config.contains(TalonConfigurationBuilder.REVERSE_LIMIT_SWITCH)
        !config.contains(TalonConfigurationBuilder.FORWARD_SOFT_LIMIT)
        config.get(TalonConfigurationBuilder.REVERSE_SOFT_LIMIT) == 0.0
        config.get(TalonConfigurationBuilder.CURRENT_LIMIT) == 50
    }
}

