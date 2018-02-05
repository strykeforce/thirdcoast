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
        List<Configurable> configurables = [feedback, switches, limits, currents, velocity, output, motion]
        def profiles = [Mock(TalonConfiguration.ClosedLoopProfile), Mock(TalonConfiguration.ClosedLoopProfile),
                        Mock(TalonConfiguration.ClosedLoopProfile), Mock(TalonConfiguration.ClosedLoopProfile)]
        def talonIds = Collections.<Integer> emptyList()
        def talonConfig = new TalonConfiguration("TEST", configurables, profiles, talonIds)
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

    def "creates default with golden default TOML"() {
        when:
        def toml = new Toml().read(this.getClass().getResourceAsStream("/testdata/talon/default.toml"))

        then:
        TalonConfiguration.create(toml) == TalonConfiguration.DEFAULT
    }

    def "overrides selected sensor with full default TOML"() {
        given:
        def feedback = new FeedbackSensor(FeedbackDevice.Analog, 1, false)
        def switches = LimitSwitches.DEFAULT
        def limits = SoftLimits.DEFAULT
        def currents = CurrentLimits.DEFAULT
        def velocity = VelocityMeasurement.DEFAULT
        def output = Output.DEFAULT
        def motion = MotionMagic.DEFAULT
        List<Configurable> configurables = [feedback, switches, limits, currents, velocity, output, motion]
        def clp = TalonConfiguration.ClosedLoopProfile.DEFAULT
        def profiles = [clp, clp, clp, clp]
        def talonIds = Collections.<Integer> emptyList()
        def talonConfig = new TalonConfiguration("ANALOG", configurables, profiles, talonIds)

        when:
        def toml = new Toml().read(this.getClass().getResourceAsStream("/testdata/talon/analog.toml"))

        then:
        TalonConfiguration.create(toml) == talonConfig
    }

    def "overrides default with name in TOML"() {
        given:
        def tomlStr = "name = \"STRYKE_FORCE\""
        def toml = new Toml().read(tomlStr)
        def clp = TalonConfiguration.ClosedLoopProfile.DEFAULT
        def feedback = FeedbackSensor.DEFAULT
        def switches = LimitSwitches.DEFAULT
        def limits = SoftLimits.DEFAULT
        def currents = CurrentLimits.DEFAULT
        def velocity = VelocityMeasurement.DEFAULT
        def output = Output.DEFAULT
        def motion = MotionMagic.DEFAULT
        List<Configurable> configurables = [feedback, switches, limits, currents, velocity, output, motion]

        when:
        def talonConfig = TalonConfiguration.create(toml)

        then:
        with(talonConfig) {
            name == "STRYKE_FORCE"
            closedLoopProfiles == [clp, clp, clp, clp]
            talonIds.empty
        }
        talonConfig.getConfigurables().equals(configurables)
    }

    def "overrides default with output inverted in TOML"() {
        given:
        def tomlStr = "[output]\ninverted=true"
        def toml = new Toml().read(tomlStr)
        def feedback = FeedbackSensor.DEFAULT
        def switches = LimitSwitches.DEFAULT
        def limits = SoftLimits.DEFAULT
        def currents = CurrentLimits.DEFAULT
        def velocity = VelocityMeasurement.DEFAULT
        def output = new Output(Output.Limits.FORWARD_DEFAULT,
                Output.Limits.REVERSE_DEFAULT, Output.RampRates.DEFAULT,
                Output.VoltageCompensation.DEFAULT,
                0.04d, true, NeutralMode.Coast)
        def motion = MotionMagic.DEFAULT
        List<Configurable> expected = [feedback, switches, limits, currents, velocity, output, motion]

        when:
        def talonConfig = TalonConfiguration.create(toml)

        then:
        with(talonConfig) {
            name == "DEFAULT"
            configurables == expected
            closedLoopProfiles == TalonConfiguration.DEFAULT.closedLoopProfiles
            talonIds.empty
        }
    }

    def "overrides default with output forward peak in TOML"() {
        given:
        def tomlStr = "[output.forward]\npeak = 0.5"
        def toml = new Toml().read(tomlStr)
        def forward = new Output.Limits(0d, 0.5d)
        def feedback = FeedbackSensor.DEFAULT
        def switches = LimitSwitches.DEFAULT
        def limits = SoftLimits.DEFAULT
        def currents = CurrentLimits.DEFAULT
        def velocity = VelocityMeasurement.DEFAULT
        def output = new Output(forward,
                Output.Limits.REVERSE_DEFAULT, Output.RampRates.DEFAULT,
                Output.VoltageCompensation.DEFAULT,
                0.04d, false, NeutralMode.Coast)
        def motion = MotionMagic.DEFAULT
        List<Configurable> expected = [feedback, switches, limits, currents, velocity, output, motion]


        when:
        def talonConfig = TalonConfiguration.create(toml)

        then:
        with(talonConfig) {
            name == "DEFAULT"
            configurables == expected
            closedLoopProfiles == TalonConfiguration.DEFAULT.closedLoopProfiles
            talonIds.empty
        }
    }
}
