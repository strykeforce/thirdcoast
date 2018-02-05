package org.strykeforce.thirdcoast.util

import com.ctre.phoenix.motorcontrol.ControlMode
import spock.lang.Specification

class SettingsTest extends Specification {

    def "reads table defaults"() {
        when:
        def settings = new Settings()
        def toml = settings.getTable("THIRDCOAST.SWERVE")

        then:
        toml.getDouble("length") == 1.0
        toml.getDouble("width") == 1.0
    }

    def "overrides table defaults"() {
        given:
        def tomlStr = "[THIRDCOAST.WHEEL]\nticksPerRevolution = 2767\n" +
                "driveClosedLoopControlMode = \"Disabled\""

        when:
        def settings = new Settings(tomlStr)
        def wheel = settings.getTable("THIRDCOAST.WHEEL")

        then:
        with(wheel) {
            ticksPerRevolution == 2767
            driveSetpointMax == 0
            azimuthControlMode == ControlMode.MotionMagic.name()
            driveOpenLoopControlMode == ControlMode.PercentOutput.name()
            driveClosedLoopControlMode == ControlMode.Disabled.name()
        }

    }
}
