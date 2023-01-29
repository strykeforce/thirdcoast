package org.strykeforce.telemetry.measurable
import com.revrobotics.AbsoluteEncoder
import com.revrobotics.CANSparkMax
import com.revrobotics.RelativeEncoder
import com.revrobotics.SparkMaxAbsoluteEncoder
import com.revrobotics.SparkMaxAnalogSensor
import com.revrobotics.SparkMaxAnalogSensor.Mode
import com.revrobotics.SparkMaxLimitSwitch
import com.revrobotics.SparkMaxPIDController

internal const val OUTPUT_CURRENT = "OUTPUT_CURRENT"
internal const val APPLIED_OUTPUT = "APPLIED_OUTPUT"
internal const val SPEED_SETPOINT = "SPEED_SETPOINT"
internal const val ENCODER_POSITION = "ENCODER_POSITION"
internal const val  ENCODER_VELOCITY = "ENCODER_VELOCIT"
internal const val INPUT_VOLTAGE = "BUS_VOLTAGE"
internal const val I_ACCUMULATOR = "INTEGRAL_ACCUMULATOR"
internal const val ANALOG_POSITION = "ANALOG_POSITION"
internal const val  ANALOG_VELOCITY = "ANALOG_VELOCITY"
internal const val ANALOG_VOLTAGE = "ANALOG_VOLTAGE"
internal const val ABS_ENCODER_POSITION = "ABS_ENCODER_POSITION"
internal const val ABS_ENCODER_VELOCITY = "ABS_ENCODER_VELOCITY"
internal const val FWD_LIMIT_SWITCH_CLOSED = "FWD_LIMIT_SWITCH_CLOSED"
internal const val REV_LIMIT_SWITCH_CLOSED = "REV_LIMIT_SWITCH_CLOSED"
internal const val MOTOR_TEMP = "MOTOR_TEMP"
internal const val OPEN_LOOP_RAMP = "OPEN_LOOP_RAMP"
internal const val CLOSED_LOOP_RAMP = "CLOSED_LOOP_RAMP"
internal const val FWD_SOFT_LIMIT = "FWD_SOFT_LIMIT"
internal const val REV_SOFT_LIMIT = "REV_SOFT_LIMIT"
internal const val FWD_SOFT_LIMIT_EN = "FWD_SOFT_LIMIT_EN"
internal const val REV_SOFT_LIMIT_EN = "REV_SOFT_LIMIT_EN"
internal const val VOLTAGE_COMP_NOMINAL = "VOLTAGE_COMP_NOMINAL"
internal const val IS_FOLLOWER = "IS_FOLLOWER"
internal const val IS_INVERTED = "IS_INVERTED"
internal const val ENCODER_AVG_DEPTH = "ENCODER_AVG_DEPTH"
internal const val ENCODER_COUNTS_PER_REV = "ENCODER_COUNTS_PER_REV"
internal const val ENCODER_INVERTED = "ENCODER_INVERTED"
internal const val ENCODER_MEAS_PERIOD = "ENCODER_MEAS_PERIOD"
internal const val ENCODER_POSITION_CONV_FACTOR = "ENCODER_POSITION_CONV_FACTOR"
internal const val ENCODER_VELOCITY_CONV_FACTOR = "ENCODER_VELOCITY_CONV_FACTOR"
internal const val ABS_ENCODER_AVG_DEPTH = "ABS_ENCODER_AVG_DEPTH"
internal const val ABS_ENCODER_INVERTED = "ABS_ENCODER_INVERTED"
internal const val ABS_ENCODER_POS_CONV_FACTOR = "ABS_ENCODER_POS_CONV_FACTOR"
internal const val ABS_ENCODER_VEL_CONV_FACTOR  = "ABS_ENCODER_VEL_CONV_FACTOR"
internal const val ABS_ENCODER_ZERO_OFFSET = "ABS_ENCODER_ZERO_OFFSET"
internal const val ANALOG_INVERTED = "ANALOG_INVERTED"
internal const val ANALOG_POS_CONV_FACTOR = "ANALOG_POS_CONV_FACTOR"
internal const val ANALOG_VEL_CONV_FACTOR = "ANALOG_VEL_CONV_FACTOR"
internal const val PID_P = "PID_P"
internal const val PID_I = "PID_I"
internal const val PID_D = "PID_D"
internal const val PID_F = "PID_F"
internal const val PID_IZONE = "PID_IZONE"
internal const val PID_MAX_I_ACCUM = "PID_MAX_IACCUM"
internal const val PID_MAX_OUTPUT = "PID_MAX_OUTPUT"
internal const val PID_MIN_OUTPUT = "PID_MIN_OUTPUT"
internal const val PID_POS_WRAP_ENABLE = "PID_POS_WRAP_ENABLE"
internal const val PID_POS_WRAP_MAX = "PID_POS_WRAP_MAX"
internal const val PID_POS_WRAP_MIN = "PID_POS_WRAP_MIN"
internal const val SMART_MOTION_MAX_CLOSED_LOOP_ERR = "SMART_MOTION_MAX_CLOSED_LOOP_ERROR"
internal const val SMART_MOTION_MAX_VELOCITY = "SMART_MOTION_MAX_VELOCITY"
internal const val SMART_MOTION_MAX_ACCEL = "SMART_MOTION_MAX_ACCEL"
internal const val SMART_MOTION_MIN_VELOCITY = "SMART_MOTION_MIN_VELOCITY"

