package org.strykeforce.healthcheck.internal

import com.ctre.phoenix.motorcontrol.can.BaseTalon
import edu.wpi.first.wpilibj2.command.Subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase
import mu.KotlinLogging
import okhttp3.internal.toImmutableList
import org.strykeforce.healthcheck.Follow
import org.strykeforce.healthcheck.Position
import org.strykeforce.healthcheck.Timed
import org.strykeforce.swerve.SwerveDrive
import org.strykeforce.swerve.TalonSwerveModule
import java.lang.reflect.Field
import java.lang.reflect.Method

private val logger = KotlinLogging.logger {}

class RobotHealthCheckBuilder(vararg subsystems: Subsystem) {

    private val subsystemSet = subsystems.toList()

    fun build(): RobotHealthCheck {
        val subsystemHealthChecks = subsystemSet.map { SubsystemHealthCheckBuilder(it).build() }
        return RobotHealthCheck("Robot Health Check", subsystemHealthChecks)
    }
}

private val kHealthCheckAnnotationClass = org.strykeforce.healthcheck.HealthCheck::class.java
private val kBeforeHealthCheckAnnotationClass = org.strykeforce.healthcheck.BeforeHealthCheck::class.java
private val kAfterHealthCheckAnnotationClass = org.strykeforce.healthcheck.AfterHealthCheck::class.java

/** Get fields in subsystem that are annotated with `@HealthCheck`. The fields are sorted in order
 * of the `@HealthCheck` annotation's `order` parameter.  */
private fun Subsystem.healthCheckAnnotatedFields() =
    this.javaClass.declaredFields.filter { it.isAnnotationPresent(kHealthCheckAnnotationClass) }
        .sortedBy { it.getAnnotation(kHealthCheckAnnotationClass).order }
        .toMutableList()

/** Get methods in subsystem that are annotated with `@BeforeHealthCheck`. The methods are sorted in order
 * of the method's name. */
private fun Subsystem.beforeHealthCheckAnnotatedMethods() =
    this.javaClass.declaredMethods.filter { it.isAnnotationPresent(kBeforeHealthCheckAnnotationClass) }
        .sortedBy { it.getAnnotation(kBeforeHealthCheckAnnotationClass).order }

/** Get methods in subsystem that are annotated with `@AfterHealthCheck`. The methods are sorted in order
 * of the method's name. */
private fun Subsystem.afterHealthCheckAnnotatedMethods() =
    this.javaClass.declaredMethods.filter { it.isAnnotationPresent(kAfterHealthCheckAnnotationClass) }
        .sortedBy { it.getAnnotation(kAfterHealthCheckAnnotationClass).order }

class SubsystemHealthCheckBuilder(private val subsystem: Subsystem) {

