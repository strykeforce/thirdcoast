package org.strykeforce.sidewinder.talon

import com.ctre.CANTalon
import spock.lang.Specification

class TalonParametersTest extends Specification {

    def talon = Mock(CANTalon)

    void setupSpec() {
        TalonParameters.register("testdata/talons.toml")
    }

    def "handles missing parameters file"() {
        when:
        TalonParameters.register("/missing.toml")

        then:
        thrown(IllegalArgumentException)
    }

    def "reads talon parameters"() {
        when:
        def t = TalonParameters.getInstance("test")

        then:
        t.name == "test"
        t.encoder.feedbackDevice == CANTalon.FeedbackDevice.QuadEncoder;
        t.encoder.reversed
        t.encoder.unitScalingEnabled
        t.encoder.ticksPerRevolution == 360
        t.brakeInNeutral
        t.forwardLimitSwitch.enabled
        t.forwardLimitSwitch.normallyOpen
        !t.reverseLimitSwitch.enabled
        t.forwardSoftLimit.enabled
        t.forwardSoftLimit.value == 10000
        t.reverseSoftLimit.enabled
        t.reverseSoftLimit.value == 12000
        t.outputReversed
        t.velocityMeasurementPeriod == CANTalon.VelocityMeasurementPeriod.Period_5Ms
        t.velocityMeasurementWindow == 16
        t.currentLimit == 50
    }

    def "configures voltage mode talon"() {
        when:
        def t = TalonParameters.getInstance("test")
        t.configure(talon)

        then:
        with (talon) {
            1 * changeControlMode(CANTalon.TalonControlMode.Voltage)
            1 * setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder)
            1 * enableBrakeMode(true)
            1 * reverseSensor(true)
            1 * reverseOutput(true)
            1 * SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_5Ms)
            1 * SetVelocityMeasurementWindow(16)
            1 * enableLimitSwitch(true, false)
            1 * ConfigFwdLimitSwitchNormallyOpen(true)
            0 * ConfigRevLimitSwitchNormallyOpen(true)
            0 * ConfigRevLimitSwitchNormallyOpen(false)
            1 * enableForwardSoftLimit(true)
            1 * setForwardSoftLimit(10_000.0)
            1 * enableReverseSoftLimit(true)
            1 * setReverseSoftLimit(12_000.0)
        }
    }

    def "handles missing name"() {
        when:
        TalonParameters.register("testdata/no_name.toml")

        then:
        IllegalArgumentException e = thrown()
        e.message == "TALON configuration name parameter missing"
    }

    def "handles missing setpoint_max"() {
        when:
        TalonParameters.register("testdata/no_setpoint_max.toml")

        then:
        IllegalArgumentException e = thrown()
        e.message == "TALON setpoint_max parameter missing in: missing_required_setpoint_max"
    }

    def "sets defaults"() {
        when:
        def t = TalonParameters.getInstance("all_defaults")

        then:
        t.class == VoltageTalonParameters
        t.name == "all_defaults"
        t.encoder.feedbackDevice == CANTalon.FeedbackDevice.QuadEncoder;
        !t.encoder.unitScalingEnabled
        !t.encoder.reversed
        t.brakeInNeutral
        !t.forwardLimitSwitch.enabled
        !t.reverseLimitSwitch.enabled
        !t.forwardSoftLimit.enabled
        !t.reverseSoftLimit.enabled
        !t.outputReversed
        t.velocityMeasurementPeriod == CANTalon.VelocityMeasurementPeriod.Period_100Ms
        t.velocityMeasurementWindow == 64
        t.currentLimit == 0
    }

    def "handles file when name is emoji"() {
        when:
        def t = TalonParameters.getInstance("😀🕹")

        then:
        t.name == "😀🕹"
        t.encoder.feedbackDevice == CANTalon.FeedbackDevice.QuadEncoder;
    }

    def "handles truncate_velocity_measurement_window 100"() {
        when:
        def t = TalonParameters.getInstance("truncate_velocity_measurement_window_100")

        then:
        t.name == "truncate_velocity_measurement_window_100"
        t.velocityMeasurementWindow == 64
    }

    def "handles truncate_velocity_measurement_window 3"() {
        when:
        def t = TalonParameters.getInstance("truncate_velocity_measurement_window_3")

        then:
        t.name == "truncate_velocity_measurement_window_3"
        t.velocityMeasurementWindow == 2
    }

    def "non-allowed velocity measurement period"() {
        when:
        TalonParameters.register("testdata/bad_velocity_measurement_period.toml")

        then:
        IllegalArgumentException e = thrown()
        e.message == "TALON velocity_measurement_period invalid: 51"
    }
}
