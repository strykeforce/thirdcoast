package org.strykeforce.thirdcoast.telemetry

import com.ctre.CANTalon
import groovy.json.JsonSlurper
import okio.Buffer
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import org.strykeforce.thirdcoast.telemetry.item.Item
import org.strykeforce.thirdcoast.telemetry.item.TalonItem
import spock.lang.Specification

import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.ABSOLUTE_ENCODER_POSITION
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.SETPOINT

class InventoryTest extends Specification {

    CANTalon talonStub(int id, String description) {
        def stub = Stub(CANTalon)
        stub.getDeviceID() >> id
        stub.getDescription() >> description
        return stub
    }

    def "Creates Robot Inventory with Talons"() {
        given:
        def talons = new ArrayList<Item>()
        talons.add(new TalonItem(talonStub(51, "talon0")))
        talons.add(new TalonItem(talonStub(61, "talon1")))

        when:
        Inventory inventory = new RobotInventory(talons)

        then:
        inventory.itemForId(0).description() == "talon0"
        inventory.itemForId(0).id() == 51
        inventory.itemForId(1).description() == "talon1"
        inventory.itemForId(1).id() == 61
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
            items[0].description == "talon10"
            measures.size == 1
            with(measures[0]) {
                deviceType == "talon"
                deviceMeasures.size == TalonItem.MEASURES.size()
                Measure.valueOf(deviceMeasures[0].id) == SETPOINT
                deviceMeasures[0].description == SETPOINT.description
                Measure.valueOf(deviceMeasures[5].id) == ABSOLUTE_ENCODER_POSITION
                deviceMeasures[5].description == ABSOLUTE_ENCODER_POSITION.description
            }
            items[1].id == 1
            items[1].description == "talon11"
        }
    }
}
