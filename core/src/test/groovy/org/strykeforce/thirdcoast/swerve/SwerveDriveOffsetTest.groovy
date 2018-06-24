package org.strykeforce.thirdcoast.swerve

import org.strykeforce.thirdcoast.util.Settings
import spock.lang.Shared
import spock.lang.Specification

class SwerveDriveOffsetTest extends Specification {

    @Shared
    Settings settings

    void setupSpec() {
        settings = new Settings()
    }

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
        length | width | r1      | r2      | r3      | r4
        30.0   | 30.0  | 42.4264 | 42.4264 | 42.4264 | 42.4264
        1.0    | 1.0   | 1.414   | 1.414   | 1.414   | 1.414
        10.0   | 10.0  | 14.1421 | 14.1421 | 14.1421 | 14.1421
        10.0   | 12.0  | 15.6204 | 15.6204 | 15.6204 | 15.6204
        12.0   | 20.0  | 23.3238 | 23.3238 | 23.3238 | 23.3238
        30.0   | 2.0   | 30.0665 | 30.0665 | 30.0665 | 30.0665
        24.0   | 20.0  | 31.2409 | 31.2409 | 31.2409 | 31.2409
    }

    def "calculate findRadii with offset"() {
        given:
        SwerveDrive swerveDrive = new SwerveDrive(null, null, null)

        when:
        double[] radii = swerveDrive.findRadii(length, width, offsetX, offsetY)

        then:
        1e-3 > Math.abs(r0 - radii[0])
        1e-3 > Math.abs(r1 - radii[1])
        1e-3 > Math.abs(r2 - radii[2])
        1e-3 > Math.abs(r3 - radii[3])

        where:
        length | width | offsetX | offsetY | r0      | r1      | r2      | r3
        30.0   | 30.0  | 0.0     | 0.0     | 42.4264 | 42.4264 | 42.4264 | 42.4264
        1.0    | 1.0   | 0.0     | 0.0     | 1.414   | 1.414   | 1.414   | 1.414
        1.0    | 1.0   | 1.0     | 1.0     | 0.0     | 2.0     | 2.0     | 2.828
        1.0    | 1.0   | -1.0    | 1.0     | 2.0     | 0.0     | 2.828   | 2.0
        1.0    | 1.0   | 1.0     | -1.0    | 2.0     | 2.828   | 0.0     | 2.0
        1.0    | 1.0   | -1.0    | -1.0    | 2.828   | 2.0     | 2.0     | 0.0
        2.0    | 2.0   | 4.0     | 4.0     | 2.8284  | 6.324   | 6.324   | 8.485
        2.0    | 2.0   | -4.0    | 4.0     | 6.324   | 2.8284  | 8.485   | 6.324
        2.0    | 2.0   | 4.0     | -4.0    | 6.324   | 8.485   | 2.8284  | 6.324
        2.0    | 2.0   | -4.0    | -4.0    | 8.485   | 6.324   | 6.324   | 2.8284
        24.0   | 20.0  | 800.0   | 200.0   | 799.609 | 838.675 | 811.527 | 850.045
        24.0   | 20.0  | -800.0  | 200.0   | 838.675 | 799.609 | 850.045 | 811.527
        24.0   | 20.0  | 800.0   | -200.0  | 811.527 | 850.045 | 799.609 | 838.675
        24.0   | 20.0  | -800.0  | -200.0  | 850.045 | 811.527 | 838.675 | 799.609
    }

    def "check length components"() {
        when:
        def toml = "[THIRDCOAST.SWERVE]\nlength = " + length + "\nwidth = " + width + "\noffsetX = " + offsetX +
                "\noffsetY = " + offsetY
        SwerveDrive swerveDrive = new SwerveDrive(null, null, new Settings(toml))
        double[] kLengthComponents = swerveDrive.getLengthComponents()

        then:
        1e-3 > Math.abs(kLengthComponents[0] - l0)
        1e-3 > Math.abs(kLengthComponents[1] - l1)
        1e-3 > Math.abs(kLengthComponents[2] - l2)
        1e-3 > Math.abs(kLengthComponents[3] - l3)

        where:
        length | width | offsetX | offsetY | l0     | l1     | l2     | l3
        30.0   | 30.0  | 0.0     | 0.0     | 0.7071 | 0.7071 | 0.7071 | 0.7071
        1.0    | 1.0   | 0.0     | 0.0     | 0.7072 | 0.7072 | 0.7072 | 0.7072
        1.0    | 1.0   | 1.0     | 1.0     | 0.0    | 0.5    | 0.5    | 0.3536
        1.0    | 1.0   | -1.0    | 1.0     | 0.5    | 0.0    | 0.3526 | 0.5
        1.0    | 1.0   | 1.0     | -1.0    | 0.5    | 0.3526 | 0.0    | 0.5
        1.0    | 1.0   | -1.0    | -1.0    | 0.3526 | 0.5    | 0.5    | 0.0
        2.0    | 2.0   | 4.0     | 4.0     | 0.7071 | 0.3162 | 0.3162 | 0.2357
//        2.0    | 2.0   | -4.0    | 4.0     |
//        2.0    | 2.0   | 4.0     | -4.0    |
//        2.0    | 2.0   | -4.0    | -4.0    |
//        24.0   | 20.0  | 800.0   | 200.0   |
//        24.0   | 20.0  | -800.0  | 200.0   |
//        24.0   | 20.0  | 800.0   | -200.0  |
//        24.0   | 20.0  | -800.0  | -200.0  |
    }

    def "check width components"() {
        when:
        def toml = "[THIRDCOAST.SWERVE]\nlength = " + length + "\nwidth = " + width + "\noffsetX = " + offsetX +
                "\noffsetY = " + offsetY
        SwerveDrive swerveDrive = new SwerveDrive(null, null, new Settings(toml))
        double[] kWidthComponents = swerveDrive.getWidthComponents()

        then:
        1e-3 > Math.abs(kWidthComponents[0] - w0)
        1e-3 > Math.abs(kWidthComponents[1] - w1)
        1e-3 > Math.abs(kWidthComponents[2] - w2)
        1e-3 > Math.abs(kWidthComponents[3] - w3)

        where:
        length | width | offsetX | offsetY | w0     | w1     | w2     | w3
        30.0   | 30.0  | 0.0     | 0.0     | 0.7071 | 0.7071 | 0.7071 | 0.7071
        1.0    | 1.0   | 0.0     | 0.0     | 0.7072 | 0.7072 | 0.7072 | 0.7072
        1.0    | 1.0   | 1.0     | 1.0     | 0.0    | 0.5    | 0.5    | 0.3536
        1.0    | 1.0   | -1.0    | 1.0     | 0.5    | 0.0    | 0.3526 | 0.5
        1.0    | 1.0   | 1.0     | -1.0    | 0.5    | 0.3526 | 0.0    | 0.5
        1.0    | 1.0   | -1.0    | -1.0    | 0.3526 | 0.5    | 0.5    | 0.0
        2.0    | 2.0   | 4.0     | 4.0     | 0.7071 | 0.3162 | 0.3162 | 0.2357
//        2.0    | 2.0   | -4.0    | 4.0     |
//        2.0    | 2.0   | 4.0     | -4.0    |
//        2.0    | 2.0   | -4.0    | -4.0    |
//        24.0   | 20.0  | 800.0   | 200.0   |
//        24.0   | 20.0  | -800.0  | 200.0   |
//        24.0   | 20.0  | 800.0   | -200.0  |
//        24.0   | 20.0  | -800.0  | -200.0  |
    }

