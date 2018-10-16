package org.strykeforce.thirdcoast.swerve

import spock.lang.Specification

class MathTest extends Specification {

    static final EPSILON = 1e-12d
    static final ROT = 4096d

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

    // check some swerve related math
    def "calculate max of 4 doubles"() {
        expect:
        Math.max(Math.max(a, b), Math.max(c, d)) == max

        where:
        a   | b   | c   | d   || max
        0.1 | 0.2 | 1.1 | 0.2 || 1.1
        1.1 | 0.2 | 0.1 | 0.2 || 1.1
        0.1 | 1.2 | 0.1 | 0.2 || 1.2
        0.1 | 1.2 | 0.1 | 1.2 || 1.2
    }

    def "convert interpolated yaw to [-180, 180] using Math.IEEEremainder"() {
        expect:
        Math.IEEEremainder(input, 360d) == output

        where:
        input | output
        0     | 0
        -180d | -180d
        180d  | 180d
        120d  | 120d
        -150d | -150d
        181d  | -179d
    }

    def "convert interpolated yaw to [-180, 180] using conditionals"() {
        when:
        if (input < -180d) input += 360d
        else if (input > 180d) input -= 360d

        then:
        input == output

        where:
        input | output
        0     | 0
        -180d | -180d
        180d  | 180d
        120d  | 120d
        -150d | -150d
        181d  | -179d
    }

}
