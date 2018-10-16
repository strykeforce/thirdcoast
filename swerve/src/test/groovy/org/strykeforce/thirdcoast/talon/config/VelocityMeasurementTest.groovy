package org.strykeforce.thirdcoast.talon.config

import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.moandjiezana.toml.Toml
import spock.lang.Specification

class VelocityMeasurementTest extends Specification {

    final static Random random = new Random()

    def "configures default values"() {
        given:
        def vm = VelocityMeasurement.DEFAULT
        def talon = Mock(TalonSRX)
        def timeout = random.nextInt()

        when:
        vm.configure(talon, timeout)

        then:
        1 * talon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, timeout)
        1 * talon.configVelocityMeasurementWindow(64, timeout)
        0 * talon._
    }

    def "overrides default with TOML"() {
        given:
        def tomlStr = "period = \"Period_2Ms\""
        def toml = new Toml().read(tomlStr)
        def expected = new VelocityMeasurement(VelocityMeasPeriod.Period_2Ms, 64)

        expect:
        VelocityMeasurement.create(toml) == expected
    }

    def "default values for null TOML"() {
        expect:
        VelocityMeasurement.create(null) == VelocityMeasurement.DEFAULT
    }

}
