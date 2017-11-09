package org.strykeforce.thirdcoast.talon

import com.moandjiezana.toml.Toml
import spock.lang.Specification

import static com.ctre.CANTalon.FeedbackDevice.*
import static com.ctre.CANTalon.TalonControlMode.*
import static com.ctre.CANTalon.VelocityMeasurementPeriod.Period_25Ms
import static com.ctre.CANTalon.VelocityMeasurementPeriod.Period_5Ms

class TalonConfigurationBuilderTest extends Specification {

    TalonConfigurationBuilder tcb = new TalonConfigurationBuilder()

    def "creates VoltageTalonConfiguration from TOML config"() {
        def input = '''
name = "foo"
mode = "Voltage"
setpointMax = 12.0
'''
        when:
        def vtc = TalonConfigurationBuilder.create(new Toml().read(input))

        then:
        vtc instanceof VoltageTalonConfiguration
        vtc.name == 'foo'
        vtc.setpointMax == 12.0
    }

    def "creates SpeedTalonConfiguration from TOML config"() {
        def input = '''
name = "bar"
mode = "Speed"
setpointMax = 120.0
pGain = 1.2
'''
        when:
        def stc = (SpeedTalonConfiguration) TalonConfigurationBuilder.create(new Toml().read(input))

        then:
        stc.name == 'bar'
        stc.setpointMax == 120.0
        stc.PGain == 1.2
    }

    def "builds VoltageTalonConfiguration with defaults"() {
        when:
        def tc = tcb.build()

        then:
        tc instanceof VoltageTalonConfiguration
        tc.name == TalonConfigurationBuilder.DEFAULT_NAME
        tc.setpointMax == 12.0
        tc.currentLimit == null
        tc.encoder == null
        tc.brakeInNeutral == null
        tc.outputReversed == null
        tc.velocityMeasurementPeriod == null
        tc.velocityMeasurementWindow == null
        tc.forwardLimitSwitch == null
        tc.reverseLimitSwitch == null
        tc.forwardSoftLimit == null
        tc.reverseSoftLimit == null
    }

    def "reads talon parameters"() {
        when:
        def tc = tcb.name("test")
                .mode(Voltage)
                .setpointMax(12)
                .encoder(QuadEncoder, true, 360)
                .brakeInNeutral(true)
                .forwardLimitSwitch(true)
                .forwardSoftLimit(10000)
                .reverseSoftLimit(12000)
                .outputReversed(true)
                .velocityMeasurementPeriod(Period_5Ms)
                .velocityMeasurementWindow(16)
                .currentLimit(50)
                .build()

        then:
        tc.name == "test"
        tc.encoder.device == QuadEncoder
        tc.encoder.reversed
        tc.encoder.unitScalingEnabled
        tc.encoder.ticksPerRevolution == 360
        tc.brakeInNeutral
        tc.forwardLimitSwitch.enabled
        tc.forwardLimitSwitch.normallyOpen
        tc.forwardSoftLimit.enabled
        tc.forwardSoftLimit.position == 10000
        tc.reverseSoftLimit.enabled
        tc.reverseSoftLimit.position == 12000
        tc.outputReversed
        tc.velocityMeasurementPeriod == Period_5Ms
        tc.velocityMeasurementWindow == 16
        tc.currentLimit == 50
    }


    def "creates TOML for SpeedTalonConfiguration"() {
        when:
        def toml = new Toml().read(tcb.mode(Speed).P(27.67).getToml())

        then:
        toml.getString("name") == TalonConfigurationBuilder.DEFAULT_NAME
        toml.getDouble("setpointMax") == 12.0
        toml.getDouble("pGain") == 27.67
    }

    def "builds Position with defaults"() {
        when:
        def tc = tcb.mode(Position).build()

        then:
        tc instanceof PositionTalonConfiguration
    }

