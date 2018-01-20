package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc

import org.strykeforce.thirdcoast.telemetry.tct.Command
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommandTest
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.*
import static org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommand.TIMEOUT_MS

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
        1 * talon.configSelectedFeedbackSensor(Analog, 0, TIMEOUT_MS)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select CTRE Magnetic Absolute"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "2"
        1 * talon.configSelectedFeedbackSensor(CTRE_MagEncoder_Absolute, 0, TIMEOUT_MS)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select CTRE Magnetic Relative"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "3"
        1 * talon.configSelectedFeedbackSensor(CTRE_MagEncoder_Relative, 0, TIMEOUT_MS)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select None"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "4"
        1 * talon.configSelectedFeedbackSensor(None, 0, TIMEOUT_MS)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select Pulse Width Encoded Position"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "5"
        1 * talon.configSelectedFeedbackSensor(PulseWidthEncodedPosition, 0, TIMEOUT_MS)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select Quadrature"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "6"
        1 * talon.configSelectedFeedbackSensor(QuadEncoder, 0, TIMEOUT_MS)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select RemoteSensor 0"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "7"
        1 * talon.configSelectedFeedbackSensor(RemoteSensor0, 0, TIMEOUT_MS)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select RemoteSensor 1"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "8"
        1 * talon.configSelectedFeedbackSensor(RemoteSensor1, 0, TIMEOUT_MS)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select Sensor Difference"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "9"
        1 * talon.configSelectedFeedbackSensor(SensorDifference, 0, TIMEOUT_MS)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select Sensor Sum"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "10"
        1 * talon.configSelectedFeedbackSensor(SensorSum, 0, TIMEOUT_MS)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select Software Emulated Sensor"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "11"
        1 * talon.configSelectedFeedbackSensor(SoftwareEmulatedSensor, 0, TIMEOUT_MS)
        1 * talon.getDescription()
        0 * talon._
    }

    def "select Tachometer"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "12"
        1 * talon.configSelectedFeedbackSensor(Tachometer, 0, TIMEOUT_MS)
        1 * talon.getDescription()
        0 * talon._
    }

    /*
        case 1: Analog;
        case 2: CTRE_MagEncoder_Absolute;
        case 3: CTRE_MagEncoder_Relative;
        case 4: None;
        case 5: PulseWidthEncodedPosition;
        case 6: QuadEncoder;
        case 7: RemoteSensor0;
        case 8: RemoteSensor1;
        case 9: SensorDifference;
        case 10: SensorSum;
        case 11: SoftwareEmulatedSensor;
        case 12: Tachometer;
     */

}
