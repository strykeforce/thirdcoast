package org.strykeforce.thirdcoast.talon.config

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal
import com.ctre.phoenix.motorcontrol.LimitSwitchSource
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.moandjiezana.toml.Toml
import spock.lang.Specification

import static com.ctre.phoenix.motorcontrol.LimitSwitchNormal.*
import static com.ctre.phoenix.motorcontrol.LimitSwitchSource.Deactivated
import static com.ctre.phoenix.motorcontrol.LimitSwitchSource.FeedbackConnector

class LimitSwitchesTest extends Specification {

    final static Random random = new Random()

    def "configures default values"() {
        given:
        def switches = LimitSwitches.DEFAULT
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        switches.configure(talon, timeout)

        then:
        1 * talon.configForwardLimitSwitchSource(Deactivated, Disabled, timeout)
        1 * talon.configReverseLimitSwitchSource(Deactivated, Disabled, timeout)
        1 * talon.overrideLimitSwitchesEnable(false)
        0 * talon._
    }

    def "configures normally-open forward limit switch"() {
        given:
        def state = new LimitSwitches.State(FeedbackConnector, NormallyOpen)
        def switches = new LimitSwitches(state, LimitSwitches.State.DEFAULT)
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        switches.configure(talon, timeout)

        then:
        1 * talon.configForwardLimitSwitchSource(FeedbackConnector, NormallyOpen, timeout)
        1 * talon.configReverseLimitSwitchSource(Deactivated, Disabled, timeout)
        1 * talon.overrideLimitSwitchesEnable(true)
        0 * talon._

    }

    def "configures normally-open reverse limit switch"() {
        given:
        def state = new LimitSwitches.State(FeedbackConnector, NormallyOpen)
        def switches = new LimitSwitches(LimitSwitches.State.DEFAULT, state)
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        switches.configure(talon, timeout)

        then:
        1 * talon.configForwardLimitSwitchSource(Deactivated, Disabled, timeout)
        1 * talon.configReverseLimitSwitchSource(FeedbackConnector, NormallyOpen, timeout)
        1 * talon.overrideLimitSwitchesEnable(true)
        0 * talon._
    }

    def "configures normally-closed limit switches"() {
        given:
        def state = new LimitSwitches.State(FeedbackConnector, NormallyClosed)
        def switches = new LimitSwitches(state, state)
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        switches.configure(talon, timeout)

        then:
        1 * talon.configForwardLimitSwitchSource(FeedbackConnector, NormallyClosed, timeout)
        1 * talon.configReverseLimitSwitchSource(FeedbackConnector, NormallyClosed, timeout)
        1 * talon.overrideLimitSwitchesEnable(true)
        0 * talon._
    }

    def "overrides default with TOML"() {
        given:
        def tomlStr = "[forward]\nsource = \"FeedbackConnector\"\nnormal = \"NormallyOpen\""
        def toml = new Toml().read(tomlStr)
        def expected = new LimitSwitches(
                new LimitSwitches.State(FeedbackConnector, NormallyOpen), LimitSwitches.State.DEFAULT)

        expect:
        LimitSwitches.create(toml) == expected
    }

    def "default values for null TOML"() {
        expect:
        LimitSwitches.create(null) == LimitSwitches.DEFAULT
    }
}
