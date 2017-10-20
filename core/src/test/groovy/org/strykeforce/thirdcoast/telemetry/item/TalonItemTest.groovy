package org.strykeforce.thirdcoast.telemetry.item

import com.ctre.CANTalon
import org.strykeforce.thirdcoast.talon.StatusFrameRate
import org.strykeforce.thirdcoast.telemetry.TelemetryService
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import spock.lang.Specification

class TalonItemTest extends Specification {
    // TODO: add rest of measurements
    def "MeasurementFor"() {
        given:
        def talon = Stub(CANTalon)
        talon.get() >> 0.2767
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

    def "sets status frame rates for given Talon"() {
        given:
        def talon = Mock(CANTalon)
        talon.getDeviceID() >>> [1, 3, 5]
        def target = Mock(CANTalon)
        target.getDeviceID() >> 4
        def telemetry = new TelemetryService()
        def rates = StatusFrameRate.builder().general(2767).build()

        when:
        telemetry.register(talon)
        telemetry.register(talon)
        telemetry.register(target)
        telemetry.register(talon)

        then:
        3 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.General, 10)
        1 * target.setStatusFrameRateMs(CANTalon.StatusFrameRate.General, 10)

        when:
        telemetry.configureStatusFrameRates(4, rates)

        then:
        1 * target.setStatusFrameRateMs(CANTalon.StatusFrameRate.General, 2767)
        0 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.General, _)


    }
}
