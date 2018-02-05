package org.strykeforce.thirdcoast.talon.config

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.moandjiezana.toml.Toml
import spock.lang.Specification

class MotionMagicTest extends Specification {

    final static Random random = new Random()

    def "configures default values"() {
        given:
        def motion = MotionMagic.DEFAULT
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        motion.configure(talon, timeout)

        then:
        1 * talon.configMotionAcceleration(0, timeout)
        1 * talon.configMotionCruiseVelocity(0, timeout)
        0 * talon._
    }

    def "configures motion magic values"() {
        given:
        def motion = new MotionMagic(27_670, 2767)
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        motion.configure(talon, timeout)

        then:
        1 * talon.configMotionAcceleration(27_670, timeout)
        1 * talon.configMotionCruiseVelocity(2767, timeout)
        0 * talon._
    }

    def "overrides default with TOML"() {
        given:
        def tomlStr = "cruiseVelocity = 27\nacceleration=670"
        def toml = new Toml().read(tomlStr)
        def expected = new MotionMagic(670, 27)

        expect:
        MotionMagic.create(toml) == expected
    }

    def "default values for null TOML"() {
        expect:
        MotionMagic.create(null) == MotionMagic.DEFAULT
    }

}
