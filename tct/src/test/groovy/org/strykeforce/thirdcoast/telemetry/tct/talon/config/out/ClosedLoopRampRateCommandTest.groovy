package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out

import org.strykeforce.thirdcoast.telemetry.tct.Command
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractDoubleConfigCommand
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommandTest
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.ClosedLoopRampRateCommand
import spock.lang.Ignore

class ClosedLoopRampRateCommandTest extends AbstractTalonConfigCommandTest {

    Command command

    @Override
    void setup() {
        command = new ClosedLoopRampRateCommand(reader, talonSet)
    }


    def "nothing input"() {
        given:
        reader.readLine(_) >> ""

        when:
        command.perform()

        then:
        0 * talon._
    }

    def "handles input"() {
        when:
        command.perform()

        then:
        2 * reader.readLine(_) >>> ["ABC", "27.67"]

        1 * writer.println("please enter a number") // ABC

        1 * talon.configClosedloopRamp(27.67, AbstractDoubleConfigCommand.TIMEOUT_MS) // 27.67
        1 * talon.getDescription()
        0 * talon._
    }
}
