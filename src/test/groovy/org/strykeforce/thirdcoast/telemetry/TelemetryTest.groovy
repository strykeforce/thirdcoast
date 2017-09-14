package org.strykeforce.thirdcoast.telemetry

import com.ctre.CANTalon
import spock.lang.Specification

class TelemetryTest extends Specification {

    def "registers mock CANTalons"() {
        when:
        def telemetry = Telemetry.getInstance()
        telemetry.register(Mock(CANTalon))

        then:
        telemetry.talons.size() == 1

        when:
        telemetry.registerAll(Arrays.asList(Mock(CANTalon), Mock(CANTalon)))

        then:
        telemetry.talons.size() == 3
    }
}
