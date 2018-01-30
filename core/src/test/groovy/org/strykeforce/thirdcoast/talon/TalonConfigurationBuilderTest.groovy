package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.NeutralMode
import com.moandjiezana.toml.Toml
import spock.lang.Specification

import static com.ctre.phoenix.motorcontrol.ControlMode.Disabled
import static com.ctre.phoenix.motorcontrol.ControlMode.MotionMagic
import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput
import static com.ctre.phoenix.motorcontrol.ControlMode.Position
import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.CTRE_MagEncoder_Absolute
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder
import static com.ctre.phoenix.motorcontrol.VelocityMeasPeriod.Period_25Ms
import static com.ctre.phoenix.motorcontrol.VelocityMeasPeriod.Period_5Ms

// TODO: test voltageCompSaturation
class TalonConfigurationBuilderTest extends Specification {

    TalonConfigurationBuilder tcb = new TalonConfigurationBuilder()

    def "creates VoltageTalonConfiguration from TOML config"() {
        def input = '''
name = "foo"
mode = "PercentOutput"
setpointMax = 12.0
'''
        when:
        def vtc = TalonConfigurationBuilder.create(new Toml().read(input))

        then:
        vtc instanceof VoltageTalonConfiguration
        vtc.name == 'foo'
        vtc.setpointMax == 12.0d
    }

    def "creates SpeedTalonConfiguration from TOML config"() {
        def input = '''
name = "bar"
mode = "Velocity"
setpointMax = 120.0
pGain = 1.2
'''
        when:
        def stc = (SpeedTalonConfiguration) TalonConfigurationBuilder.create(new Toml().read(input))

        then:
        stc.name == 'bar'
        stc.setpointMax == 120.0d
        stc.PGain == 1.2
    }

    def "builds VoltageTalonConfiguration with defaults"() {
        when:
        def tc = tcb.build()

        then:
        tc instanceof VoltageTalonConfiguration
        tc.name == TalonConfigurationBuilder.DEFAULT_NAME
        tc.setpointMax == 12.0d
        tc.voltageCompSaturation == null
        tc.continuousCurrentLimit == null
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
                .mode(PercentOutput)
                .setpointMax(12)
                .encoder(QuadEncoder, true)
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
        tc.brakeInNeutral == NeutralMode.Brake
        tc.forwardLimitSwitch.enabled
        tc.forwardLimitSwitch.normallyOpen
        tc.forwardSoftLimit.enabled
        tc.forwardSoftLimit.position == 10000
        tc.reverseSoftLimit.enabled
        tc.reverseSoftLimit.position == 12000
        tc.outputReversed
        tc.velocityMeasurementPeriod == Period_5Ms
        tc.velocityMeasurementWindow == 16
        tc.continuousCurrentLimit == 50
    }

    def "creates TOML for SpeedTalonConfiguration"() {
        when:
        def toml = new Toml().read(tcb.mode(Velocity).P(27.67d).getToml())

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
        def tc = tcb.mode(Velocity).build()

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
        def tc = tcb.setpointMax(27.67d).build()

        then:
        tc.setpointMax == 27.67d
    }

    def "configures encoder"() {
        when:
        def tc = tcb.encoder(CTRE_MagEncoder_Absolute, true).build()

        then:
        tc.encoder.device == CTRE_MagEncoder_Absolute
        tc.encoder.reversed
    }

    def "configures only encoder reversed"() {
        when:
        def tc = tcb.encoderReversed(true).build()

        then:
        tc.encoder.device == QuadEncoder
        tc.encoder.reversed
    }

    def "configure coast in neutral"() {
        when:
        def tc = tcb.brakeInNeutral(false).build()

        then:
        tc.brakeInNeutral == NeutralMode.Coast
    }

    def "configure brake in neutral"() {
        when:
        def tc = tcb.brakeInNeutral(true).build()

        then:
        tc.brakeInNeutral == NeutralMode.Brake
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
        def tc = tcb.forwardSoftLimit(2767).build()

        then:
        tc.forwardSoftLimit.enabled
        tc.forwardSoftLimit.position == 2767
    }

    def "configure reverse soft limit"() {
        when:
        def tc = tcb.reverseSoftLimit(2767).build()

        then:
        tc.reverseSoftLimit.enabled
        tc.reverseSoftLimit.position == 2767
    }

    def "configure current limit"() {
        when:
        def tc = tcb.currentLimit(2767).build()

        then:
        tc.continuousCurrentLimit == 2767
    }

    def "configure voltage ramp rate"() {
        when:
        def tc = tcb.voltageRampRate(27.67d).build()

        then:
        tc.openLoopRampTime == 27.67d
    }

    // PIDTalonConfig
    def "configure max output voltage"() {
        when:
        PIDTalonConfiguration tc = (PIDTalonConfiguration) tcb.mode(Velocity)
                .voltageCompSaturation(27.67d).build()

        then:
        tc.voltageCompSaturation == 27.67d
    }

