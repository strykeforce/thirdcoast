package org.strykeforce.thirdcoast.talon

import edu.wpi.first.wpilibj.MotorSafety
import spock.lang.Specification

class TalonFactoryTest extends Specification {

    def wrapperFactory = Mock(ThirdCoastTalonFactory)
    def wrapper = Mock(ThirdCoastTalon)


    def "creation of wrapped talon with defaults"() {
        given: "mock wrapped talon and factory under test"
        def factory = new TalonFactory(Stub(TalonProvisioner), wrapperFactory)

        when:
        factory.getTalon(27)

        then:
        1 * wrapperFactory.create(27) >> wrapper
        1 * wrapper.setSafetyEnabled(false)
        1 * wrapper.setExpiration(MotorSafety.DEFAULT_SAFETY_EXPIRATION)
        1 * wrapper.clearStickyFaults(TalonFactory.TIMEOUT_MS)
        0 * wrapper._
    }

    def "creation of wrapped talon with config"() {
        given: "mock wrapped talon and provisioner"
        def provisioner = Mock(TalonProvisioner)
        def config = Mock(TalonConfiguration)

        and: "factory under test"
        def factory = new TalonFactory(provisioner, wrapperFactory)

        when:
        factory.getTalonWithConfiguration(67, "test-config")

        then:
        1 * wrapperFactory.create(67) >> wrapper
        1 * provisioner.configurationFor("test-config") >> config
        1 * config.configure(wrapper)
    }

    def "gets cached wrapped talon"() {
        given: "mock wrapped talon and factory under test"
        def factory = new TalonFactory(Stub(TalonProvisioner), wrapperFactory)
        wrapper.getDeviceID() >> 27

        when:
        def talon = factory.getTalon(27)

        then:
        1 * wrapperFactory.create(27) >> wrapper

        and:
        factory.hasSeen(27)
        talon.is(factory.getTalon(27))
    }
}
