package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.SensorCollection
import spock.lang.Specification

import static com.ctre.phoenix.motorcontrol.StatusFrame.*
import static org.strykeforce.thirdcoast.talon.TalonConfiguration.TIMEOUT_MS

class TalonFactoryTest extends Specification {

    def wrapperFactory = Mock(ThirdCoastTalonFactory)
    def wrapper = Mock(ThirdCoastTalon)
    def sensorCollection = Mock(SensorCollection)


    def "creation of wrapped talon with defaults"() {
        given: "mock wrapped talon and factory under test"
        def factory = new TalonFactory(Stub(TalonProvisioner), wrapperFactory)

        when:
        factory.getTalon(27)

        then:
        1 * wrapperFactory.create(27) >> wrapper
        1 * wrapper.changeControlMode(TalonControlMode.Voltage)
        1 * wrapper.selectProfileSlot(0, 0)
        1 * wrapper.setInverted(false)
        1 * wrapper.setStatusFramePeriod(Status_4_AinTempVbat, 100, TIMEOUT_MS)
        1 * wrapper.setStatusFramePeriod(Status_2_Feedback0, 20, TIMEOUT_MS)
        1 * wrapper.setStatusFramePeriod(Status_1_General, 10, TIMEOUT_MS)
        1 * wrapper.enableVoltageCompensation(true)
        1 * wrapper.setIntegralAccumulator(0.0d, 0, 10)
        1 * wrapper.setIntegralAccumulator(0.0d, 1, 10)
        1 * wrapper.clearMotionProfileHasUnderrun(TalonFactory.TIMEOUT_MS)
        1 * wrapper.clearStickyFaults(TalonFactory.TIMEOUT_MS)
        1 * wrapper.clearMotionProfileTrajectories()
        1 * wrapper.getSensorCollection() >> sensorCollection
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
        1 * wrapper.getSensorCollection() >> sensorCollection
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
        1 * wrapper.getSensorCollection() >> sensorCollection

        and:
        factory.hasSeen(27)
        talon.is(factory.getTalon(27))
    }
}
