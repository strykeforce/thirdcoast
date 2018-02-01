package org.strykeforce.thirdcoast.talon.config

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.moandjiezana.toml.Toml
import spock.lang.Specification

class SoftLimitsTest extends Specification {

    final static Random random = new Random()

    def "configures default values"() {
        given:
        def limits = SoftLimits.DEFAULT
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        limits.configure(talon, timeout)

        then:
        1 * talon.configForwardSoftLimitThreshold(0, timeout)
        1 * talon.configForwardSoftLimitEnable(false, timeout)
        1 * talon.configReverseSoftLimitThreshold(0, timeout)
        1 * talon.configReverseSoftLimitEnable(false, timeout)
        0 * talon._
    }

    def "configures forward soft limit"() {
        given:
        def state = new SoftLimits.State(2767, true)
        def limits = new SoftLimits(state, SoftLimits.State.DEFAULT)
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        limits.configure(talon, timeout)

        then:
        1 * talon.configForwardSoftLimitThreshold(2767, timeout)
        1 * talon.configForwardSoftLimitEnable(true, timeout)
        1 * talon.configReverseSoftLimitThreshold(0, timeout)
        1 * talon.configReverseSoftLimitEnable(false, timeout)
        0 * talon._

    }

    def "configures reverse soft limit"() {
        given:
        def state = new SoftLimits.State(2767, true)
        def limits = new SoftLimits(SoftLimits.State.DEFAULT, state)
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        limits.configure(talon, timeout)

        then:
        1 * talon.configForwardSoftLimitThreshold(0, timeout)
        1 * talon.configForwardSoftLimitEnable(false, timeout)
        1 * talon.configReverseSoftLimitThreshold(2767, timeout)
        1 * talon.configReverseSoftLimitEnable(true, timeout)
        0 * talon._
    }

    def "overrides default with TOML"() {
        given:
        def tomlStr = "[forward]\nlimit = 2767"
        def toml = new Toml().read(tomlStr)
        def expected = new SoftLimits(
                new SoftLimits.State(2767, false), SoftLimits.State.DEFAULT)

        expect:
        SoftLimits.create(toml) == expected
    }

    def "default values for null TOML"() {
        expect:
        SoftLimits.create(null) == SoftLimits.DEFAULT
    }

}
