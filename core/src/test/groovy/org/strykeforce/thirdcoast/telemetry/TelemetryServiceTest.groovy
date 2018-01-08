package org.strykeforce.thirdcoast.telemetry

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.strykeforce.thirdcoast.talon.StatusFrameRate
import org.strykeforce.thirdcoast.telemetry.item.TalonItem
import spock.lang.Ignore
import spock.lang.Specification

@Ignore("2018")
class TelemetryServiceTest extends Specification {

    def "sets status frame rates for given Talon"() {
        given:
        def talon = Mock(TalonSRX)
        talon.getDescription() >>> ["talon1", "talon3", "talon5", "talon4"]
        def target = Mock(TalonSRX)
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
        0 * talon.setStatusFrameRateMs(_)
        0 * target.setStatusFrameRateMs(_)

        when:
        talon.getDeviceID() >>> [1, 3, 5, 4] // configureStatusFrameRates causes calls
        telemetry.configureStatusFrameRates(4, rates)

        then:
        1 * target.setStatusFrameRateMs(TalonSRX.StatusFrameRate.General, 2767)
        0 * talon.setStatusFrameRateMs(TalonSRX.StatusFrameRate.General, _)
    }

    def "prevent multiple copies and preserve insertion order"() {
        given:
        def talon1 = Stub(TalonSRX)
        talon1.getDeviceID() >> 1
        def talon2 = Stub(TalonSRX)
        talon2.getDeviceID() >> 2
        def talon3 = Stub(TalonSRX)
        talon3.getDeviceID() >> 3
        def telemetry = new TelemetryService()

        when:
        telemetry.register(talon2)
        telemetry.register(talon1)
        telemetry.register(talon3)
        telemetry.register(talon2)
        telemetry.register(talon2)

        and:
        def inv = new RobotInventory(telemetry.items)

        then:
        inv.itemForId(0) instanceof TalonItem
        inv.itemForId(0).deviceId() == 2
        inv.itemForId(1).deviceId() == 1
        inv.itemForId(2).deviceId() == 3

        when:
        inv.itemForId(3).deviceId() == 2

        then:
        thrown(IndexOutOfBoundsException)
    }

}
