package org.strykeforce.thirdcoast.telemetry.item

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX

import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import spock.lang.Specification

class TalonItemTest extends Specification {
    // TODO: add rest of measurements
    def "MeasurementFor"() {
        given:
        def talon = Stub(WPI_TalonSRX)
        talon.getOutputCurrent() >> 2.767d
        talon.getMotorOutputVoltage() >> 27.67d
        Item item = new TalonItem(talon)

        when:
        def d = item.measurementFor(Measure.OUTPUT_CURRENT)

        then:
        d.getAsDouble() == 2.767d

        when:
        d = item.measurementFor(Measure.OUTPUT_VOLTAGE)

        then:
        d.getAsDouble() == 27.67d
    }

}
