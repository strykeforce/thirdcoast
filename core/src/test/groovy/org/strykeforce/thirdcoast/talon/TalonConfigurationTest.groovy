package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import org.strykeforce.thirdcoast.talon.config.*
import spock.lang.Specification

class TalonConfigurationTest extends Specification {

    final static Random random = new Random()

    def "configures values"() {
        given:
        def feedback = Mock(FeedbackSensor)
        def switches = Mock(LimitSwitches)
        def limits = Mock(SoftLimits)
        def currents = Mock(CurrentLimits)
        def velocity = Mock(VelocityMeasurement)
        def output = Mock(Output)
        def motion = Mock(MotionMagic)
        def profiles = [Mock(TalonConfiguration.ClosedLoopProfile), Mock(TalonConfiguration.ClosedLoopProfile),
                        Mock(TalonConfiguration.ClosedLoopProfile), Mock(TalonConfiguration.ClosedLoopProfile)]
        def talonConfig = new TalonConfiguration("TEST", feedback, switches, limits,
                currents, velocity, output, motion, profiles, Collections.<Integer>emptyList())
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        talonConfig.configure(talon, timeout)

        then:
        1 * feedback.configure(talon, timeout)
        1 * switches.configure(talon, timeout)
        1 * limits.configure(talon, timeout)
        1 * currents.configure(talon, timeout)
        1 * velocity.configure(talon, timeout)
        1 * output.configure(talon, timeout)
        1 * motion.configure(talon, timeout)
        for (int i = 0; i < TalonConfiguration.PROFILE_COUNT; i++) {
            1 * profiles[i].configure(talon, i, timeout)
        }
        1 * talon.getDeviceID()
        0 * talon._
    }

    def "creates default with empty TOML"() {
        expect:
        TalonConfiguration.create(new Toml()) == TalonConfiguration.DEFAULT
    }

    def "overrides default with full TOML"() {
        given:
        def feedback = new FeedbackSensor(FeedbackDevice.Analog, 1, false)
        def switches = LimitSwitches.DEFAULT
        def limits = SoftLimits.DEFAULT
        def currents = CurrentLimits.DEFAULT
        def velocity = VelocityMeasurement.DEFAULT
        def output = Output.DEFAULT
        def motion = MotionMagic.DEFAULT
        def clp = TalonConfiguration.ClosedLoopProfile.DEFAULT
        def profiles = [clp, clp, clp, clp]
        def talonConfig = new TalonConfiguration("TEST", feedback, switches, limits,
                currents, velocity, output, motion, profiles, Collections.<Integer>emptyList())

        when:
        def toml = new Toml().read(new TomlWriter().write(talonConfig))

        then:
        TalonConfiguration.create(toml) == talonConfig
    }

    def "overrides default with name in TOML"() {
        given:
        def tomlStr = "name = \"STRYKE_FORCE\""
        def toml = new Toml().read(tomlStr)
        def clp = TalonConfiguration.ClosedLoopProfile.DEFAULT

        when:
        def talonConfig = TalonConfiguration.create(toml)

        then:
        with(talonConfig) {
            name == "STRYKE_FORCE"
            output == Output.DEFAULT
            currentLimit == CurrentLimits.DEFAULT
            limitSwitch == LimitSwitches.DEFAULT
            softLimit == SoftLimits.DEFAULT
            selectedFeedbackSensor == FeedbackSensor.DEFAULT
            velocityMeasurement == VelocityMeasurement.DEFAULT
            motionMagic == MotionMagic.DEFAULT
            closedLoopProfiles == [clp, clp, clp, clp]
            talonIds.empty
        }
    }

    def "overrides default with output in TOML"() {
        given:
        def tomlStr = "[output]\ninverted=true"
        def toml = new Toml().read(tomlStr)
        def expected = new Output(Output.Limits.DEFAULT,
                Output.Limits.DEFAULT, Output.RampRates.DEFAULT,
                Output.VoltageCompensation.DEFAULT,
                0.04d, true, NeutralMode.Coast)

        when:
        def talonConfig = TalonConfiguration.create(toml)

        then:
        with(talonConfig) {
            name == "DEFAULT"
            output == expected
            currentLimit == CurrentLimits.DEFAULT
            limitSwitch == LimitSwitches.DEFAULT
            softLimit == SoftLimits.DEFAULT
            selectedFeedbackSensor == FeedbackSensor.DEFAULT
            velocityMeasurement == VelocityMeasurement.DEFAULT
            closedLoopProfiles == TalonConfiguration.DEFAULT.closedLoopProfiles
            talonIds.empty
        }
    }
}
