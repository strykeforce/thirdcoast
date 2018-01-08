package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import spock.lang.Ignore
import spock.lang.Specification

@Ignore("2018")
class TalonFactoryTest extends Specification {

    def wrapperFactory = Mock(TalonFactory.WrapperFactory)
    def wrapper = Mock(TalonFactory.Wrapper)


    def "creation of wrapped talon with defaults"() {
        given: "mock wrapped talon and factory under test"
        def factory = new TalonFactory(Stub(TalonProvisioner), wrapperFactory)

        when:
        factory.getTalon(27)

        then:
        1 * wrapperFactory.createWrapper(27, TalonFactory.CONTROL_FRAME_MS) >> wrapper
        1 * wrapper.setStatusFrameRateMs(TalonSRX.StatusFrameRate.AnalogTempVbat, 100)
        1 * wrapper.changeControlMode(TalonControlMode.Voltage)
        1 * wrapper.setStatusFrameRateMs(TalonSRX.StatusFrameRate.Feedback, 20)
        1 * wrapper.setStatusFrameRateMs(TalonSRX.StatusFrameRate.General, 10)
        1 * wrapper.setStatusFrameRateMs(TalonSRX.StatusFrameRate.PulseWidth, 100)
        1 * wrapper.setStatusFrameRateMs(TalonSRX.StatusFrameRate.QuadEncoder, 100)
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
        def provisioner = Mock(TalonProvisioner)
        def config = Mock(TalonConfiguration)

        and: "factory under test"
        def factory = new TalonFactory(provisioner, wrapperFactory)

        when:
        factory.getTalonWithConfiguration(67, "test-config")

        then:
        1 * wrapperFactory.createWrapper(67, TalonFactory.CONTROL_FRAME_MS) >> wrapper
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
        1 * wrapperFactory.createWrapper(27, TalonFactory.CONTROL_FRAME_MS) >> wrapper

        and:
        factory.hasSeen(27)
        talon.is(factory.getTalon(27))
    }
}
