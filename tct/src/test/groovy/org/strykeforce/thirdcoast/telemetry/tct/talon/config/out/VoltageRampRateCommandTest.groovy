package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out

import org.strykeforce.thirdcoast.telemetry.tct.Command
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommandTest

class VoltageRampRateCommandTest extends AbstractTalonConfigCommandTest {

    Command command

    @Override
    void setup() {
        command = new VoltageRampRateCommand(reader, talonSet)
    }

    def "invalid or no input"() {
        when:
        command.perform()

        then:
        4 * reader.readLine(_) >>> ["ABC", "ABC,ABC", ",", ""]
        0 * talon._
    }


    def "handles input"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "27.67"

        1 * talon.setVoltageRampRate(27.67)
        1 * talon.getDescription()
        0 * talon._
    }

}
