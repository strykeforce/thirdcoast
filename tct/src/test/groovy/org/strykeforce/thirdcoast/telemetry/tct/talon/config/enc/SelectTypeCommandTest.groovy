package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.strykeforce.thirdcoast.telemetry.tct.Command
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommandTest

class SelectTypeCommandTest extends AbstractTalonConfigCommandTest {

    Command command

    @Override
    void setup() {
        command = new SelectTypeCommand(reader, talonSet)
    }

    def "invalid input"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >>> ["", "-1", "0", "9"]
        0 * talon._
    }

    def "select Analog"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "1"
        1 * talon.setFeedbackDevice(TalonSRX.FeedbackDevice.AnalogEncoder)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select Analog Potentiometer"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "2"
        1 * talon.setFeedbackDevice(TalonSRX.FeedbackDevice.AnalogPot)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select CTRE Magnetic Absolute"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "3"
        1 * talon.setFeedbackDevice(TalonSRX.FeedbackDevice.CtreMagEncoder_Absolute)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select CTRE Magnetic Relative"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "4"
        1 * talon.setFeedbackDevice(TalonSRX.FeedbackDevice.CtreMagEncoder_Relative)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select Falling Edge"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "5"
        1 * talon.setFeedbackDevice(TalonSRX.FeedbackDevice.EncFalling)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select Rising Edge"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "6"
        1 * talon.setFeedbackDevice(TalonSRX.FeedbackDevice.EncRising)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select Pulse Width"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "7"
        1 * talon.setFeedbackDevice(TalonSRX.FeedbackDevice.PulseWidth)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select Quadrature"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "8"
        1 * talon.setFeedbackDevice(TalonSRX.FeedbackDevice.QuadEncoder)
        1 * talon.getDescription()
        0 * talon._
    }


}
