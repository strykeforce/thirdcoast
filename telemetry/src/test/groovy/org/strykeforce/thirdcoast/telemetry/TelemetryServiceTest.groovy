package org.strykeforce.thirdcoast.telemetry

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import org.strykeforce.thirdcoast.telemetry.item.TalonItem
import spock.lang.Specification

class TelemetryServiceTest extends Specification {

    def "prevent multiple copies and sort by type and deviceId"() {
        given:
        def talon1 = Stub(WPI_TalonSRX)
        talon1.getDeviceID() >> 1
        def talon2 = Stub(WPI_TalonSRX)
        talon2.getDeviceID() >> 2
        def talon3 = Stub(WPI_TalonSRX)
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
        inv.itemForId(0).deviceId() == 1
        inv.itemForId(1).deviceId() == 2
        inv.itemForId(2).deviceId() == 3

        when:
        inv.itemForId(3).deviceId() == 2

        then:
        thrown(IndexOutOfBoundsException)
    }

}
