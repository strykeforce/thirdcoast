package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc

import com.ctre.CANTalon
import org.strykeforce.thirdcoast.telemetry.tct.Command
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.AbstractTalonConfigCommandTest

class VelocityMeasurementPeriodCommandTest extends AbstractTalonConfigCommandTest {

    Command command

    @Override
    void setup() {
        command = new VelocityMeasurementPeriodCommand(reader, talonSet)
    }

    def "invalid input"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >>> ["", "-1", "0", "9"]
        0 * talon._
    }


    def "input 1 ms"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "1"
        1 * talon.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_1Ms)
        1 * talon.getDescription()
        0 * talon._
    }

    def "input 2 ms"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "2"
        1 * talon.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_2Ms)
        1 * talon.getDescription()
        0 * talon._
    }

    def "input 5 ms"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "3"
        1 * talon.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_5Ms)
        1 * talon.getDescription()
        0 * talon._
    }

    def "input 10 ms"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "4"
        1 * talon.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_10Ms)
        1 * talon.getDescription()
        0 * talon._
    }

    def "input 20 ms"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "5"
        1 * talon.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_20Ms)
        1 * talon.getDescription()
        0 * talon._
    }

    def "input 25 ms"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "6"
        1 * talon.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_25Ms)
        1 * talon.getDescription()
        0 * talon._
    }

    def "input 50 ms"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "7"
        1 * talon.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_50Ms)
        1 * talon.getDescription()
        0 * talon._
    }

    def "input 100 ms"() {
        when:
        command.perform()

        then:
        1 * reader.readLine(_) >> "8"
        1 * talon.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_100Ms)
        1 * talon.getDescription()
        0 * talon._
    }
}
