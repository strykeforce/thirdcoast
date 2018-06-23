package org.strykeforce.thirdcoast.swerve

import org.strykeforce.thirdcoast.util.Settings
import spock.lang.Specification

class SwerveDriveOffsetTest extends Specification {
    def "calculate findRadii no offset"() {
        given:
        def offsetX = 0.0
        def offsetY = 0.0
        SwerveDrive swerveDrive = new SwerveDrive(null, null, null)

        when:
        double[] radii = swerveDrive.findRadii(length, width, offsetX, offsetY)

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
        12.0   | 20.0   | 23.3238  | 23.3238  | 23.3238  | 23.3238
        30.0   | 2.0    | 30.0665  | 30.0665  | 30.0665  | 30.0665
        24.0   | 20.0   | 31.2409  | 31.2409  | 31.2409  | 31.2409
    }

    def "calculate findRadii with offset"() {
        given:
        SwerveDrive swerveDrive = new SwerveDrive(null, null,null)

        when:
        double[] radii = swerveDrive.findRadii(length, width, offsetX, offsetY)

        then:
        1e-3 > Math.abs(r0 - radii[0])
        1e-3 > Math.abs(r1 - radii[1])
        1e-3 > Math.abs(r2 - radii[2])
        1e-3 > Math.abs(r3 - radii[3])

        where:
        length | width  | offsetX  | offsetY  | r0       | r1       | r2       | r3
        30.0   | 30.0   | 0.0      | 0.0      | 42.4264  | 42.4264  | 42.4264  | 42.4264
        1.0    | 1.0    | 0.0      | 0.0      | 1.414    | 1.414    | 1.414    | 1.414
        1.0    | 1.0    | 1.0      | 1.0      | 0.0      | 2.0      | 2.0      | 2.828
        1.0    | 1.0    | -1.0     | 1.0      | 2.0      | 0.0      | 2.828    | 2.0
        1.0    | 1.0    | 1.0      | -1.0     | 2.0      | 2.828    | 0.0      | 2.0
        1.0    | 1.0    | -1.0     | -1.0     | 2.828    | 2.0      | 2.0      | 0.0


    }
}
