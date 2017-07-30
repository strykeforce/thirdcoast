package org.strykeforce.sidewinder.talon

import com.ctre.CANTalon
import spock.lang.Specification

class PIDTalonParametersTest extends Specification {

    def talon = Mock(CANTalon)

    void setupSpec() {
        TalonParameters.register("testdata/talons.toml")
    }

    def "reads talon parameters"() {
        when:
        def t = (PIDTalonParameters) TalonParameters.getInstance("pid")

        then:
        t.name == "pid"
        t.class == PositionTalonParameters
        t.encoder.feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Relative
        t.outputVoltageMax == 12.0
        t.forwardOutputVoltagePeak == 5.0
        t.reverseOutputVoltagePeak == -3.0
        t.forwardOutputVoltageNominal == 0.5
        t.reverseOutputVoltageNominal == -0.3
        t.allowableClosedLoopError == 10
        t.pGain == 0.1
        t.iGain == 0.2
        t.dGain == 0.3
        t.fGain == 0.4
        t.iZone == 50
    }

    def "configures default parameters"() {
        when:
        def t = (PIDTalonParameters) TalonParameters.getInstance("pid_defaults")

        then:
        t.outputVoltageMax == 0.0
        t.forwardOutputVoltagePeak == 0.0
        t.reverseOutputVoltagePeak == 0.0
        t.forwardOutputVoltageNominal == 0.0
        t.reverseOutputVoltageNominal == 0.0
        t.allowableClosedLoopError == 0
        t.nominalClosedLoopVoltage == 0.0
    }

    def "configures PID position mode talon"() {
        when:
        def t = TalonParameters.getInstance("pid")
        t.configure(talon)

        then:
        1 * talon.changeControlMode(CANTalon.TalonControlMode.Position)
        1 * talon.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        1 * talon.configMaxOutputVoltage(12.0)
        1 * talon.configPeakOutputVoltage(5.0, -3.0)
        1 * talon.configNominalOutputVoltage(0.5, -0.3)
        1 * talon.setAllowableClosedLoopErr(10)
        1 * talon.setPID(0.1, 0.2, 0.3)
        1 * talon.setF(0.4)
        1 * talon.setIZone(50)
    }

    def "configures speed mode talon"() {
        when:
        def t = (SpeedTalonParameters) TalonParameters.getInstance("speed")

        then:
        t.name == "speed"
        t.class == SpeedTalonParameters
    }

}
