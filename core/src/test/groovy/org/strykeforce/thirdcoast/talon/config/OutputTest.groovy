package org.strykeforce.thirdcoast.talon.config

import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.moandjiezana.toml.Toml
import spock.lang.Specification

class OutputTest extends Specification {

    final static Random random = new Random()

    def "configures default values"() {
        given:
        def output = Output.DEFAULT
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        output.configure(talon, timeout)

        then:
        1 * talon.configNeutralDeadband(0.04d, timeout)
        1 * talon.setInverted(false)

        1 * talon.configNominalOutputForward(0d, timeout)
        1 * talon.configPeakOutputForward(1d, timeout)
        1 * talon.configNominalOutputReverse(0d, timeout)
        1 * talon.configPeakOutputReverse(1d, timeout)

        1 * talon.configOpenloopRamp(0d, timeout)
        1 * talon.configClosedloopRamp(0d, timeout)

        1 * talon.configVoltageCompSaturation(12d, timeout)
        1 * talon.enableVoltageCompensation(true)
        1 * talon.setNeutralMode(NeutralMode.Coast)

        0 * talon._
    }

    def "overrides default with TOML forward peak"() {
        given:
        def tomlStr = "[forward]\npeak = 27.0"
        def toml = new Toml().read(tomlStr)
        def expected = new Output(
                new Output.Limits(0d, 27d),
                Output.Limits.DEFAULT,
                Output.RampRates.DEFAULT,
                Output.VoltageCompensation.DEFAULT,
                0.04d,
                false, NeutralMode.Coast)

        expect:
        Output.create(toml) == expected
    }

    def "overrides default with TOML rampRates closedLoop"() {
        given:
        def tomlStr = "[rampRates]\nclosedLoop = 27.0"
        def toml = new Toml().read(tomlStr)
        def expected = new Output(
                Output.Limits.DEFAULT,
                Output.Limits.DEFAULT,
                new Output.RampRates(0d, 27d),
                Output.VoltageCompensation.DEFAULT,
                0.04d,
                false, NeutralMode.Coast)

        expect:
        Output.create(toml) == expected
    }

    def "overrides default with TOML voltageCompensation saturation"() {
        given:
        def tomlStr = "[voltageCompensation]\nsaturation = 27.0"
        def toml = new Toml().read(tomlStr)
        def expected = new Output(
                Output.Limits.DEFAULT,
                Output.Limits.DEFAULT,
                Output.RampRates.DEFAULT,
                new Output.VoltageCompensation(27d, true, 32),
                0.04d,
                false, NeutralMode.Coast)

        expect:
        Output.create(toml) == expected
    }

    def "default values for null TOML"() {
        expect:
        Output.create(null) == Output.DEFAULT
    }
}
