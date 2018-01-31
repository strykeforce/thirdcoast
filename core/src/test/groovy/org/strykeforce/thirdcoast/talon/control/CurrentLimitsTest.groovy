package org.strykeforce.thirdcoast.talon.control

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import spock.lang.Specification

class CurrentLimitsTest extends Specification {

    final static Random random = new Random()

    def "configures default values"() {
        given:
        def current = CurrentLimits.DEFAULT
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        current.configure(talon, timeout)

        then:
        1 * talon.configContinuousCurrentLimit(0, timeout)
        1 * talon.configPeakCurrentLimit(0, timeout)
        1 * talon.configPeakCurrentDuration(0, timeout)
        1 * talon.enableCurrentLimit(false)
        0 * talon._
    }

    def "configures continuous current limit"() {
        given:
        def current = new CurrentLimits(27, 0, 0)
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        current.configure(talon, timeout)

        then:
        1 * talon.configContinuousCurrentLimit(27, timeout)
        1 * talon.configPeakCurrentLimit(0, timeout)
        1 * talon.configPeakCurrentDuration(0, timeout)
        1 * talon.enableCurrentLimit(true)
        0 * talon._
    }

    def "configures peak current limit"() {
        given:
        def current = new CurrentLimits(0, 27, 67)
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        current.configure(talon, timeout)

        then:
        1 * talon.configContinuousCurrentLimit(0, timeout)
        1 * talon.configPeakCurrentLimit(27, timeout)
        1 * talon.configPeakCurrentDuration(67, timeout)
        1 * talon.enableCurrentLimit(true)
        0 * talon._
    }
}
