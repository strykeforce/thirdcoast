package org.strykeforce.thirdcoast.telemetry.item

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.squareup.moshi.JsonWriter
import groovy.json.JsonSlurper
import okio.Buffer
import org.strykeforce.thirdcoast.talon.TalonControlMode
import spock.lang.Ignore
import spock.lang.Specification

class TalonJsonTest extends Specification {

    def random = new Random()

    TalonSRX talon

    void setup() {
        talon = Stub(TalonSRX)
        talon.getDeviceID() >> random.nextInt()
        talon.getDescription() >> "test talon"
        talon.getControlMode() >> TalonControlMode.Speed
        talon.getAnalogInPosition() >> random.nextInt()
        talon.getAnalogInRaw() >> random.nextInt()
        talon.getAnalogInVelocity() >> random.nextInt()
        talon.getBrakeEnableDuringNeutral() >> true
        talon.getBusVoltage() >> random.nextDouble() * 10
        talon.getCloseLoopRampRate() >> random.nextDouble() * 10
        talon.getClosedLoopError() >> random.nextInt()
        talon.getD() >> random.nextDouble()
        talon.getEncPosition() >> random.nextInt()
        talon.getEncVelocity() >> random.nextInt()
        talon.getError() >> random.nextDouble() * 10
        talon.getExpiration() >> random.nextDouble() * 10
        talon.getF() >> random.nextDouble()
        talon.getFaultForLim() >> random.nextInt()
        talon.getFaultForSoftLim() >> random.nextInt()
        talon.getFaultHardwareFailure() >> random.nextInt()
        talon.getFaultOverTemp() >> random.nextInt()
        talon.getFaultUnderVoltage() >> random.nextInt()
        talon.getFaultRevLim() >> random.nextInt()
        talon.getFaultRevSoftLim() >> random.nextInt()
        talon.GetFirmwareVersion() >> random.nextLong()
    }

    @Ignore("2018")
    def "ToJson"() {
        given:
        def talon = new TalonItem(this.talon)
        Buffer buffer = new Buffer()
        JsonWriter writer = JsonWriter.of(buffer)
        JsonSlurper slurper = new JsonSlurper()

        when:
        talon.toJson(writer)
//        println buffer.readUtf8()
        def result = slurper.parseText(buffer.readUtf8())

        then:
        with(result) {
            deviceId == this.talon.getDeviceID()
            description == this.talon.getDescription()
            controlMode == this.talon.getControlMode().toString()
            feedbackDevice == "not available in API"
            analogInput.position == this.talon.getAnalogInPosition()
            analogInput.raw == this.talon.getAnalogInRaw()
            analogInput.velocity == this.talon.getAnalogInVelocity()
            closedLoop.enabled == true
            closedLoop.p == this.talon.getP()
            closedLoop.i == this.talon.getI()
            closedLoop.d == this.talon.getD()
            closedLoop.f == this.talon.getF()
            closedLoop.errorDouble == this.talon.getError()
            encoder.position == this.talon.getEncPosition()
            encoder.velocity == this.talon.getEncVelocity()
            firmwareVersion == this.talon.GetFirmwareVersion()
        }
    }
}
