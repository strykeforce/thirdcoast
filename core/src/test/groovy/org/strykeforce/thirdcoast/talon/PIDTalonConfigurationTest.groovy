package org.strykeforce.thirdcoast.talon

import com.moandjiezana.toml.Toml
import spock.lang.Shared

import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.CTRE_MagEncoder_Relative

class PIDTalonConfigurationTest extends TalonConfigurationInteractions {

    static tomlString = '''
    [[TALON]]
    name = "pid"
    mode = "Position"
    setpointMax = 1.0
    voltageCompSaturation = 11.0
    closedLoopRampRate = 27.67
    forwardOutputVoltagePeak = 0.5
    reverseOutputVoltagePeak = -0.3
    forwardOutputVoltageNominal = 0.1
    reverseOutputVoltageNominal = -0.2
    allowableClosedLoopError = 10
    nominalClosedLoopVoltage = 12.0
    pGain = 0.1
    iGain = 0.2
    dGain = 0.3
    fGain = 0.4
    iZone = 50
    [TALON.encoder]
    device = "CTRE_MagEncoder_Relative"

    [[TALON]]
    name = "speed"
    mode = "Velocity"
    setpointMax = 1.0
    
    [[TALON]]
    name = "pid_defaults"
    mode = "Velocity"
    setpointMax = 1.0

    [[TALON]]
    name = "motion_magic"
    mode = "MotionMagic"
    motionMagicAcceleration = 27
    motionMagicCruiseVelocity = 67
'''

    def talon = Mock(ThirdCoastTalon)
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
        t.encoder.device == CTRE_MagEncoder_Relative
        // FIXME: implement outputVoltage parms
        t.voltageCompSaturation == 11.0
        t.closedLoopRampRate == 27.67
        t.forwardOutputVoltagePeak == 0.5
        t.reverseOutputVoltagePeak == -0.3
        t.forwardOutputVoltageNominal == 0.1
        t.reverseOutputVoltageNominal == -0.2
        t.allowableClosedLoopError == 10
        t.PGain == 0.1
        t.IGain == 0.2
        t.DGain == 0.3
        t.FGain == 0.4
        t.IZone == 50
    }

    def "configures default parameters"() {
        when:
        def t = (PIDTalonConfiguration) provisioner.configurationFor("pid_defaults")
        t.configure(talon)

        then:
        t.voltageCompSaturation == null
        t.closedLoopRampRate == null
        t.forwardOutputVoltagePeak == null
        t.reverseOutputVoltagePeak == null
        t.forwardOutputVoltageNominal == null
        t.reverseOutputVoltageNominal == null
        t.allowableClosedLoopError == null
        t.nominalClosedLoopVoltage == null

        and:
        interaction {
            defaultTalonInteraction(talon)
            1 * talon.changeControlMode(Velocity)
            1 * talon.configAllowableClosedloopError(0, 0, TIMEOUT)
            1 * talon.config_kP(0, 0d, TIMEOUT)
            1 * talon.config_kI(0, 0d, TIMEOUT)
            1 * talon.config_kD(0, 0d, TIMEOUT)
            1 * talon.config_kF(0, 0d, TIMEOUT)
            1 * talon.config_IntegralZone(0, 0, TIMEOUT)
            1 * talon.configClosedloopRamp(0, TIMEOUT)
            1 * talon.configPeakOutputForward(1d, TIMEOUT)
            1 * talon.configPeakOutputReverse(-1d, TIMEOUT)
            1 * talon.configNominalOutputForward(0d, TIMEOUT)
            1 * talon.configNominalOutputReverse(0d, TIMEOUT)

            0 * talon._
        }
    }

    def "configures PID position mode talon"() {
        when:
        def t = provisioner.configurationFor("pid")
        t.configure(talon)

        then:
        interaction {
            defaultNeutralModeInteractions(talon)
            defaultInvertedInteractions(talon)
            defaultOpenLoopRampInteractions(talon)
            defaultLimitSwitchInteractions(talon)
            defaultSoftLimitInteractions(talon)
            defaultCurrentLimitInteractions(talon)
            defaultProfileSlotInteractions(talon)
            defaultVelocityMeasurementInteractions(talon)

            selectedFeedbackSensorInteraction(talon, CTRE_MagEncoder_Relative, false)
            1 * talon.configVoltageCompSaturation(11.0d, TIMEOUT)
            1 * talon.configClosedloopRamp(27.67d, TIMEOUT)
            1 * talon.configPeakOutputForward(0.5d, TIMEOUT)
            1 * talon.configPeakOutputReverse(-0.3d, TIMEOUT)
            1 * talon.configNominalOutputForward(0.1d, TIMEOUT)
            1 * talon.configNominalOutputReverse(-0.2d, TIMEOUT)
            1 * talon.configAllowableClosedloopError(0, 10, TIMEOUT)
            1 * talon.config_kP(0, 0.1d, TIMEOUT)
            1 * talon.config_kI(0, 0.2d, TIMEOUT)
            1 * talon.config_kD(0, 0.3d, TIMEOUT)
            1 * talon.config_kF(0, 0.4d, TIMEOUT)
            1 * talon.config_IntegralZone(0, 50, TIMEOUT)
        }
    }

    def "configures speed mode talon"() {
        when:
        def t = (SpeedTalonConfiguration) provisioner.configurationFor("speed")

        then:
        t.name == "speed"
        t.class == SpeedTalonConfiguration
    }

    def "configures motion magic mode talon"() {
        when:
        def t = provisioner.configurationFor("motion_magic")
        t.configure(talon)

        then:
        t.name == "motion_magic"
        t.class == MotionMagicTalonConfiguration

        and:
        1 * talon.configMotionAcceleration(27, TIMEOUT)
        1 * talon.configMotionCruiseVelocity(67, TIMEOUT)
    }

}
