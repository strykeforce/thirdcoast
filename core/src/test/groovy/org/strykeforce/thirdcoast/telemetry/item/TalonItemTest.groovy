package org.strykeforce.thirdcoast.telemetry.item

import com.ctre.CANTalon
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import spock.lang.Specification

class TalonItemTest extends Specification {
    // TODO: add rest of measurements
    def "MeasurementFor"() {
        given:
        def talon = Stub(CANTalon)
        talon.getSetpoint() >> 0.2767
        talon.getOutputCurrent() >> 2.767
        talon.getOutputVoltage() >> 27.67
        Item item = new TalonItem(talon)

        when:
        def d = item.measurementFor(Measure.SETPOINT)

        then:
        d.getAsDouble() == 0.2767

        when:
        d = item.measurementFor(Measure.OUTPUT_CURRENT)

        then:
        d.getAsDouble() == 2.767

        when:
        d = item.measurementFor(Measure.OUTPUT_VOLTAGE)

        then:
        d.getAsDouble() == 27.67
    }

}
