package org.strykeforce.thirdcoast.telemetry

import com.ctre.CANTalon
import spock.lang.Specification

class TelemetryTest extends Specification {

    def "registers mock CANTalons"() {

        given: "a Telemetry service"
        Telemetry telemetry = new Telemetry()

        when: "a talon is registered"
        telemetry.register(Mock(CANTalon))

        then: "a single talon is in the set of talons"
        telemetry.talons.size() == 1

        when: "two more talons are registered"
        telemetry.registerAll(Arrays.asList(Mock(CANTalon), Mock(CANTalon)))

        then: "set of talons has 3 talons"
        telemetry.talons.size() == 3
    }
}
