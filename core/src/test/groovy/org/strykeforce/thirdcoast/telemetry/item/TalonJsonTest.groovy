package org.strykeforce.thirdcoast.telemetry.item

import com.ctre.CANTalon
import com.squareup.moshi.JsonWriter
import groovy.json.JsonSlurper
import okio.Buffer

import spock.lang.Specification

class TalonJsonTest extends Specification {

    def random = new Random()

    CANTalon talon

    void setup() {
        talon = Stub(CANTalon)
        talon.getDeviceID() >> random.nextInt()
        talon.getDescription() >> "test talon"
        talon.getControlMode() >> CANTalon.TalonControlMode.Disabled
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

    def "ToJson"() {
        given:
        TalonItem.Json talon = new TalonItem.Json(this.talon)
        Buffer buffer = new Buffer()
        JsonWriter writer = JsonWriter.of(buffer)
        JsonSlurper slurper = new JsonSlurper()

        when:
        talon.toJson(writer)
//        println buffer.readUtf8()
        def result = slurper.parseText(buffer.readUtf8())

        then:
        with(result) {
            id == this.talon.getDeviceID()
            description == this.talon.getDescription()
            controlMode == this.talon.getControlMode().toString()
            feedbackDevice == "unknown"
            analogInPosition == this.talon.getAnalogInPosition()
            analogInRaw == this.talon.getAnalogInRaw()
            analogInVelocity == this.talon.getAnalogInVelocity()
            d == this.talon.getD()
            encPosition == this.talon.getEncPosition()
            encVelocity == this.talon.getEncVelocity()
            error == this.talon.getError()
            expiration == this.talon.getExpiration()
            f == this.talon.getF()
            with(faults) {
                lim == this.talon.getFaultForLim()
                softLim == this.talon.getFaultForSoftLim()
                hardwareFailure == this.talon.getFaultHardwareFailure()
                overTemp == this.talon.getFaultOverTemp()
                underVoltage == this.talon.getFaultUnderVoltage()
                revLim == this.talon.getFaultRevLim()
                revSoftLim == this.talon.getFaultRevSoftLim()
            }
            firmwareVersion == this.talon.GetFirmwareVersion()
        }
    }
}
