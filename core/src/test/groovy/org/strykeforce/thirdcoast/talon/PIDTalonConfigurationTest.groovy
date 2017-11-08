package org.strykeforce.thirdcoast.talon

import com.ctre.CANTalon
import com.moandjiezana.toml.Toml
import spock.lang.Shared
import spock.lang.Specification

class PIDTalonConfigurationTest extends Specification {

    static tomlString = '''
    [[TALON]]
    name = "pid"
    mode = "Position"
    setpointMax = 1.0
#    output_voltage_max = 12.0
#    forward_output_voltage_peak = 5.0
#    reverse_output_voltage_peak = -3.0
#    forward_output_voltage_nominal = 0.5
#    reverse_output_voltage_nominal = -0.3
    allowableClosedLoopError = 10
    nominalClosedLoopVoltage = 12.0
    pGain = 0.1
    iGain = 0.2
    dGain = 0.3
    fGain = 0.4
    iZone = 50
    [TALON.encoder]
    device = "CtreMagEncoder_Relative"

    [[TALON]]
    name = "speed"
    mode = "Speed"
    setpointMax = 1.0
    
    [[TALON]]
    name = "pid_defaults"
    mode = "Speed"
    setpointMax = 1.0

'''

    def talon = Mock(CANTalon)
    @Shared
    TalonProvisioner provisioner

    void setupSpec() {
        File temp = File.createTempFile("thirdcoast_", ".toml")
        temp.delete()
        temp.deleteOnExit()
        def toml = new Toml().read(tomlString)
        provisioner = new TalonProvisioner(temp)
        provisioner.addConfigurations(toml)
    }

    def "reads talon parameters"() {
        when:
        def t = (PIDTalonConfiguration) provisioner.configurationFor("pid")

        then:
        t.name == "pid"
        t.class == PositionTalonConfiguration
        t.encoder.device == CANTalon.FeedbackDevice.CtreMagEncoder_Relative
        // FIXME: implement outputVoltage parms
//        t.outputVoltageMax == 12.0
//        t.forwardOutputVoltagePeak == 5.0
//        t.reverseOutputVoltagePeak == -3.0
//        t.forwardOutputVoltageNominal == 0.5
//        t.reverseOutputVoltageNominal == -0.3
        t.allowableClosedLoopError == 10
        t.pGain == 0.1
        t.iGain == 0.2
        t.dGain == 0.3
        t.fGain == 0.4
        t.iZone == 50
    }

    def "configures default parameters"() {
        when:
        def t = (PIDTalonConfiguration) provisioner.configurationFor("pid_defaults")

        then:
        t.outputVoltageMax == null
        t.forwardOutputVoltagePeak == null
        t.reverseOutputVoltagePeak == null
        t.forwardOutputVoltageNominal == null
        t.reverseOutputVoltageNominal == null
        t.allowableClosedLoopError == null
        t.nominalClosedLoopVoltage == null
    }

    def "configures PID position mode talon"() {
        when:
        def t = provisioner.configurationFor("pid")
        t.configure(talon)

        then:
        1 * talon.changeControlMode(CANTalon.TalonControlMode.Position)
        1 * talon.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        // FIXME: not implemented
//        1 * talon.configMaxOutputVoltage(12.0)
//        1 * talon.configPeakOutputVoltage(5.0, -3.0)
//        1 * talon.configNominalOutputVoltage(0.5, -0.3)
        1 * talon.setAllowableClosedLoopErr(10)
        1 * talon.setPID(0.1, 0.2, 0.3)
        1 * talon.setF(0.4)
        1 * talon.setIZone(50)
    }

    def "configures speed mode talon"() {
        when:
        def t = (SpeedTalonConfiguration) provisioner.configurationFor("speed")

        then:
        t.name == "speed"
        t.class == SpeedTalonConfiguration
    }

}
