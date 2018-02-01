package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.strykeforce.thirdcoast.util.Settings
import spock.lang.Specification

class TalonsTest extends Specification {

    def "creates talons"() {
        given:
        def toml ="[[TALON]]\nname=\"TEST\"\ntalonIds=[0,2,4]"
        def talon = Stub(TalonSRX)
        def factory = Mock(Talons.Factory)

        when:
        new Talons(new Settings(toml), factory)

        then:
        1 * factory.create(0) >> talon
        1 * factory.create(2) >> talon
        1 * factory.create(4) >> talon
    }

    def "throws exception for duplicate talons"() {
        given:
        def toml ="[[TALON]]\nname=\"TEST\"\ntalonIds=[1,1]"
        def talon = Stub(TalonSRX)
        def factory = Mock(Talons.Factory)

        when:
        new Talons(new Settings(toml), factory)

        then:
        1 * factory.create(1) >> talon
        thrown(IllegalStateException)
    }
}
