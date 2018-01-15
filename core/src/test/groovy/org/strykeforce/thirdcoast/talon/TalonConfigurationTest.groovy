package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.ErrorCode
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod
import edu.wpi.first.wpilibj.MotorSafety
import spock.lang.Specification

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder
import static com.ctre.phoenix.motorcontrol.VelocityMeasPeriod.Period_5Ms
import static org.strykeforce.thirdcoast.talon.TalonConfiguration.TIMEOUT_MS

class TalonConfigurationTest extends Specification {

    def talon = Mock(ThirdCoastTalon)
    def tcb = new TalonConfigurationBuilder()


    def "configures voltage mode talon"() {
        when:
        def tc = tcb.name("test")
                .mode(PercentOutput)
                .setpointMax(12)
                .encoder(QuadEncoder, true)
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
            1 * talon.setSafetyEnabled(false)
            1 * talon.setExpiration(MotorSafety.DEFAULT_SAFETY_EXPIRATION)
            1 * talon.selectProfileSlot(0, 0)
            1 * talon.configVoltageCompSaturation(12.0d, TIMEOUT_MS)
            1 * talon.enableVoltageCompensation(true)
            1 * talon.configOpenloopRamp(0.0d, TIMEOUT_MS)
            1 * talon.configSelectedFeedbackSensor(QuadEncoder, 0, TIMEOUT_MS) >> ErrorCode.OK
            1 * talon.setSensorPhase(true)
            1 * talon.getDescription()
            1 * talon.setNeutralMode(NeutralMode.Brake)
            1 * talon.setInverted(true)
            1 * talon.configVelocityMeasurementPeriod(Period_5Ms, TIMEOUT_MS)
            1 * talon.configVelocityMeasurementWindow(16, TIMEOUT_MS)
            1 * talon.configContinuousCurrentLimit(50, TIMEOUT_MS)
            1 * talon.enableCurrentLimit(true)
            1 * talon.getDeviceID()
            1 * talon.changeControlMode(PercentOutput)
            1 * talon.configPeakCurrentLimit(0, TIMEOUT_MS)
            1 * talon.configForwardSoftLimitEnable(true, TIMEOUT_MS)
            1 * talon.configForwardSoftLimitThreshold(10000, TIMEOUT_MS)
            1 * talon.configReverseSoftLimitEnable(true, TIMEOUT_MS)
            1 * talon.configReverseSoftLimitThreshold(12000, TIMEOUT_MS)

            0 * talon._
        }
    }

    def "no current limit set for all defaults"() {
        when:
        def tc = tcb.build()
        tc.configure(talon)

        then:
        1 * talon.enableCurrentLimit(false)
        0 * talon.configContinuousCurrentLimit(_)
        0 * talon.configPeakCurrentLimit(_)
    }

    def "brake in neutral is default"() {
        when:
        def tc = tcb.build()
        tc.configure(talon)

        then:
        1 * talon.setNeutralMode(NeutralMode.Coast)
    }

    def "don't brake in neutral set"() {
        when:
        def tc = tcb.brakeInNeutral(false).build()
        tc.configure(talon)

        then:
        1 * talon.setNeutralMode(NeutralMode.Coast)
    }

    def "reverse output is default"() {
        when:
        def tc = tcb.build()
        tc.configure(talon)

        then:
        1 * talon.setInverted(false)
    }

    def "reverse output set"() {
        when:
        def tc = tcb.outputReversed(true).build()
        tc.configure(talon)

        then:
        tc.outputReversed
        1 * talon.setInverted(true)
    }

    def "voltage ramp rate set"() {
        when:
        def tc = tcb.voltageRampRate(27.67d).build()
        tc.configure(talon)

        then:
        tc.openLoopRampTime == 27.67
        1 * talon.configOpenloopRamp(27.67d, TIMEOUT_MS)
    }

    def "sets defaults"() {
        when:
        def tc = tcb.build()
        tc.configure(talon)

        then:
        1 * talon.changeControlMode(PercentOutput)
        1 * talon.enableVoltageCompensation(true)
        1 * talon.configSelectedFeedbackSensor(QuadEncoder, 0, TIMEOUT_MS) >> ErrorCode.OK
        1 * talon.configVoltageCompSaturation(12.0d, TIMEOUT_MS)

        1 * talon.setNeutralMode(NeutralMode.Coast)
        1 * talon.setSafetyEnabled(false)
        1 * talon.setInverted(false)
        1 * talon.configOpenloopRamp(0d, TIMEOUT_MS)
        1 * talon.getDeviceID()
        1 * talon.getDescription()
        1 * talon.setSensorPhase(false)
        1 * talon.configVelocityMeasurementWindow(64, TIMEOUT_MS)
        1 * talon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, TIMEOUT_MS)
        1 * talon.configForwardSoftLimitEnable(false, TIMEOUT_MS)
        1 * talon.configForwardSoftLimitThreshold(0, TIMEOUT_MS)
        1 * talon.configReverseSoftLimitEnable(false, TIMEOUT_MS)
        1 * talon.configReverseSoftLimitThreshold(0, TIMEOUT_MS)
        1 * talon.configContinuousCurrentLimit(0, TIMEOUT_MS)
        1 * talon.enableCurrentLimit(false)
        1 * talon.selectProfileSlot(0, 0)
//        1 * talon.enableLimitSwitch(false, false)
        1 * talon.setExpiration(MotorSafety.DEFAULT_SAFETY_EXPIRATION)
        1 * talon.configPeakCurrentLimit(0, TIMEOUT_MS)
        0 * talon._

    }
}