    def "builds Speed with defaults"() {
        when:
        def tc = tcb.mode(Speed).build()

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
        def tc = tcb.encoder(EncRising, true, 2767).build()

        then:
        tc.encoder.device == EncRising
        tc.encoder.reversed
        tc.encoder.ticksPerRevolution == 2767
    }

    def "configures only encoder reversed"() {
        when:
        def tc = tcb.encoderReversed(true).build()

        then:
        tc.encoder.device == QuadEncoder
        tc.encoder.reversed
        !tc.encoder.unitScalingEnabled
    }

    def "configure brake in neutral"() {
        when:
        def tc = tcb.brakeInNeutral(false).build()

        then:
        !tc.brakeInNeutral
    }

    def "configure velocity measurement period"() {
        when:
        def tc = tcb.velocityMeasurementPeriod(Period_25Ms).build()

        then:
        tc.velocityMeasurementPeriod == Period_25Ms
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
        tc.forwardSoftLimit.position == 27.67
    }

    def "configure reverse soft limit"() {
        when:
        def tc = tcb.reverseSoftLimit(27.67).build()

        then:
        tc.reverseSoftLimit.enabled
        tc.reverseSoftLimit.position == 27.67
    }

    def "configure current limit"() {
        when:
        def tc = tcb.currentLimit(2767).build()

        then:
        tc.currentLimit == 2767
    }

    def "configure voltage ramp rate"() {
        when:
        def tc = tcb.voltageRampRate(27.67).build()

        then:
        tc.voltageRampRate == 27.67
    }

    // PIDTalonConfig
    def "configure max output voltage limit"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(Speed).outputVoltageMax(27.67).build()

        then:
        tc.outputVoltageMax == 27.67
    }

    def "configure closed loop ramp rate limit"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(Speed).closedLoopRampRate(27.67).build()

        then:
        tc.closedLoopRampRate == 27.67
    }

    def "configure peak output voltage limit"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(Speed).outputVoltagePeak(2.7, 6.7).build()

        then:
        tc.forwardOutputVoltagePeak == 2.7
        tc.reverseOutputVoltagePeak == 6.7
    }

    def "configure nominal output voltage limit"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(Speed).outputVoltageNominal(2.7, 6.7).build()

        then:
        tc.forwardOutputVoltageNominal == 2.7
        tc.reverseOutputVoltageNominal == 6.7
    }

    def "configure allowable closed loop error"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(Speed).allowableClosedLoopError(2767).build()

        then:
        tc.allowableClosedLoopError == 2767
    }

    def "configure nominal closed-loop voltage"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(Speed).nominalClosedLoopVoltage(27.67).build()

        then:
        tc.nominalClosedLoopVoltage == 27.67
    }

    def "configure P"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(Speed).P(27.67).build()

        then:
        tc.PGain == 27.67
    }

    def "configure I"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(Speed).I(27.67).build()

        then:
        tc.IGain == 27.67
    }

    def "configure D"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(Speed).D(27.67).build()

        then:
        tc.DGain == 27.67
    }

    def "configure F"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(Speed).F(27.67).build()

        then:
        tc.FGain == 27.67
    }

    def "configure I-zone"() {
        when:
        PIDTalonConfiguration tc = tcb.mode(Speed).iZone(2767).build()

        then:
        tc.IZone == 2767
    }

    def "checks config for proper operating mode"() {
        given:
        def toml = new Toml().read("mode = \"Disabled\"\nsetpointMax = 0.0")

        expect:
        TalonConfigurationBuilder.getMode(toml) == Disabled
    }

    def "checks config for bad mode"() {
        given:
        def toml = new Toml().read(input)

        when:
        TalonConfigurationBuilder.getMode(toml)

        then:
        IllegalArgumentException e = thrown()
        e.message == message

        where:
        input                                 | message
        ''                                    | 'mode missing from configuration'
        "mode = \"Bogus\"\nsetpointMax = 0.0" | 'No enum constant com.ctre.CANTalon.TalonControlMode.Bogus'
        'mode = "Voltage"'                    | 'setpointMax missing from configuration'
    }
}

