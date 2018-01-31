package org.strykeforce.thirdcoast.talon.control

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import spock.lang.Specification

class FeedbackSensorTest extends Specification {

    final static Random random = new Random()

    def "configures default values"() {
        given:
        def feedback = FeedbackSensor.DEFAULT
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        feedback.configure(talon, timeout)

        then:
        1 * talon.configSelectedFeedbackSensor(FeedbackDevice.None, 0, timeout)
        0 * talon._
    }

    def "configures encoder"() {
        given:
        def feedback = new FeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 1)
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        feedback.configure(talon, timeout)

        then:
        1 * talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 1, timeout)
        0 * talon._

    }

}