    fun build(): SubsystemHealthCheck {

        val healthChecks = mutableListOf<HealthCheck>()

        // get all methods from this subsystem that are annotated with @BeforeHealthCheck
        val beforeHealthCheckMethods = subsystem.beforeHealthCheckAnnotatedMethods()

        // create LifecycleHealthChecks from annotated methods and prepend them to health checks for this subsystem
        healthChecks.addAll(beforeHealthCheckMethods.map { LifecycleHealthCheckBuilder(subsystem, it).build() })

        // get all fields from this subsystem that are annotated with @HealthCheck
        val healthCheckFields = subsystem.healthCheckAnnotatedFields()

        // will remove non-talon field(s), only expect SwerveDrive field
        val fieldsToRemove = mutableSetOf<Field>()

        // make each HealthCheck annotated field accessible or remove it if unable
        healthCheckFields.forEach {
            if (!it.trySetAccessible()) {
                logger.error { "$subsystem: trySetAccessible() failed for ${it.name}, removing!" }
                fieldsToRemove.add(it)
            }
        }
        healthCheckFields.removeAll(fieldsToRemove)

        // if a SwerveDrive field is annotated with HealthCheck,
        // create a TalonHealthCheck for each azimuth and drive talon and add to talonHealthChecks list
        // queue the SwerveDrive field for removal in fieldsToRemove
        healthCheckFields.forEach {
            if (it.type == SwerveDrive::class.java) {
                healthChecks.addAll(SwerveDriveHealthCheckBuilder(subsystem, it).build())
                fieldsToRemove.add(it)
            }
        }

        // remove all non-talon fields and create TalonHealthChecks from the remaining talon fields
        healthCheckFields.removeAll(fieldsToRemove)
        healthChecks.addAll(healthCheckFields.map { TalonHealthCheckBuilder(subsystem, it).build() })
        if (healthChecks.isEmpty()) logger.warn { "$subsystem: no health checks found" }

        // find any follower talon health checks and
        // add their talon to be measured to their associated leader health check
        // remove the follower health check since they will be run during leader health check
        val followerHealthChecks = healthChecks.mapNotNull { it as? TalonFollowerHealthCheck }
        for (fhc in followerHealthChecks) {
            val lhc = healthChecks.mapNotNull { it as? TalonHealthCheck }.find { it.talon.deviceID == fhc.leaderId }
            if (lhc == null) {
                logger.error { "$subsystem: no leader (id = ${fhc.leaderId}) found for follower (id = ${fhc.talon.deviceID})" }
                continue
            }
            // leader health check is a TalonTimedHealthCheck or TalonPositionHealthCheck
            // add follower talon to each of its child cases
            lhc.healthChecks.map { it as TalonHealthCheckCase }.forEach { it.addFollowerTalon(fhc.talon) }
        }
        healthChecks.removeAll(followerHealthChecks)


        // get all methods from this subsystem that are annotated with @AfterHealthCheck
        val afterHealthCheckMethods = subsystem.afterHealthCheckAnnotatedMethods()

        // create LifecycleHealthChecks from annotated methods and append them to health checks for this subsystem
        healthChecks.addAll(afterHealthCheckMethods.map { LifecycleHealthCheckBuilder(subsystem, it).build() })

        val name = (subsystem as? SubsystemBase)?.name ?: subsystem.toString()
        return SubsystemHealthCheck(name, healthChecks.toImmutableList())
    }
}

class LifecycleHealthCheckBuilder(private val subsystem: Subsystem, private val method: Method) {
    fun build(): HealthCheck {
        val subsystemName = (subsystem as? SubsystemBase)?.name ?: subsystem.toString()

        // this will throw an exception if the method annotated with @BeforeHealthCheck is
        // inaccessible. We would rather crash the robot program than run a health check that
        // is not set up properly.
        if (!method.trySetAccessible()) {
            val msg =
                "$subsystemName.${method.name}: inaccessible, crashing to prevent health check running without setup"
            logger.error { msg }
            throw IllegalStateException(msg)
        }

        // methods annotated with @BeforeHealthCheck should have no parameters and should
        // return boolean
        if (method.parameterCount != 0) {
            val msg = "$subsystemName.${method.name}: should have no parameters"
            logger.error { msg }
            throw IllegalArgumentException(msg)
        }

        if (method.returnType != Boolean::class.java) {
            val msg = "$subsystemName.${method.name}: should return a boolean"
            logger.error { msg }
            throw IllegalArgumentException(msg)
        }

        return LifecycleHealthCheck(subsystem, method)
    }
}

private const val AZIMUTH_LEADER_ID = 0
private const val DRIVE_LEADER_ID = 10

class SwerveDriveHealthCheckBuilder(private val subsystem: Subsystem, private val field: Field) {

    private val swerveDrive = field.get(subsystem) as? SwerveDrive
        ?: throw IllegalArgumentException("$subsystem: field '${field.name}' is not a SwerveDrive")

