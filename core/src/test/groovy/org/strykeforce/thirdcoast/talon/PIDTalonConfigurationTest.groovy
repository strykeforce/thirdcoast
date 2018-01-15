package org.strykeforce.thirdcoast.talon

import com.ctre.phoenix.ErrorCode
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod
import com.moandjiezana.toml.Toml
import edu.wpi.first.wpilibj.MotorSafety
import spock.lang.Shared
import spock.lang.Specification

import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.CTRE_MagEncoder_Relative
import static com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder
import static org.strykeforce.thirdcoast.talon.TalonConfiguration.TIMEOUT_MS

class PIDTalonConfigurationTest extends Specification {

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
        1 * talon.enableVoltageCompensation(true)
        1 * talon.configSelectedFeedbackSensor(QuadEncoder, 0, TIMEOUT_MS) >> ErrorCode.OK
        1 * talon.configVoltageCompSaturation(12.0d, TIMEOUT_MS)
        1 * talon.configAllowableClosedloopError(0, 0, TIMEOUT_MS)
        1 * talon.config_kP(0, 0d, TIMEOUT_MS)
        1 * talon.config_kI(0, 0d, TIMEOUT_MS)
        1 * talon.config_kD(0, 0d, TIMEOUT_MS)
        1 * talon.config_kF(0, 0d, TIMEOUT_MS)
        1 * talon.config_IntegralZone(0, 0, TIMEOUT_MS)
        1 * talon.configClosedloopRamp(0, TIMEOUT_MS)
        1 * talon.configPeakOutputForward(1d, TIMEOUT_MS)
        1 * talon.configPeakOutputReverse(-1d, TIMEOUT_MS)
        1 * talon.configNominalOutputForward(0d, TIMEOUT_MS)
        1 * talon.configNominalOutputReverse(0d, TIMEOUT_MS)

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
        1 * talon.changeControlMode(Velocity)
        1 * talon.setExpiration(MotorSafety.DEFAULT_SAFETY_EXPIRATION)
        1 * talon.configPeakCurrentLimit(0, TIMEOUT_MS)
        0 * talon._


    }

    def "configures PID position mode talon"() {
        when:
        def t = provisioner.configurationFor("pid")
        t.configure(talon)

        then:
        1 * talon.configSelectedFeedbackSensor(CTRE_MagEncoder_Relative, 0, TIMEOUT_MS)
        1 * talon.configVoltageCompSaturation(11.0d, TIMEOUT_MS)
        1 * talon.configClosedloopRamp(27.67d, TIMEOUT_MS)
        1 * talon.configPeakOutputForward(0.5d, TIMEOUT_MS)
        1 * talon.configPeakOutputReverse(-0.3d, TIMEOUT_MS)
        1 * talon.configNominalOutputForward(0.1d, TIMEOUT_MS)
        1 * talon.configNominalOutputReverse(-0.2d, TIMEOUT_MS)
        1 * talon.configAllowableClosedloopError(0, 10, TIMEOUT_MS)
        1 * talon.config_kP(0, 0.1d, TIMEOUT_MS)
        1 * talon.config_kI(0, 0.2d, TIMEOUT_MS)
        1 * talon.config_kD(0, 0.3d, TIMEOUT_MS)
        1 * talon.config_kF(0, 0.4d, TIMEOUT_MS)
        1 * talon.config_IntegralZone(0, 50, TIMEOUT_MS)
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
        1 * talon.configMotionAcceleration(27, TIMEOUT_MS)
        1 * talon.configMotionCruiseVelocity(67, TIMEOUT_MS)
    }

}
