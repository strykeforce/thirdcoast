package org.strykeforce.thirdcoast.telemetry

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import groovy.json.JsonSlurper
import okio.Buffer
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import org.strykeforce.thirdcoast.telemetry.item.Item
import org.strykeforce.thirdcoast.telemetry.item.TalonItem
import spock.lang.Ignore
import spock.lang.Specification

import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.ABSOLUTE_ENCODER_POSITION
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.CLOSED_LOOP_TARGET
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.SELECTED_SENSOR_VELOCITY
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.SETPOINT

class InventoryTest extends Specification {

    TalonSRX talonStub(int id, String description) {
        def stub = Stub(WPI_TalonSRX)
        stub.getDeviceID() >> id
        stub.getDescription() >> description
        return stub
    }

    def "Creates Robot Inventory with Talons"() {
        given:
        def talons = new ArrayList<Item>()
        talons.add(new TalonItem(talonStub(51, "talon0"), "Framistator (51)"))
        talons.add(new TalonItem(talonStub(61, "talon1")))

        when:
        Inventory inventory = new RobotInventory(talons)

        then:
        inventory.itemForId(0).description() == "Framistator (51)"
        inventory.itemForId(0).deviceId() == 51
        inventory.itemForId(1).description() == "TalonSRX 61"
        inventory.itemForId(1).deviceId() == 61
    }

    def "Creates JSON representation"() {
        given:
        def talons = new ArrayList<Item>()
        talons.add(new TalonItem(talonStub(51, "talon10")))
        talons.add(new TalonItem(talonStub(61, "talon11")))
        Buffer buffer = new Buffer()
        JsonSlurper slurper = new JsonSlurper()

        when:
        Inventory inventory = new RobotInventory(talons)
        inventory.writeInventory(buffer)
        def result = slurper.parse(buffer.readByteArray())

        then:
        with(result) {
            items.size == 2
            items[0].id == 0
            items[0].description == "TalonSRX 51"
            measures.size == 1
            with(measures[0]) {
                deviceType == "talon"
                deviceMeasures.size == TalonItem.MEASURES.size()
                Measure.valueOf(deviceMeasures[0].id) == CLOSED_LOOP_TARGET
                deviceMeasures[0].description == CLOSED_LOOP_TARGET.description
                Measure.valueOf(deviceMeasures[5].id) == SELECTED_SENSOR_VELOCITY
                deviceMeasures[5].description == SELECTED_SENSOR_VELOCITY.description
            }
            items[1].id == 1
            items[1].description == "TalonSRX 61"
        }
    }
}
