package org.strykeforce.thirdcoast.swerve

import spock.lang.Specification

class SwerveDriveOffsetTest extends Specification {
    def "calculate radii for offset center position"() {
        given:
        SwerveDrive swerveDrive = new SwerveDrive(null, null, null)

        when:
        double[] radii = swerveDrive.findRadii(length, width)

        then:
        1e-3 > Math.abs(r1 - radii[0])
        1e-3 > Math.abs(r2 - radii[1])
        1e-3 > Math.abs(r3 - radii[2])
        1e-3 > Math.abs(r4 - radii[3])

        where:
        length | width  | r1       | r2       | r3       | r4
        30.0   | 30.0   | 42.4264  | 42.4264  | 42.4264  | 42.4264
        1.0    | 1.0    | 1.414    | 1.414    | 1.414    | 1.414
        10.0   | 10.0   | 14.1421  | 14.1421  | 14.1421  | 14.1421
        10.0   | 12.0   | 15.6204  | 15.6204  | 15.6204  | 15.6204

    }
}
