package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc

import org.strykeforce.thirdcoast.telemetry.tct.Command
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommandTest
import spock.lang.Ignore

class SetPositionCommandTest extends AbstractTalonConfigCommandTest {

    Command command

    @Override
    void setup() {
        command = new SetPositionCommand(reader, talonSet)
    }


    def "nothing input"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> ""
        0 * talon._
    }


    def "handles input"() {
        when:
        command.perform()

        then:
        2 * reader.readLine(_) >>> ["ABC", "27"]

        1 * writer.println("please enter an integer") // ABC

        1 * talon.setSelectedSensorPosition(27, 0, AbstractDoubleConfigCommand.TIMEOUT_MS) // 27
        1 * talon.getDescription()
        0 * talon._
    }
}
