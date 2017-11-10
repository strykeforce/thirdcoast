package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim

import org.strykeforce.thirdcoast.telemetry.tct.Command
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommandTest

class ReverseSoftLimitCommandTest extends AbstractTalonConfigCommandTest {

    Command command

    @Override
    void setup() {
        command = new ReverseSoftLimitCommand(reader, talonSet)
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
        1 * reader.readLine(_) >>> ["27.67"]

        1 * talon.setReverseSoftLimit(27.67) // 27.67
        1 * talon.getDescription()
        0 * talon._
    }

}