    def "configure closed loop ramp rate limit"() {
        when:
        PIDTalonConfiguration tc = (PIDTalonConfiguration) tcb.mode(Velocity)
                .closedLoopRampRate(27.67d).build()

        then:
        tc.closedLoopRampRate == 27.67d
    }

    def "configure peak output voltage limit"() {
        when:
        PIDTalonConfiguration tc = (PIDTalonConfiguration) tcb.mode(Velocity)
                .outputVoltagePeak(2.7d, 6.7d).build()

        then:
        tc.forwardOutputVoltagePeak == 2.7d
        tc.reverseOutputVoltagePeak == 6.7d
    }

    def "configure nominal output voltage limit"() {
        when:
        PIDTalonConfiguration tc = (PIDTalonConfiguration) tcb.mode(Velocity)
                .outputVoltageNominal(2.7d, 6.7d).build()

        then:
        tc.forwardOutputVoltageNominal == 2.7d
        tc.reverseOutputVoltageNominal == 6.7d
    }

    def "configure allowable closed loop error"() {
        when:
        PIDTalonConfiguration tc = (PIDTalonConfiguration) tcb.mode(Velocity)
                .allowableClosedLoopError(2767).build()

        then:
        tc.allowableClosedLoopError == 2767
    }

    def "configure nominal closed-loop voltage"() {
        when:
        PIDTalonConfiguration tc = (PIDTalonConfiguration) tcb.mode(Velocity)
                .nominalClosedLoopVoltage(27.67d).build()

        then:
        tc.nominalClosedLoopVoltage == 27.67d
    }

    def "configure P"() {
        when:
        PIDTalonConfiguration tc = (PIDTalonConfiguration) tcb.mode(Velocity).P(27.67d).build()

        then:
        tc.PGain == 27.67
    }

    def "configure I"() {
        when:
        PIDTalonConfiguration tc = (PIDTalonConfiguration) tcb.mode(Velocity).I(27.67d).build()

        then:
        tc.IGain == 27.67
    }

    def "configure D"() {
        when:
        PIDTalonConfiguration tc = (PIDTalonConfiguration) tcb.mode(Velocity).D(27.67d).build()

        then:
        tc.DGain == 27.67
    }

    def "configure F"() {
        when:
        PIDTalonConfiguration tc = (PIDTalonConfiguration) tcb.mode(Velocity).F(27.67d).build()

        then:
        tc.FGain == 27.67
    }

    def "configure I-zone"() {
        when:
        PIDTalonConfiguration tc = (PIDTalonConfiguration) tcb.mode(Velocity).iZone(2767).build()

        then:
        tc.IZone == 2767
    }

    def "configure profile slot"() {
        when:
        PIDTalonConfiguration tc = (PIDTalonConfiguration) tcb.mode(Velocity).profileSlot(3).build()

        then:
        tc.profileSlot == 3
    }

    def "configure motionMagicAcceleration"() {
        when:
        MotionMagicTalonConfiguration tc = (MotionMagicTalonConfiguration) tcb.mode(MotionMagic)
                .motionMagicAcceleration(27).build()

        then:
        tc.motionMagicAcceleration == 27
    }

    def "configure motionMagicCruiseVelocity"() {
        when:
        MotionMagicTalonConfiguration tc = (MotionMagicTalonConfiguration) tcb.mode(MotionMagic)
                .motionMagicCruiseVelocity(67).build()

        then:
        tc.motionMagicCruiseVelocity == 67
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
        "mode = \"Bogus\"\nsetpointMax = 0.0" | 'No enum constant com.ctre.phoenix.motorcontrol.ControlMode.Bogus'
    }

    // https://github.com/strykeforce/thirdcoast/issues/19
    def "reads voltageRampRate from VoltageTalonConfiguration"() {
        def input = '''
name = "foo"
mode = "PercentOutput"
setpointMax = 12.0
openLoopRampTime = 4.0
'''
        when:
        def vtc = new TalonConfigurationBuilder(TalonConfigurationBuilder.create(new Toml().read(input))).build()

        then:
        vtc instanceof VoltageTalonConfiguration
        vtc.name == 'foo'
        vtc.setpointMax == 12.0d
        vtc.openLoopRampTime == 4.0
    }

    def "reads motion magic params from MotionMagicTalonConfiguration"() {
        def input = '''
name = "foo"
mode = "MotionMagic"
setpointMax = 0.0
motionMagicAcceleration = 27
motionMagicCruiseVelocity = 67
'''
        when:
        def mmtc = (MotionMagicTalonConfiguration) new TalonConfigurationBuilder(TalonConfigurationBuilder
                .create(new Toml().read(input))).build()

        then:
        mmtc instanceof MotionMagicTalonConfiguration
        mmtc.name == 'foo'
        mmtc.setpointMax == 0.0d
        mmtc.motionMagicAcceleration == 27
        mmtc.motionMagicCruiseVelocity == 67
    }

}

