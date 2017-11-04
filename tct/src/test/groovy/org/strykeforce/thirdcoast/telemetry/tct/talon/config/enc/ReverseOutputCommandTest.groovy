package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc

import org.jline.terminal.Terminal
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.BooleanConfigCommandTest

class ReverseOutputCommandTest extends BooleanConfigCommandTest {

    def command = new ReverseOutputCommand(reader, talonSet)

    def "nothing selected"() {
        given:
        reader.readLine(_) >> ""

        when:
        command.perform()

        then:
        0 * talon._
    }


    def "yes selected"() {
        given:
        reader.readLine(_) >> "Y"

        when:
        command.perform()

        then:
        1 * talon.reverseOutput(true)
        1 * talon.getDescription()
        0 * talon._
    }

    def "no selected"() {
        given:
        reader.readLine(_) >> "N"

        when:
        command.perform()

        then:
        1 * talon.reverseOutput(false)
        1 * talon.getDescription()
        0 * talon._
    }
}
