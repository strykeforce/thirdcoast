package org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl

import org.strykeforce.thirdcoast.telemetry.tct.Command
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommandTest
import spock.lang.Ignore

class PCommandTest extends AbstractTalonConfigCommandTest {

    Command command

    @Override
    void setup() {
        command = new PCommand(reader, talonSet)
    }


    def "nothing input"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> ""
        0 * talon._
    }

    @Ignore
    def "handles input"() {
        when:
        command.perform()

        then:
        2 * reader.readLine(_) >>> ["ABC", "27.67"]

        1 * writer.println("please enter a number") // ABC

        1 * talon.setP(27.67) // 27.67
        1 * talon.getDescription()
        0 * talon._
    }
}
