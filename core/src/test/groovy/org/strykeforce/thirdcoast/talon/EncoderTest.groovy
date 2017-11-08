package org.strykeforce.thirdcoast.talon

import com.ctre.CANTalon
import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import spock.lang.Specification

import static com.ctre.CANTalon.FeedbackDevice.AnalogEncoder
import static com.ctre.CANTalon.FeedbackDevice.CtreMagEncoder_Absolute
import static com.ctre.CANTalon.FeedbackDevice.EncRising
import static com.ctre.CANTalon.FeedbackDevice.PulseWidth
import static com.ctre.CANTalon.FeedbackDevice.QuadEncoder

class EncoderTest extends Specification {

    def "has a default configuration"() {
        given:
        def talon = Mock(CANTalon)

        when:
        Encoder.DEFAULT.configure(talon)

        then:
        1 * talon.setFeedbackDevice(QuadEncoder)
        1 * talon.reverseSensor(false)
        1 * talon.isSensorPresent(QuadEncoder)
        0 * talon._
    }

    def "given a non-default configuration"() {
        given:
        def encoder = new Encoder(CtreMagEncoder_Absolute, true, 2767)
        def talon = Mock(CANTalon)

        when:
        encoder.configure(talon)

        then:
        1 * talon.setFeedbackDevice(CtreMagEncoder_Absolute)
        1 * talon.reverseSensor(true)
        1 * talon.configEncoderCodesPerRev(2767)
        1 * talon.isSensorPresent(CtreMagEncoder_Absolute)
    }

    def "creates instance from full TOML"() {
        def input = '''
device = "AnalogEncoder"
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
            device == AnalogEncoder
            !reversed
            !unitScalingEnabled
            ticksPerRevolution == 0
        }
    }

    def "creates instance from partial TOML"() {
        def input = '''
device = "EncRising"
'''
        given:
        def toml = new Toml().read(input)
        when:
        def encoder = toml.to(Encoder.class)

        then:
        with(encoder) {
            device == EncRising
            !reversed
            !unitScalingEnabled
            ticksPerRevolution == 0
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
            !unitScalingEnabled
            ticksPerRevolution == 0
        }
    }

    def "serializes into TOML"() {
        given:
        def encoder = new Encoder(PulseWidth,true,null)
        def writer = new TomlWriter()

        when:
        def output = writer.write(encoder)

        then:
        output == '''device = "PulseWidth"
reversed = true
unitScalingEnabled = false
ticksPerRevolution = 0
'''
    }
}
