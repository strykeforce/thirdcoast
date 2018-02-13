package org.strykeforce.thirdcoast.util

import com.ctre.phoenix.motorcontrol.ControlMode
import com.moandjiezana.toml.Toml
import spock.lang.Specification

class SettingsTest extends Specification {

    void matchesDefaultWheel(Toml wheel) {
        assert wheel.getLong("ticksPerRevolution") == 4096L
        assert wheel.getLong("driveSetpointMax") == 0L
        assert wheel.getString("azimuthControlMode") == ControlMode.MotionMagic.name()
        assert wheel.getString("driveOpenLoopControlMode") == ControlMode.PercentOutput.name()
        assert wheel.getString("driveClosedLoopControlMode") == ControlMode.Velocity.name()
    }

    def "reads table defaults"() {
        when:
        def settings = new Settings()
        def swerve = settings.getTable("THIRDCOAST.SWERVE")

        then:
        swerve.getDouble("length") == 1.0
        swerve.getDouble("width") == 1.0

        when:
        def wheel = settings.getTable("THIRDCOAST.WHEEL")

        then:
        matchesDefaultWheel(wheel)
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
            getLong("ticksPerRevolution") == 2767
            getLong("driveSetpointMax") == 0
            getString("azimuthControlMode") == ControlMode.MotionMagic.name()
            getString("driveOpenLoopControlMode") == ControlMode.PercentOutput.name()
            getString("driveClosedLoopControlMode") == ControlMode.Disabled.name()
        }
    }

    def "reads settings from resource URL with merged defaults"() {
        given:
        def url = this.class.getResource("/testdata/settings/test.toml")

        when:
        def settings = new Settings(url)
        def wheel = settings.getTable("THIRDCOAST.WHEEL")

        then:
        with(wheel) {
            getLong("ticksPerRevolution") == 4096
            getLong("driveSetpointMax") == 2767
            getString("azimuthControlMode") == ControlMode.Disabled.name()
            getString("driveOpenLoopControlMode") == ControlMode.PercentOutput.name()
            getString("driveClosedLoopControlMode") == ControlMode.Velocity.name()
        }

        when:
        def talons = settings.getTable("THIRDCOAST.TALONS")

        then:
        talons.timeout == 2767
    }

    def "reads default setting from missing URL"() {
        given:
        def url = this.class.getResource("/testdata/settings/missing.toml")

        when:
        def settings = new Settings(url as URL)
        def wheel = settings.getTable("THIRDCOAST.WHEEL")

        then:
        matchesDefaultWheel(wheel)
    }
}
