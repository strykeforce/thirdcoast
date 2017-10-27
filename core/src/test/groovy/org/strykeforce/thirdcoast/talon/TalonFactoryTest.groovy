package org.strykeforce.thirdcoast.talon

import com.ctre.CANTalon
import spock.lang.Specification

class TalonFactoryTest extends Specification {

    def "creation of wrapped talon with defaults"() {
        given: "mock wrapped talon"
        def wrapperFactory = Mock(TalonFactory.WrapperFactory)
        TalonFactory.Wrapper wrapper = Mock(TalonFactory.Wrapper)

        and: "factory under test"
        def factory = new TalonFactory(Stub(TalonProvisioner), wrapperFactory)

        when:
        factory.createTalon(27)

        then:
        1 * wrapperFactory.createWrapper(27, TalonFactory.CONTROL_FRAME_MS) >> wrapper
        1 * wrapper.changeControlMode(CANTalon.TalonControlMode.Voltage)
        1 * wrapper.clearIAccum()
        1 * wrapper.ClearIaccum()
        1 * wrapper.clearMotionProfileHasUnderrun()
        1 * wrapper.clearMotionProfileTrajectories()
        1 * wrapper.clearStickyFaults()
        1 * wrapper.enableZeroSensorPositionOnForwardLimit(false)
        1 * wrapper.enableZeroSensorPositionOnIndex(false, false)
        1 * wrapper.enableZeroSensorPositionOnReverseLimit(false)
        1 * wrapper.reverseOutput(false)
        1 * wrapper.reverseSensor(false)
        1 * wrapper.setAnalogPosition(0)
        1 * wrapper.setPosition(0)
        1 * wrapper.setProfile(0)
        1 * wrapper.setPulseWidthPosition(0)
        0 * wrapper._
    }

    def "creation of wrapped talon with config"() {
        given: "mock wrapped talon and provisioner"
        def wrapperFactory = Mock(TalonFactory.WrapperFactory)
        TalonFactory.Wrapper wrapper = Mock(TalonFactory.Wrapper)
        def provisioner = Mock(TalonProvisioner)
        def config = Mock(TalonConfiguration)

        and: "factory under test"
        def factory = new TalonFactory(provisioner, wrapperFactory)

        when:
        factory.createTalonWithConfiguration(67, "test-config")

        then:
        1 * wrapperFactory.createWrapper(67, TalonFactory.CONTROL_FRAME_MS) >> wrapper
        1 * provisioner.configurationFor("test-config") >> config
        1 * config.configure(wrapper)
    }
}
