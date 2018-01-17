package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import spock.lang.Specification

import static com.ctre.phoenix.motorcontrol.FeedbackDevice.*

class EncoderTest extends Specification {

    static TIMEOUT = 0

    def "has a default configuration"() {
        given:
        def talon = Mock(WPI_TalonSRX)

        when:
        Encoder.DEFAULT.configure(talon, TIMEOUT)

        then:
        1 * talon.configSelectedFeedbackSensor(QuadEncoder, 0, TIMEOUT)
        1 * talon.setSensorPhase(false)
        1 * talon.getDescription()
        0 * talon._
    }

    def "given a non-default configuration"() {
        given:
        def encoder = new Encoder(CTRE_MagEncoder_Absolute, true)
        def talon = Mock(WPI_TalonSRX)

        when:
        encoder.configure(talon, TIMEOUT)

        then:
        1 * talon.configSelectedFeedbackSensor(CTRE_MagEncoder_Absolute, 0, TIMEOUT)
        1 * talon.setSensorPhase(true)
    }

    def "creates instance from full TOML"() {
        def input = '''
device = "Analog"
reversed = false
unitScalingEnabled = false
ticksPerRevolution = 0
'''
        given:
        def toml = new Toml().read(input)
        when:
        def encoder = toml.to(Encoder.class)

        then:
        with(encoder) {
            device == Analog
            !reversed
        }
    }

    def "creates instance from partial TOML"() {
        def input = '''
device = "PulseWidthEncodedPosition"
'''
        given:
        def toml = new Toml().read(input)
        when:
        def encoder = toml.to(Encoder.class)

        then:
        with(encoder) {
            device == PulseWidthEncodedPosition
            !reversed
        }
    }

    def "creates instance from no TOML"() {
        given:
        def toml = new Toml().read('')
        when:
        def encoder = toml.to(Encoder.class)

        then:
        with(encoder) {
            device == QuadEncoder
            !reversed
        }
    }

    def "serializes into TOML"() {
        given:
        def encoder = new Encoder(PulseWidthEncodedPosition, true)
        def writer = new TomlWriter()

        when:
        def output = writer.write(encoder)

        then:
        output == '''device = "PulseWidthEncodedPosition"
reversed = true
'''
    }

    def "creates a copy with reversed set"() {
        given:
        def encoder = new Encoder(CTRE_MagEncoder_Absolute)

        expect:
        !encoder.reversed
        encoder.device == CTRE_MagEncoder_Absolute

        when:
        encoder = encoder.copyWithReversed(true)

        then:
        encoder.reversed
        encoder.device == CTRE_MagEncoder_Absolute
    }

    def "creates a copy with new device set"() {
        given:
        def encoder = new Encoder()

        expect:
        !encoder.reversed
        encoder.device == QuadEncoder

        when:
        encoder = encoder.copyWithEncoder(CTRE_MagEncoder_Relative)

        then:
        !encoder.reversed
        encoder.device == CTRE_MagEncoder_Relative
    }

    def "creates a default with reverse set"() {
        when:
        def encoder = new Encoder(true)

        then:
        encoder.reversed
        encoder.device == QuadEncoder
    }
}