    fun build(): List<TalonHealthCheck> {
        val talonHealthChecks = mutableListOf<TalonHealthCheck>()

        var azimuthLeader: BaseTalon? = null
        var driveLeader: BaseTalon? = null
        val azimuthFollowers = mutableListOf<BaseTalon>()
        val driveFollowers = mutableListOf<BaseTalon>()

        // extract talons from swerve drive modules and
        // create TalonTimedHealthChecks for leader azimuth and drive talons
        // create TalonFollowerHealthCheck for remainder
        // follow associated leaders
        swerveDrive.swerveModules.forEach {
            val module =
                it as? TalonSwerveModule ?: throw IllegalArgumentException("$subsystem: $it is not TalonSwerveModule")
            val azimuth = module.azimuthTalon
            val drive = module.driveTalon

            if (azimuth.deviceID == AZIMUTH_LEADER_ID) {
                talonHealthChecks.add(TalonTimedHealthCheckBuilder(azimuth).build())
                azimuthLeader = azimuth
            } else {
                talonHealthChecks.add(TalonFollowerHealthCheck(azimuth, AZIMUTH_LEADER_ID))
                azimuthFollowers.add(azimuth)
            }

            if (drive.deviceID == DRIVE_LEADER_ID) {
                talonHealthChecks.add(TalonTimedHealthCheckBuilder(drive).build())
                driveLeader = drive
            } else {
                talonHealthChecks.add(TalonFollowerHealthCheck(drive, DRIVE_LEADER_ID))
                driveFollowers.add(drive)
            }
        }

        requireNotNull(azimuthLeader) { "$subsystem: swerve azimuth talon with id $AZIMUTH_LEADER_ID not found" }
        requireNotNull(driveLeader) { "$subsystem: swerve drive talon with id $DRIVE_LEADER_ID not found" }

        azimuthFollowers.forEach { it.follow(azimuthLeader) }
        driveFollowers.forEach { it.follow(driveLeader) }

        return talonHealthChecks
    }
}

class TalonHealthCheckBuilder(private val subsystem: Subsystem, private val field: Field) {

    val talon = field.get(subsystem) as? BaseTalon
        ?: throw IllegalArgumentException("$subsystem: field '${field.name}' is not a subclass of BaseTalon")

    fun build(): TalonHealthCheck {
        val timedAnnotation = field.getAnnotation(Timed::class.java)
        val positionAnnotation = field.getAnnotation(Position::class.java)
        val followAnnotation = field.getAnnotation(Follow::class.java)

        if (arrayListOf(timedAnnotation, positionAnnotation, followAnnotation).filterNotNull().count() > 1)
            throw IllegalArgumentException("$subsystem: only one of @Timed, @Position, or @Follow may be specified.")

        if (timedAnnotation != null) {
            val builder = TalonTimedHealthCheckBuilder(talon)
            builder.percentOutput = timedAnnotation.percentOutput
            builder.duration = timedAnnotation.duration
            return builder.build()
        }


        if (positionAnnotation != null) {
            val builder = TalonPositionHealthCheckBuilder(talon)
            builder.percentOutput = positionAnnotation.percentOutput
            builder.encoderChange = positionAnnotation.encoderChange
            return builder.build()
        }

        if (followAnnotation != null) return TalonFollowerHealthCheck(talon, followAnnotation.leader)

        // default to timed check if not specified
        return TalonTimedHealthCheckBuilder(talon).build()
    }
}

class TalonTimedHealthCheckBuilder(val talon: BaseTalon) {

    var percentOutput = doubleArrayOf(0.25, 0.5, 0.75, -0.25, -0.5, -0.75)
    var duration = 5.0
    var limits: DoubleArray? = null

    fun build(): TalonHealthCheck {
        val cases = percentOutput.let {
            var previousCase: TalonTimedHealthCheckCase? = null
            it.map { pctOut ->
                val case = TalonTimedHealthCheckCase(previousCase, talon, pctOut, (duration * 1e6).toLong())
                previousCase = case
                case
            }
        }

        return TalonTimedHealthCheck(talon, cases, percentOutput)
    }
}


class TalonPositionHealthCheckBuilder(val talon: BaseTalon) {
    var percentOutput = doubleArrayOf(0.25, -0.25)
    var encoderChange = 0
    var limits: DoubleArray? = null

    fun build(): TalonHealthCheck {
        if (encoderChange == 0) logger.warn { "Talon ${talon.deviceID}: position health check encoderChange is zero" }

        val cases = percentOutput.let {
            var previousCase: TalonPositionHealthCheckCase? = null
            it.map { pctOut ->
                val case = TalonPositionHealthCheckCase(previousCase, talon, pctOut, encoderChange)
                previousCase = case
                case
            }
        }

        return TalonPositionHealthCheck(talon, cases, percentOutput)
    }
}
