package org.strykeforce.telemetry.measurable

import com.ctre.phoenix6.hardware.Pigeon2
import jdk.jfr.Description

//internal const val ROLL = "ROLL"
//internal const val PITCH = "PITCH"
//internal const val YAW = "YAW"
internal const val QUATW = "QUAT_W"
internal const val QUATX = "QUAT_X"
internal const val QUATY = "QUAT_Y"
internal const val QUATZ = "QUAT_Z"
internal const val GRAV_X = "GRAV_X"
internal const val GRAV_Y = "GRAV_Y"
internal const val GRAV_Z = "GRAV_Z"
internal const val TEMP = "TEMP"
internal const val NO_MOT_EN = "NO_MOT_EN"
internal const val NO_MOT_CNT = "NO_MOT_CNT"
internal const val UP_TIME = "UP_TIME"
internal const val ACCUM_X = "ACCUM_X"
internal const val ACCUM_Y = "ACCUM_Y"
internal const val ACCUM_Z = "ACCUM_Z"
internal const val ANG_VEL_X = "ANG_VEL_X"
internal const val ANG_VEL_Y = "ANG_VEL_Y"
internal const val ANG_VEL_Z = "ANG_VEL_Z"
internal const val ACCEL_X = "ACCEL_X"
internal const val ACCEL_Y = "ACCEL_Y"
internal const val ACCEL_Z = "ACCEL_Z"
internal const val SUPPLY_V = "SUPPLY_V"
internal const val ANG_VEL_WORLD_X = "ANG_VEL_WORLD_X"
internal const val ANG_VEL_WORLD_Y = "ANG_VEL_WORLD_Y"
internal const val ANG_VEL_WORLD_Z = "ANG_VEL_WORLD_Z"
internal const val MAG_X = "MAG_X"
internal const val MAG_Y = "MAG_Y"
internal const val MAG_Z = "MAG_Z"
internal const val IS_PRO = "IS_PRO"
internal const val RATE = "RATE"
//internal const val ANGLE = "ANGLE"
internal const val ROTATION2D = "ROTATION2D"
internal const val ROTATION3D = "ROTATION3D"
class Pigeon2Measureable @JvmOverloads constructor(
    private val pigeon2: Pigeon2,
    override val description: String = "Pigeon2 ${pigeon2.deviceID}"
): Measurable {

    override val deviceId = pigeon2.deviceID
    override val measures = setOf(
        Measure(ROLL, "Roll") {pigeon2.roll.valueAsDouble},
        Measure(PITCH, "Pitch") {pigeon2.pitch.valueAsDouble},
        Measure(YAW, "Yaw") {pigeon2.yaw.valueAsDouble},
        Measure(QUATW, "Quat W") {pigeon2.quatW.valueAsDouble},
        Measure(QUATX, "Quat X") {pigeon2.quatX.valueAsDouble},
        Measure(QUATY, "Quat Y") {pigeon2.quatY.valueAsDouble},
        Measure(QUATZ, "Quat Z") {pigeon2.quatZ.valueAsDouble},
        Measure(GRAV_X, "Gravity X") {pigeon2.gravityVectorX.valueAsDouble},
        Measure(GRAV_Y, "Gravity Y") { pigeon2.gravityVectorY.valueAsDouble},
        Measure(GRAV_Z, "Gravity Z") { pigeon2.gravityVectorZ.valueAsDouble},
        Measure(TEMP, "Temperature") { pigeon2.temperature.valueAsDouble},
        Measure(NO_MOT_EN, "No Motion Enabled") { pigeon2.noMotionEnabled.valueAsDouble},
        Measure(NO_MOT_CNT, "No Motion Count") { pigeon2.noMotionCount.valueAsDouble},
        Measure(UP_TIME, "Up Time") { pigeon2.upTime.valueAsDouble},
        Measure(ACCUM_X, "Accumulator X") { pigeon2.accumGyroX.valueAsDouble},
        Measure(ACCUM_Y, "Accumulator Y") {pigeon2.accumGyroY.valueAsDouble},
        Measure(ACCUM_Z, "Accumulator Z") { pigeon2.accumGyroZ.valueAsDouble},
        Measure(ANG_VEL_X, "Angular Vel X") {pigeon2.angularVelocityXDevice.valueAsDouble},
        Measure(ANG_VEL_Y, "Angular Vel Y") {pigeon2.angularVelocityYDevice.valueAsDouble},
        Measure(ANG_VEL_Z, "Angular Vel Z") {pigeon2.angularVelocityZDevice.valueAsDouble},
        Measure(ACCEL_X, "Acceleration X") {pigeon2.accelerationX.valueAsDouble},
        Measure(ACCEL_Y, "Acceleration Y") {pigeon2.accelerationY.valueAsDouble},
        Measure(ACCEL_Z, "Acceleration Z") {pigeon2.accelerationZ.valueAsDouble},
        Measure(SUPPLY_V, "Supply Voltage") {pigeon2.supplyVoltage.valueAsDouble},
        Measure(ANG_VEL_WORLD_X, "Angular Vel World X") {pigeon2.angularVelocityXWorld.valueAsDouble},
        Measure(ANG_VEL_WORLD_Y, "Angular Vel World Y") {pigeon2.angularVelocityYWorld.valueAsDouble},
        Measure(ANG_VEL_WORLD_Z, "Angular Vel World Z") {pigeon2.angularVelocityZWorld.valueAsDouble},
        Measure(MAG_X, "Mag Field X") {pigeon2.magneticFieldX.valueAsDouble},
        Measure(MAG_Y, "Mag Field Y") {pigeon2.magneticFieldY.valueAsDouble},
        Measure(MAG_Z, "Mag Field Z") {pigeon2.magneticFieldZ.valueAsDouble},
        Measure(IS_PRO, "Is Pro Lic") {pigeon2.isProLicensed.valueAsDouble},
        Measure(RATE, "Rate") {pigeon2.rate},
        Measure(ANGLE, "Angle") {pigeon2.angle},
        Measure(ROTATION2D, "Rotation2d Deg") {pigeon2.rotation2d.degrees}
    )

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false

        other as Pigeon2
        if(deviceId != other.deviceID) return false
        return true
    }

    override fun hashCode() = deviceId
}