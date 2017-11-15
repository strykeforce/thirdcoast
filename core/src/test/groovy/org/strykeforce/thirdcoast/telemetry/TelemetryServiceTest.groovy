package org.strykeforce.thirdcoast.telemetry

import com.ctre.CANTalon
import edu.wpi.first.wpilibj.DigitalOutput
import edu.wpi.first.wpilibj.Servo
import org.strykeforce.thirdcoast.talon.StatusFrameRate
import org.strykeforce.thirdcoast.telemetry.item.DigitalOutputItem
import org.strykeforce.thirdcoast.telemetry.item.ServoItem
import org.strykeforce.thirdcoast.telemetry.item.TalonItem
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

    def "prevent multiple copies and preserve insertion order"() {
        given:
        def talon1 = Stub(CANTalon)
        talon1.getDeviceID() >> 1
        def talon2 = Stub(CANTalon)
        talon2.getDeviceID() >> 2
        def talon3 = Stub(CANTalon)
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
        inv.itemForId(0).id() == 2
        inv.itemForId(1).id() == 1
        inv.itemForId(2).id() == 3

        when:
        inv.itemForId(3).id() == 2

        then:
        thrown(IndexOutOfBoundsException)
    }

}