//    def "check swerve drive"() {
//        given:
//        Wheel[] wheels = [Mock(Wheel), Mock(Wheel), Mock(Wheel), Mock(Wheel)]
//
//        double length = 2767.0
//        double width = 1.0
//
//        def toml = "[THIRDCOAST.SWERVE]\nlength = " + length + "\nwidth = " + width + "\noffsetX = " + oX +
//                "\noffsetY = " + oY
//        def swerveDrive = Mock(SwerveDrive(null, wheels, new Settings(toml)))
//
//        when:
//        for (Wheel w : wheels) {
//            w.azimuthTalon.getPosition() >> 0
//        }
//        swerveDrive.drive(f, s, a)
//
//        then:
//        1 * wheels[0].set({ Math.abs(it - w0a) < 1e-3 }, { Math.abs(it - w0d) < 1e-4 })
//        1 * wheels[1].set({ Math.abs(it - w1a) < 1e-3 }, { Math.abs(it - w1d) < 1e-4 })
//        1 * wheels[2].set({ Math.abs(it - w2a) < 1e-3 }, { Math.abs(it - w2d) < 1e-4 })
//        1 * wheels[3].set({ Math.abs(it - w3a) < 1e-3 }, { Math.abs(it - w3d) < 1e-4 })
//
//        where:
//        oX  | oY  | f     | s   | a    || w0a    | w0d    | w1a    | w1d    | w2a     | w2d    | w3a     | w3d
//        0.0 | 0.0 | 0     | 0   | 0    || 0      | 0      | 0      | 0      | 0       | 0      | 0       | 0
//        0.0 | 0.0 | 1     | 0   | 0    || 0      | 1      | 0      | 1      | 0       | 1      | 0       | 1
//        0.0 | 0.0 | -1    | 0   | 0    || 0.5    | 1      | 0.5    | 1      | 0.5     | 1      | 0.5     | 1
//        0.0 | 0.0 | 0     | 1   | 0    || 0.25   | 1      | 0.25   | 1      | 0.25    | 1      | 0.25    | 1
//        0.0 | 0.0 | 0     | -1  | 0    || -0.25  | 1      | -0.25  | 1      | -0.25   | 1      | -0.25   | 1
//
//        0.0 | 0.0 | 1     | 1   | 0    || 0.125  | 1      | 0.125  | 1      | 0.125   | 1      | 0.125   | 1
//        0.0 | 0.0 | -1    | 1   | 0    || 0.375  | 1      | 0.375  | 1      | 0.375   | 1      | 0.375   | 1
//        0.0 | 0.0 | 1     | -1  | 0    || -0.125 | 1      | -0.125 | 1      | -0.125  | 1      | -0.125  | 1
//        0.0 | 0.0 | -1    | -1  | 0    || -0.375 | 1      | -0.375 | 1      | -0.375  | 1      | -0.375  | 1
//
//        0.0 | 0.0 | 0     | 0   | 1    || 0.125  | 1      | 0.375  | 1      | -0.125  | 1      | -0.375  | 1
//        0.0 | 0.0 | 0     | 0   | -1   || -0.375 | 1      | -0.125 | 1      | 0.375   | 1      | 0.125   | 1
//
//        // expected values from Ether's swerve tester spreadsheet
//        0.0 | 0.0 | 0.5   | 0   | 1    || 0.0844 | 1      | 0.2953 | 0.5267 | -0.0844 | 1      | -0.2953 | 0.5267
//        0.0 | 0.0 | 1     | 1   | 1    || 0.125  | 1      | 0.2231 | 0.7174 | 0.0269  | 0.7174 | 0.125   | 0.1716
//        0.0 | 0.0 | -0.25 | 0.5 | -0.2 || 0.3819 | 0.5308 | 0.2967 | 0.3746 | 0.3372  | 0.7514 | 0.2767  | 0.6505
//    }
}
