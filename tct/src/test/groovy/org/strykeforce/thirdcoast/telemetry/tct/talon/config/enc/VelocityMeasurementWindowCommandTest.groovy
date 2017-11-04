package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc

import org.strykeforce.thirdcoast.telemetry.tct.Command
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommandTest

class VelocityMeasurementWindowCommandTest extends AbstractTalonConfigCommandTest {

    Command command

    @Override
    void setup() {
        command = new VelocityMeasurementWindowCommand(reader, talonSet)
    }

    def "invalid input"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >>> ["", "-1", "0", "8"]
        0 * talon._
    }

    def "input window size 1"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >>> ["1"]

        1 * talon.SetVelocityMeasurementWindow(1)
        1 * talon.getDescription()
        0 * talon._
    }

    def "input window size 4"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >>> ["3"]

        1 * talon.SetVelocityMeasurementWindow(4)
        1 * talon.getDescription()
        0 * talon._
    }

    def "input window size 64"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >>> ["7"]

        1 * talon.SetVelocityMeasurementWindow(64)
        1 * talon.getDescription()
        0 * talon._
    }

}
