package org.strykeforce.thirdcoast.telemetry.grapher

import com.ctre.CANTalon
import groovy.json.JsonSlurper
import okio.Buffer
import spock.lang.Specification

import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.*

class InventoryTest extends Specification {

    CANTalon talonStub(int id, String description) {
        def stub = Stub(CANTalon)
        stub.getDeviceID() >> id
        stub.getDescription() >> description
        return stub
    }

    def "Creates Robot Inventory with Talons"() {
        given:
        def talons = new ArrayList<CANTalon>()
        talons.add(talonStub(0, "talon0"))
        talons.add(talonStub(1, "talon1"))

        when:
        Inventory inventory = RobotInventory.of(talons)

        then:
        inventory.itemForId(0).description() == "talon0"
        inventory.itemForId(1).description() == "talon1"
    }

    def "Creates JSON representation"() {
        given:
        def talons = new ArrayList<CANTalon>()
        talons.add(talonStub(10, "talon10"))
        talons.add(talonStub(11, "talon11"))
        Buffer buffer = new Buffer()
        JsonSlurper slurper = new JsonSlurper()

        when:
        Inventory inventory = RobotInventory.of(talons)
        inventory.writeInventory(buffer)
        def result = slurper.parse(buffer.readByteArray())

        then:
        with(result) {
            items.size == 2
            items[0].id == 10
            items[0].description == "talon10"
            with(measures) {
                talon.size == Item.Measure.values().length
                talon[0].id == SETPOINT.jsonId
                talon[0].description == SETPOINT.description
                talon[5].id == ABSOLUTE_ENCODER_POSITION.jsonId
                talon[5].description == ABSOLUTE_ENCODER_POSITION.description
            }
        }
    }
}
