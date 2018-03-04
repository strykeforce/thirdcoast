package org.strykeforce.thirdcoast.talon.config

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.moandjiezana.toml.Toml
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
        1 * talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, timeout)
        1 * talon.setSensorPhase(false)
        0 * talon._
    }

    def "configures encoder"() {
        given:
        def feedback = new FeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 1, true)
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        feedback.configure(talon, timeout)

        then:
        1 * talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 1, timeout)
        1 * talon.getDeviceID()
        1 * talon.setSensorPhase(true)
        0 * talon._

    }

    def "overrides default with TOML"() {
        given:
        def tomlStr = "feedbackDevice = \"QuadEncoder\""
        def toml = new Toml().read(tomlStr)
        def expected = new FeedbackSensor(FeedbackDevice.QuadEncoder, 0, false)

        when:
        def feedback = FeedbackSensor.create(toml)

        then:
        feedback == expected
    }

    def "default values for null TOML"() {
        expect:
        FeedbackSensor.create(null) == FeedbackSensor.DEFAULT
    }
}
