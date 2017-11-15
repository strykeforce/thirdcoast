package org.strykeforce.thirdcoast.telemetry

import com.ctre.CANTalon
import org.strykeforce.thirdcoast.talon.StatusFrameRate
import spock.lang.Specification

class TelemetryServiceTest extends Specification {

    def "sets status frame rates for given Talon"() {
        given:
        def talon = Mock(CANTalon)
        talon.getDescription() >>> ["talon1", "talon3", "talon5", "talon4"]
        def target = Mock(CANTalon)
        target.getDeviceID() >> 4
        target.getDescription() >> "target4"
        def telemetry = new TelemetryService()
        def rates = StatusFrameRate.builder().general(2767).build()

        when:
        talon.getDeviceID() >>> [1, 3, 5, 4]
        telemetry.register(talon)  // 1
        telemetry.register(talon)  // 3
        telemetry.register(target) // 4
        telemetry.register(talon)  // 5
        telemetry.register(talon)  // 4

        then:
        2 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.General, 10)
        1 * target.setStatusFrameRateMs(CANTalon.StatusFrameRate.General, 10)

        when:
        talon.getDeviceID() >>> [1, 3, 5, 4] // configureStatusFrameRates causes calls
        telemetry.configureStatusFrameRates(4, rates)

        then:
        1 * target.setStatusFrameRateMs(CANTalon.StatusFrameRate.General, 2767)
        0 * talon.setStatusFrameRateMs(CANTalon.StatusFrameRate.General, _)
    }

}
