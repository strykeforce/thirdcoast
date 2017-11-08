package org.strykeforce.thirdcoast.talon

import com.ctre.CANTalon
import com.moandjiezana.toml.Toml
import spock.lang.Shared
import spock.lang.Specification

import static com.ctre.CANTalon.FeedbackDevice.QuadEncoder
import static com.ctre.CANTalon.TalonControlMode.Voltage
import static com.ctre.CANTalon.VelocityMeasurementPeriod.Period_100Ms
import static com.ctre.CANTalon.VelocityMeasurementPeriod.Period_1Ms
import static com.ctre.CANTalon.VelocityMeasurementPeriod.Period_5Ms

class TalonConfigurationTest extends Specification {

    def talon = Mock(CANTalon)
    def tcb = new TalonConfigurationBuilder()


    def "configures voltage mode talon"() {
        when:
        def tc = tcb.name("test")
                .mode(Voltage)
                .setpointMax(12)
                .encoder(QuadEncoder, true, 360)
                .brakeInNeutral(true)
                .forwardLimitSwitch(true)
                .forwardSoftLimit(10000)
                .reverseSoftLimit(12000)
                .outputReversed(true)
                .velocityMeasurementPeriod(Period_5Ms)
                .velocityMeasurementWindow(16)
                .currentLimit(50)
                .build()
        tc.configure(talon)

        then:
        with(talon) {
            1 * changeControlMode(Voltage)
            1 * setFeedbackDevice(QuadEncoder)
            1 * enableBrakeMode(true)
            1 * reverseSensor(true)
            1 * reverseOutput(true)
            1 * SetVelocityMeasurementPeriod(Period_5Ms)
            1 * SetVelocityMeasurementWindow(16)
            1 * enableLimitSwitch(true, false)
            1 * ConfigFwdLimitSwitchNormallyOpen(true)
            0 * ConfigRevLimitSwitchNormallyOpen(true)
            0 * ConfigRevLimitSwitchNormallyOpen(false)
            1 * enableForwardSoftLimit(true)
            1 * setForwardSoftLimit(10_000.0)
            1 * enableReverseSoftLimit(true)
            1 * setReverseSoftLimit(12_000.0)
            1 * EnableCurrentLimit(true)
            1 * setCurrentLimit(50)
            1 * setSafetyEnabled(false)
            1 * setProfile(0)
            1 * talon.setExpiration(0.1)
            1 * talon.configEncoderCodesPerRev(360)
            1 * talon.isSensorPresent(QuadEncoder)
            0 * talon._
        }
    }

    def "no current limit set for all defaults"() {
        when:
        def tc = tcb.build()
        tc.configure(talon)

        then:
        1 * talon.EnableCurrentLimit(false)
        0 * talon.setCurrentLimit(_)
    }

    def "brake in neutral is default"() {
        when:
        def tc = tcb.build()
        tc.configure(talon)

        then:
        1 * talon.enableBrakeMode(true)
    }

    def "don't brake in neutral set"() {
        when:
        def tc = tcb.brakeInNeutral(false).build()
        tc.configure(talon)

        then:
        1 * talon.enableBrakeMode(false)
    }

    def "reverse output is default"() {
        when:
        def tc = tcb.build()
        tc.configure(talon)

        then:
        1 * talon.reverseOutput(false)
    }

    def "reverse output set"() {
        when:
        def tc = tcb.outputReversed(true).build()
        tc.configure(talon)

        then:
        tc.outputReversed
        1 * talon.reverseOutput(true)
    }

    def "sets defaults"() {
        when:
        def tc = tcb.build()
        tc.configure(talon)

        then:
        with(talon) {
            1 * changeControlMode(Voltage)
            1 * setFeedbackDevice(QuadEncoder)
            1 * enableBrakeMode(true)
            1 * reverseSensor(false)
            1 * reverseOutput(false)
            1 * SetVelocityMeasurementPeriod(Period_100Ms)
            1 * SetVelocityMeasurementWindow(64)
            1 * enableLimitSwitch(false, false)
            1 * enableForwardSoftLimit(false)
            1 * enableReverseSoftLimit(false)
            1 * EnableCurrentLimit(false)
            1 * setSafetyEnabled(false)
            1 * setProfile(0)
            1 * talon.setExpiration(0.1)
            1 * talon.isSensorPresent(QuadEncoder)
            0 * talon._
        }

    }
}
