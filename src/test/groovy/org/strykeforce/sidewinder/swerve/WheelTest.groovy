package org.strykeforce.sidewinder.swerve

import com.ctre.CANTalon
import org.strykeforce.sidewinder.talon.TalonParameters
import spock.lang.Specification

class WheelTest extends Specification {

    def azimuth = Mock(CANTalon)
    def drive = Mock(CANTalon)


    void setupSpec() {
        TalonParameters.register("testdata/talons.toml")
    }

    def "configures azimuth and drive talons"() {
        setup:
        azimuth.getDescription() >> "Azimuth"
        drive.getDescription() >> "Drive"

        when:
        new Wheel(azimuth, drive)

        then:
        1 * azimuth.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        1 * azimuth.setPID(12.0, 0.0, 200.0)
        1 * drive.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder)
    }

    def "sets azimuth parameters"() {
        when:
        def wheel = new Wheel(azimuth, drive)

        then:
        1 * azimuth.changeControlMode(CANTalon.TalonControlMode.Position)
        1 * drive.changeControlMode(CANTalon.TalonControlMode.Voltage)

        when:
        wheel.azimuthParameters = "speed"

        then:
        1 * azimuth.changeControlMode(CANTalon.TalonControlMode.Speed)
        0 * azimuth.changeControlMode(_)
        0 * drive.changeControlMode(_)
    }

    def "sets drive parameters"() {
        when:
        def wheel = new Wheel(azimuth, drive)

        then:
        1 * drive.changeControlMode(CANTalon.TalonControlMode.Voltage)
        1 * azimuth.changeControlMode(CANTalon.TalonControlMode.Position)

        when:
        wheel.driveParameters = "speed"

        then:
        1 * drive.changeControlMode(CANTalon.TalonControlMode.Speed)
        0 * drive.changeControlMode(_)
        0 * azimuth.changeControlMode(_)
    }

    def "gets azimuth absolution position"() {
        setup:
        azimuth.getDescription() >> "Azimuth"
        drive.getDescription() >> "Drive"
        azimuth.getPulseWidthPosition() >> 0x1000

        when:
        def wheel = new Wheel(azimuth, drive)
        def zeroPosition = 2767

        then:
        wheel.azimuthAbsolutePosition == 0

        when:
        wheel.setAzimuthZero(zeroPosition)

        then:
        1 * azimuth.setPosition((double)-zeroPosition / 0xFFF)
    }

}
