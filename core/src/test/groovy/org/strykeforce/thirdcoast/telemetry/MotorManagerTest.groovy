package org.strykeforce.thirdcoast.telemetry

import com.ctre.CANTalon
import groovy.json.JsonSlurper
import okio.Buffer
import okio.BufferedSink
import spock.lang.Specification

class MotorManagerTest extends Specification {

    CANTalon createStub(int id, String description) {
        CANTalon stub = Stub(CANTalon)
        stub.getDeviceID() >> id
        stub.getDescription() >> description
        stub.getControlMode() >> CANTalon.TalonControlMode.Disabled;
        return stub
    }

    def "toJson"() {
        given:
        MotorManager mm = new MotorManager()
        mm.register(createStub(1, "test1"))
        mm.register(createStub(2, "test2"))
        Buffer buffer = new Buffer();
        JsonSlurper slurper = new JsonSlurper()

        when:
        mm.toJson(buffer)
        def result = slurper.parseText(buffer.readUtf8())

        then:
        result[0].id == 1
        result[0].description == "test1"
        result[1].id == 2
        result[1].description == "test2"
    }
}
