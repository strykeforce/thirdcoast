package org.strykeforce.thirdcoast.swerve

import com.ctre.phoenix.motorcontrol.SensorCollection
import org.strykeforce.thirdcoast.talon.TalonProvisioner
import org.strykeforce.thirdcoast.talon.ThirdCoastTalon
import org.strykeforce.thirdcoast.util.Settings
import spock.lang.Shared
import spock.lang.Specification

import static com.ctre.phoenix.motorcontrol.ControlMode.*

class WheelTest extends Specification {

    static final EPSILON = 1e-12
    static final ROT = 4096

    def azimuth = Mock(ThirdCoastTalon)
    def drive = Mock(ThirdCoastTalon)
    @Shared
    TalonProvisioner provisioner
    @Shared
    Settings settings

    static tomlString = '''
    [THIRDCOAST.WHEEL]
    ticks_per_rev = 4096

    [[TALON]]
    name = "drive"
    mode = "PercentOutput"
    setpointMax    = 12.0
    continuousCurrentLimit   = 50
    [TALON.encoder]
    device = "QuadEncoder"

    [[TALON]]
    name = "azimuth"
    mode = "Position"
    setpointMax     = 4095.0
    neutralMode = "Coast"
    pGain =   1.0
    iGain =   2.0
    dGain =   3.0
    fGain =   4.0
    iZone = 0
    forward_output_voltage_peak =  6.0
    reverse_output_voltage_peak = -6.0
    [TALON.encoder]
    device  = "CtreMagEncoder_Relative"
    
    [[TALON]]
    name = "speed"
    mode = "Velocity"
    setpointMax = 1.0
'''

    void setupSpec() {
        settings = new Settings(tomlString)
        provisioner = new TalonProvisioner(settings)
    }

    def "configures azimuth and drive talons"() {
        when:
        def wheel = new Wheel(provisioner, settings, azimuth, drive)

        then:
        1 * drive.changeControlMode(PercentOutput)
        1 * azimuth.changeControlMode(Position)

        when:
        wheel.configAzimuth("speed")

        then:
        1 * azimuth.changeControlMode(Velocity)
        0 * azimuth.changeControlMode(_)
        0 * drive.changeControlMode(_)
    }

    def "gets azimuth absolution position"() {
        setup:
        def sensorCollection = Mock(SensorCollection)
        azimuth.getSensorCollection() >> sensorCollection
        sensorCollection.getPulseWidthPosition() >> 0x1000

        when:
        def wheel = new Wheel(provisioner, settings, azimuth, drive)

        then:
        wheel.azimuthAbsolutePosition == 0

        when:
        wheel.setAzimuthZero(2767)

        then:
        1 * azimuth.setSelectedSensorPosition(-2767, 0, 10)
    }

    def "azimuth changes are optimized"() {
        when:
        azimuth.getSelectedSensorPosition(0) >> start_position * ROT
        def wheel = new Wheel(provisioner, settings, azimuth, drive)
        wheel.set(setpoint, 1d)

        then:
        Math.abs(wheel.azimuthSetpoint - end_position * ROT) < EPSILON
        wheel.isDriveReversed() == is_reversed

        where:
        start_position | setpoint || end_position | is_reversed
        0              | 0        || 0            | false
        0              | 0.1      || -0.1         | false
        -0.1           | 0.2      || -0.2         | false
        -0.2           | 0.3      || -0.3         | false
        -0.3           | 0.4      || -0.4         | false
        -0.4           | 0.45     || -0.45        | false
        -0.45          | 0.5      || -0.5         | false
        -0.5           | 0.5      || -0.5         | false
        -0.5           | 0.45     || -0.45        | false
        -0.45          | -0.4     || -0.6         | false
        -0.6           | -0.3     || -0.7         | false
        -0.7           | -0.2     || -0.8         | false
        -0.8           | -0.1     || -0.9         | false
        -0.9           | -0.0     || -1.0         | false

        -0.1           | 0.1      || -0.1         | false
        0.1            | -0.1     || 0.1          | false

        -0.4           | 0.4      || -0.4         | false
        -0.6           | 0.3      || -0.8         | true  // true -> 1024
        -0.7           | 0.25     || -0.75        | true
        -0.75          | 0.1      || -0.6         | true
        -0.9           | -0.1     || -0.9         | false

        0              | 0.5      || 0            | true
        0              | -0.5     || 0            | true
        -0.5           | 0.5      || -0.5         | false

        -0.1           | -0.4     || -0.1         | true
        -0.1           | 0.4      || 0.1          | true
        -1.1           | -0.4     || -1.1         | true
        -1.1           | 0.4      || -0.9         | true

        2767.4         | -0.2     || 2767.2       | false
        -2767.4        | 0.2      || -2767.2      | false
        2767.4         | -10.2    || 2767.2       | false
        -2767.4        | 10.2     || -2767.2      | false

        0.25           | -0.25    || 0.25         | false
    }

    def "drive output is scaled"() {
        when:
        def wheel = new Wheel(provisioner, settings, azimuth, drive)
        wheel.set(0, 1)

        then:
        1 * azimuth.set(0.0)
        1 * drive.set(12.0)
    }

    // check some wheel-related math
    def "calculates error between current azimuth and setpoint"() {
        expect:
        Math.abs(Math.IEEEremainder(setpoint * ROT - position * ROT, ROT) - error * ROT) < EPSILON

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
        def error = Math.IEEEremainder(setpoint * ROT - position * ROT, ROT)
        def isReversed = false
        if (Math.abs(error) > 0.25 * ROT) {
            error -= Math.copySign(0.5 * ROT, error)
            isReversed = true
        }

        then:
        Math.abs(error - expected_error * ROT) < EPSILON
        isReversed == expected_reverse
        Math.abs(position * ROT + error - expected_position * ROT) < EPSILON

        where:
        setpoint | position || expected_error | expected_position | expected_reverse
        0        | 0        || 0              | 0                 | false
        0.25     | 0.5      || -0.25          | 0.25              | false
        -0.5     | -0.25    || -0.25          | -0.5              | false
        0.25     | -0.1     || -0.15          | -0.25             | true
        -0.5     | 0.5      || 0              | 0.5               | false
        -0.5     | 0.5      || 0              | 0.5               | false
        0.49     | -0.5     || -0.01          | -0.51             | false
        0        | 1.0      || 0              | 1.0               | false
        0        | 1.1      || -0.1           | 1.0               | false
        0.4      | -2.4     || -0.2           | -2.6              | false
        0        | -0.4     || -0.1           | -0.5              | true
        0.2      | -0.4     || 0.1            | -0.3              | true
        0.2      | -2.4     || 0.1            | -2.3              | true
        -0.2     | 0.4      || -0.1           | 0.3               | true
        -0.2     | 2.4      || -0.1           | 2.3               | true
        0.6      | 0        || 0.1            | 0.1               | true
        -1.0     | 0.5      || 0.0            | 0.5               | true
        1.5      | 0.5      || 0.0            | 0.5               | false

    }
}