class SparkMaxMeasureable @JvmOverloads constructor(
    private val sparkMax: CANSparkMax,
    override val description: String = "SparkMax ${sparkMax.deviceId}"
): Measurable {
    private val encoder: RelativeEncoder = sparkMax.encoder
    private val absEncoder: AbsoluteEncoder = sparkMax.getAbsoluteEncoder(SparkMaxAbsoluteEncoder.Type.kDutyCycle)
    private val analog: SparkMaxAnalogSensor = sparkMax.getAnalog(Mode.kAbsolute)
    private val pid: SparkMaxPIDController = sparkMax.pidController
    private val fwdLim: SparkMaxLimitSwitch = sparkMax.getForwardLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen)
    private val revLim: SparkMaxLimitSwitch = sparkMax.getReverseLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen)
    override val deviceId = sparkMax.deviceId
    override val measures = setOf(
        Measure(OUTPUT_CURRENT, "Output Current") {sparkMax.outputCurrent},
        Measure(APPLIED_OUTPUT, "Applied Output"){sparkMax.appliedOutput},
        Measure(SPEED_SETPOINT, "Speed Setpoint"){sparkMax.get()},
        Measure(ENCODER_POSITION, "Encoder Position") {encoder.position},
        Measure(ENCODER_VELOCITY, "Encoder Velocity"){encoder.velocity},
        Measure(INPUT_VOLTAGE, "Bus Voltage"){sparkMax.busVoltage},
        Measure(I_ACCUMULATOR, "Integral Accumulator"){pid.iAccum},
        Measure(ANALOG_POSITION, "Analog Position"){analog.position},
        Measure(ANALOG_VELOCITY, "Analog Velocity"){analog.velocity},
        Measure(ANALOG_VOLTAGE, "Analog Voltage"){analog.voltage},
        Measure(ABS_ENCODER_POSITION, "Absolute Position"){absEncoder.position},
        Measure(ABS_ENCODER_VELOCITY, "Absolute Velocity"){absEncoder.velocity},
        Measure(FWD_LIMIT_SWITCH_CLOSED, "FWD Limit Switch Closed"){if(fwdLim.isPressed) 1.0 else 0.0},
        Measure(REV_LIMIT_SWITCH_CLOSED, "REV Limit Switch Closed"){if(revLim.isPressed) 1.0 else 0.0},
        Measure(MOTOR_TEMP, "Motor Temp"){sparkMax.motorTemperature},
        Measure(OPEN_LOOP_RAMP, "Open Loop Ramp"){sparkMax.openLoopRampRate},
        Measure(CLOSED_LOOP_RAMP, "Closed Loop Ramp"){sparkMax.closedLoopRampRate},
        Measure(FWD_SOFT_LIMIT, "FWD Soft Limit"){sparkMax.getSoftLimit(CANSparkMax.SoftLimitDirection.kForward)},
        Measure(REV_SOFT_LIMIT, "REV Soft Limit"){sparkMax.getSoftLimit(CANSparkMax.SoftLimitDirection.kReverse)},
        Measure(FWD_SOFT_LIMIT_EN, "FWD Soft Limit Enabled"){if(sparkMax.isSoftLimitEnabled(CANSparkMax.SoftLimitDirection.kForward)) 1.0 else 0.0},
        Measure(REV_SOFT_LIMIT_EN, "REV Soft Limit Enabled"){if(sparkMax.isSoftLimitEnabled(CANSparkMax.SoftLimitDirection.kReverse)) 1.0 else 0.0},
        Measure(VOLTAGE_COMP_NOMINAL, "Voltage Compensation Nominal"){sparkMax.voltageCompensationNominalVoltage},
        Measure(IS_FOLLOWER, "Is Follower"){if(sparkMax.isFollower) 1.0 else 0.0},
        Measure(IS_INVERTED, "Is Inverted"){if(sparkMax.inverted) 1.0 else 0.0},
        Measure(ENCODER_AVG_DEPTH, "Encoder Avg Depth"){encoder.averageDepth.toDouble()},
        Measure(ENCODER_COUNTS_PER_REV, "Encoder Counts per Rev"){encoder.countsPerRevolution.toDouble()},
        Measure(ENCODER_INVERTED, "Encoder Inverted"){if(encoder.inverted) 1.0 else 0.0},
        Measure(ENCODER_MEAS_PERIOD, "Encoder Measurement Period"){encoder.measurementPeriod.toDouble()},
        Measure(ENCODER_POSITION_CONV_FACTOR, "Encoder Position Conversion Factor"){encoder.positionConversionFactor},
        Measure(ENCODER_VELOCITY_CONV_FACTOR, "Encoder Velocity Conversion Factor"){encoder.velocityConversionFactor},
        Measure(ABS_ENCODER_AVG_DEPTH, "ABS Encoder Avg Depth"){absEncoder.averageDepth.toDouble()},
        Measure(ABS_ENCODER_INVERTED, "ABS Encoder Inverted"){if(absEncoder.inverted) 1.0 else 0.0},
        Measure(ABS_ENCODER_POS_CONV_FACTOR, "ABS Encoder Position Conversion Factor"){absEncoder.positionConversionFactor},
        Measure(ABS_ENCODER_VEL_CONV_FACTOR, "ABS Encoder Velocity Conversion Factor"){absEncoder.velocityConversionFactor},
        Measure(ABS_ENCODER_ZERO_OFFSET, "ABS Encoder Zero Offset"){absEncoder.zeroOffset},
        Measure(ANALOG_POS_CONV_FACTOR, "Analog Position Conversion Factor"){analog.positionConversionFactor},
        Measure(ANALOG_VEL_CONV_FACTOR, "Analog Velocity Conversion Factor"){analog.velocityConversionFactor},
        Measure(ANALOG_INVERTED, "Analog Inverted"){if(analog.inverted) 1.0 else 0.0},
        Measure(PID_P, "PID P"){pid.p},
        Measure(PID_I, "PID I"){pid.i},
        Measure(PID_D, "PID D"){pid.d},
        Measure(PID_F, "PID FF"){pid.ff},
        Measure(PID_IZONE, "PID Izone"){pid.iZone},
        Measure(PID_MAX_I_ACCUM, "PID Max Integral Accumulator 0"){pid.getIMaxAccum(0)},
        Measure(PID_MAX_OUTPUT, "PID Max Output 0"){pid.getOutputMax(0)},
        Measure(PID_MIN_OUTPUT, "PID Min OUtput 0"){pid.getOutputMin(0)},
        Measure(PID_POS_WRAP_ENABLE, "PID Wrap Enabled"){if(pid.positionPIDWrappingEnabled) 1.0 else 0.0},
        Measure(PID_POS_WRAP_MAX, "PID Wrap Max"){pid.positionPIDWrappingMaxInput},
        Measure(PID_POS_WRAP_MIN, "PID Wrap Min"){pid.positionPIDWrappingMinInput},
        Measure(SMART_MOTION_MAX_CLOSED_LOOP_ERR, "Smart Motion Max Closed Loop Err 0"){pid.getSmartMotionAllowedClosedLoopError(0)},
        Measure(SMART_MOTION_MAX_ACCEL, "Smart Motion Max Accel 0"){pid.getSmartMotionMaxAccel(0)},
        Measure(SMART_MOTION_MAX_VELOCITY, "Smart Motion Max Velocity 0"){pid.getSmartMotionMaxVelocity(0)},
        Measure(SMART_MOTION_MIN_VELOCITY, "Smart Motion Min Velocity"){pid.getSmartMotionMinOutputVelocity(0)}
    )

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false
        other as SparkMaxMeasureable
        if(deviceId != other.deviceId) return false
        return true
    }

    override fun hashCode() = deviceId

}