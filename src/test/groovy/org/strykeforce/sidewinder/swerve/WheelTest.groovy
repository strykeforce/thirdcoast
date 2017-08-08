package org.strykeforce.sidewinder.swerve

import com.ctre.CANTalon
import org.strykeforce.sidewinder.talon.TalonParameters
import spock.lang.Specification

class WheelTest extends Specification {

    static final EPSILON = 1e-15

    def azimuth = Mock(CANTalon)
    def drive = Mock(CANTalon)


    void setupSpec() {
        TalonParameters.register("testdata/talons.toml")
    }

    def "configures azimuth and drive talons"() {
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
        azimuth.getPulseWidthPosition() >> 0x1000

        when:
        def wheel = new Wheel(azimuth, drive)
        def zeroPosition = 2767

        then:
        wheel.azimuthAbsolutePosition == 0

        when:
        wheel.setAzimuthZero(zeroPosition)

        then:
        1 * azimuth.setPosition((double) -zeroPosition / 0xFFF)
    }

    def "azimuth changes are optimized"() {
        when:
        azimuth.getPosition() >> start_position
        def wheel = new Wheel(azimuth, drive)
        wheel.set(setpoint, 1)

        then:
        Math.abs(wheel.azimuthSetpoint - end_position) < EPSILON
        wheel.isDriveReversed() == is_reversed

        where:
        start_position | setpoint | end_position | is_reversed
        0              | 0        | 0            | false
        0              | 0.1      | 0.1          | false
        0.1            | 0.2      | 0.2          | false
        0.2            | 0.3      | 0.3          | false
        0.3            | 0.4      | 0.4          | false
        0.4            | 0.45     | 0.45         | false
        0.45           | 0.5      | 0.5          | false
        0.5            | -0.5     | 0.5          | false
        -0.5           | -0.45    | -0.45        | false
        -0.45          | -0.4     | -0.4         | false
        -0.4           | -0.3     | -0.3         | false
        -0.3           | -0.2     | -0.2         | false
        -0.2           | -0.1     | -0.1         | false
        -0.1           | -0.0     | 0.0          | false

        -0.1           | 0.1      | 0.1          | false
        0.1            | -0.1     | -0.1         | false

        -0.4           | 0.4      | -0.6         | false
        -0.6           | 0.3      | -0.7         | false
        -0.7           | 0.25     | -0.75        | false
        -0.75          | 0.1      | -0.9         | false
        -0.9           | -0.1     | -1.1         | false

        0              | 0.5      | 0            | true
        0              | -0.5     | 0            | true
        -0.5           | 0.5      | -0.5         | false

        -0.1           | -0.4     | 0.1          | true
        -0.1           | 0.4      | -0.1         | true
        -1.1           | -0.4     | -0.9         | true
        -1.1           | 0.4      | -1.1         | true

        2767.4         | -0.2     | 2767.3       | true
        -2767.4        | 0.2      | -2767.3      | true
    }

    // check some wheel-related math
    def "calculates error between current azimuth and setpoint"() {

        expect:
        Math.abs(Math.IEEEremainder(setpoint - position, 1.0) - error) < EPSILON

        where:
        position | setpoint || error
        0        | 0        || 0
        0.25     | 0.25     || 0
        0.25     | 0.5      || 0.25
        0.25     | -0.25    || -0.5
        0.25     | -0.5     || 0.25
        -0.4     | 0.4      || -0.2
        0.5      | -0.5     || 0
        -0.5     | 0.5      || 0
        -0.01    | 0.01     || 0.02
        -0.4     | 0.2      || -0.4
        -2.4     | 0.2      || -0.4
    }

    def "calculate minimal azimuth error with drive direction"() {
        when:
        def error = Math.IEEEremainder(setpoint - position, 1.0)
        def isReversed = false
        if (Math.abs(error) > 0.25) {
            error -= Math.copySign(0.5, error)
            isReversed = true
        }

        then:
        Math.abs(error - expected_error) < EPSILON
        isReversed == expected_reverse
        Math.abs(position + error - expected_position) < EPSILON

        where:
        setpoint | position | expected_error | expected_position | expected_reverse
        0        | 0        | 0              | 0                 | false
        0.25     | 0.5      | -0.25          | 0.25              | false
        -0.5     | -0.25    | -0.25          | -0.5              | false
        0.25     | -0.1     | -0.15          | -0.25             | true
        -0.5     | 0.5      | 0              | 0.5               | false
        -0.5     | 0.5      | 0              | 0.5               | false
        0.49     | -0.5     | -0.01          | -0.51             | false
        0        | 1.0      | 0              | 1.0               | false
        0        | 1.1      | -0.1           | 1.0               | false
        0.4      | -2.4     | -0.2           | -2.6              | false
        0        | -0.4     | -0.1           | -0.5              | true
        0.2      | -0.4     | 0.1            | -0.3              | true
        0.2      | -2.4     | 0.1            | -2.3              | true
        -0.2     | 0.4      | -0.1           | 0.3               | true
        -0.2     | 2.4      | -0.1           | 2.3               | true

    }
}
