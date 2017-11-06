package org.strykeforce.thirdcoast.talon

import com.ctre.CANTalon
import spock.lang.Specification

class EncoderTest extends Specification {

    def "has a default configuration"() {
        given:
        def talon = Mock(CANTalon)

        when:
        Encoder.DEFAULT.configure(talon)

        then:
        1 * talon.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder)
        1 * talon.reverseSensor(false)
        1 * talon.isSensorPresent(CANTalon.FeedbackDevice.QuadEncoder)
        0 * talon._
    }

    def "given a non-default configuration"() {
        given:
        def talon = Mock(CANTalon)
        def encoder = new Encoder(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute.name(),
                true, 2767)

        when:
        encoder.configure(talon)

        then:
        1 * talon.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute)
        1 * talon.reverseSensor(true)
        1 * talon.configEncoderCodesPerRev(2767)
        1 * talon.isSensorPresent(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute)

    }

}
