package org.strykeforce.thirdcoast.talon

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.slf4j.LoggerFactory
import org.strykeforce.thirdcoast.util.Settings
import spock.lang.Specification

class TalonsTest extends Specification {

    //
    // Settings
    //
    def "defaults are configured"() {
        when:
        def talons = new Talons(new Settings(), new Talons.Factory())

        then:
        with(talons) {
        }
    }


    def "creates talons"() {
        given:
        def toml = "[[TALON]]\nname=\"TEST\"\ntalonIds=[0,2,4]"
        def talon = Stub(TalonSRX)
        def factory = Mock(Talons.Factory)

        when:
        new Talons(new Settings(toml), factory)

        then:
        1 * factory.create(0) >> talon
        1 * factory.create(2) >> talon
        1 * factory.create(4) >> talon
    }

    def "skips create for duplicate talons"() {
        given:
        def toml = "[[TALON]]\nname=\"TEST\"\ntalonIds=[1,1]"
        def talon = Stub(TalonSRX)
        def factory = Mock(Talons.Factory)
        def logger = (Logger) LoggerFactory.getLogger(Talons.class)
        def level = logger.getLevel()
        logger.setLevel(Level.OFF)

        when:
        new Talons(new Settings(toml), factory)

        then:
        1 * factory.create(1) >> talon

        cleanup:
        logger.setLevel(level)
    }
}
