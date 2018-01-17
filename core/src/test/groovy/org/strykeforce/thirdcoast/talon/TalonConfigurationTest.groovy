package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.motorcontrol.NeutralMode

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder
import static com.ctre.phoenix.motorcontrol.VelocityMeasPeriod.Period_5Ms
import static org.strykeforce.thirdcoast.talon.TalonConfiguration.TIMEOUT_MS

class TalonConfigurationTest extends TalonConfigurationInteractions {

    def talon = Mock(ThirdCoastTalon)
    def tcb = new TalonConfigurationBuilder()

    def "sets defaults"() {
        when:
        def tc = tcb.build()
        tc.configure(talon)

        then:
        interaction {
            defaultControlModeInteractions(talon)
            defaultTalonInteraction(talon)
            0 * talon._
        }
    }

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
        interaction {
            defaultProfileSlotInteractions(talon)
            defaultVoltageCompensationInteractions(talon)
            defaultOpenLoopRampInteractions(talon)
            selectedFeedbackSensorInteraction(talon, QuadEncoder, true)
            velocityMeasurementInteractions(talon, Period_5Ms, 16)
            currentLimitInteractions(talon, 50, 0)
            limitSwitchInteractions(talon, true, null)
            softLimitInteractions(talon, 10_000, 12_000)
            1 * talon.setNeutralMode(NeutralMode.Brake)
            1 * talon.setInverted(true)
            1 * talon.changeControlMode(PercentOutput)
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
}
