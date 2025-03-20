package org.strykeforce.healthcheck.internal

import com.ctre.phoenix.motorcontrol.can.BaseTalon
import com.ctre.phoenix6.controls.Follower
import com.ctre.phoenix6.hardware.TalonFX
import com.ctre.phoenix6.hardware.TalonFXS
import edu.wpi.first.wpilibj2.command.Subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase
import mu.KotlinLogging
import okhttp3.internal.toImmutableList
import org.strykeforce.healthcheck.*
import org.strykeforce.swerve.FXSwerveModule
import org.strykeforce.swerve.SwerveDrive
import org.strykeforce.swerve.V6TalonSwerveModule
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.math.log

private val logger = KotlinLogging.logger {}

class RobotHealthCheckBuilder(vararg subsystems: Subsystem) {

    private val subsystemSet = subsystems.toList()

    fun build(): RobotHealthCheck {
        val subsystemHealthChecks = subsystemSet.map { SubsystemHealthCheckBuilder(it).build() }
        return RobotHealthCheck("Robot Health Check", subsystemHealthChecks)
    }
}

class RobotHealCheckIOBuilder(vararg ios: Checkable) {
    private val ioSet = ios.toList()

    fun build(): RobotIOHealthCheck {
        val ioHealthChecks = ioSet.map { IOHealthCheckBuilder(it).build() }
        return RobotIOHealthCheck("Robot Health Check", ioHealthChecks)
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

private fun Checkable.healthCheckAnnotatedFields() =
    this.javaClass.declaredFields.filter { it.isAnnotationPresent(kHealthCheckAnnotationClass) }
        .sortedBy { it.getAnnotation(kHealthCheckAnnotationClass).order }
        .toMutableList()

/** Get methods in subsystem that are annotated with `@BeforeHealthCheck`. The methods are sorted in order
 * of the method's name. */
private fun Subsystem.beforeHealthCheckAnnotatedMethods() =
    this.javaClass.declaredMethods.filter { it.isAnnotationPresent(kBeforeHealthCheckAnnotationClass) }
        .sortedBy { it.getAnnotation(kBeforeHealthCheckAnnotationClass).order }

private fun Checkable.beforeHealthCheckAnnotatedMethods() =
    this.javaClass.declaredMethods.filter { it.isAnnotationPresent(kBeforeHealthCheckAnnotationClass) }
        .sortedBy { it.getAnnotation(kBeforeHealthCheckAnnotationClass).order }

/** Get methods in subsystem that are annotated with `@AfterHealthCheck`. The methods are sorted in order
 * of the method's name. */
private fun Subsystem.afterHealthCheckAnnotatedMethods() =
    this.javaClass.declaredMethods.filter { it.isAnnotationPresent(kAfterHealthCheckAnnotationClass) }
        .sortedBy { it.getAnnotation(kAfterHealthCheckAnnotationClass).order }

private fun Checkable.afterHealthCheckAnnotatedMethods() =
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
        healthCheckFields.forEach {
            if(it.type == TalonFX::class.java) {
                healthChecks.add(P6TalonHealthCheckBuilder(subsystem,it).build())
            } else {
                healthChecks.add(TalonHealthCheckBuilder(subsystem,it).build())
            }
        }
//        healthChecks.addAll(healthCheckFields.map { TalonHealthCheckBuilder(subsystem, it).build() })
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

class IOHealthCheckBuilder(private val io: Checkable) {
    fun build(): IOHealthCheck {
        val healthChecks = mutableListOf<HealthCheck>()

        //get all methods from this io layer annotated with @BeforeHealthCheck
        val beforeHealthCHeckMethods = io.beforeHealthCheckAnnotatedMethods()

        //create lifecyclehealthchecks from annotated methods and prepend them to health checks for this io layer
        healthChecks.addAll(beforeHealthCHeckMethods.map { LifecycleHealthCheckIOBuilder(io, it).build() })

        //get all fields from this subsystem with that are annotated with @HealthCheck
        val healthCheckFields = io.healthCheckAnnotatedFields()

        //remove non-talon field(s), only expect swerve drive field
        val fieldsToRemove = mutableSetOf<Field>()

        //make each annotated field accessible or remove it
        healthCheckFields.forEach {
            if(!it.trySetAccessible()) {
                logger.error { "$io: trySetAccessible() failed for ${it.name}, removing!" }
                fieldsToRemove.add(it)
            }
        }
        healthCheckFields.removeAll(fieldsToRemove)

        //If swerve drive field annotated with HealthCheck,
        //create TalonHealthCheck for each azimuth and drive talon and add to talonHealthChecks list
        //queue the swerveDrive field for removal in fieldsToRemove
        healthCheckFields.forEach {
            if(it.type == SwerveDrive::class.java) {
                healthChecks.addAll(FXSwerveDriveHealthCheckIOBuilder(io, it).build())
                fieldsToRemove.add(it)
            }
        }

        //remove all non-talon fields and create TalonHealthChecks from the remaining talon fields
        healthCheckFields.removeAll(fieldsToRemove)
        healthCheckFields.forEach {
            if(it.type == TalonFX::class.java) {
                healthChecks.add(P6TalonHealthCheckIOBuilder(io, it).build())
            } else if(it.type == TalonFXS::class.java) {
                healthChecks.add(FXSTalonHealthCheckIOBuilder(io, it).build())
            }
            else {
                healthChecks.add(TalonHealthCheckIOBuilder(io, it).build())
            }
        }
        // healthChecks.addAll(healthCheckFields.map { TalonHealthCheckIOBuilder(io, it).build() })
        if(healthCheckFields.isEmpty()) logger.warn { "$io: no health checks found" }

        // find any follower checks and add talon to be measured to their associated leader check
        // remove the follower check since run during leader
        val followerHealthChecks = healthChecks.mapNotNull { it as? TalonFollowerHealthCheck }
        val p6FollowerHealthChecks = healthChecks.mapNotNull { it as? P6TalonFollowerHealthCheck }
        val fxsFollowerHealthChecks = healthChecks.mapNotNull { it as? FXSTalonFollowerHealthCheck }
        for(fhc in followerHealthChecks) {
            val lhc = healthChecks.mapNotNull { it as? TalonHealthCheck }.find { it.talon.deviceID == fhc.leaderId }
            if(lhc == null) {
                logger.error { "$io: no leader(id = ${fhc.leaderId}) found for follower (id = ${fhc.talon.deviceID})" }
                continue
            }
            //leader health check is TalonTimedHealthCheck or PositionTimedHealthCheck
            //add follower talon to each child case
            lhc.healthChecks.map { it as TalonHealthCheckCase }.forEach{it.addFollowerTalon(fhc.talon)}
        }
        healthChecks.removeAll(followerHealthChecks)

        for(fhc in p6FollowerHealthChecks) {
            val lhc = healthChecks.mapNotNull { it as? P6TalonHealthCheck }.find { it.talonFx.deviceID == fhc.leaderId }
            if(lhc == null) {
                logger.error { "$io: no leader (id = ${fhc.leaderId}) found for follower (id = ${fhc.talonFx.deviceID})" }
                continue
            }
            //leader is timed or position check, add follower to each child case
            lhc.healthChecks.map { it as P6TalonHealthCheckCase }.forEach{it.addFollowerTalon(fhc.talonFx)}
        }
        healthChecks.removeAll(p6FollowerHealthChecks)

        for(fhc in fxsFollowerHealthChecks) {
            val lhc = healthChecks.mapNotNull { it as? FXSTalonHealthCheck }.find { it.talonFxs.deviceID == fhc.leaderId }
            if(lhc == null) {
                logger.error { "$io: no leader (id = ${fhc.leaderId}) found for follower (id = ${fhc.talonFxs.deviceID})" }
                continue
            }
            //leader is timed or position check, add follower to each child case
            lhc.healthChecks.map { it as FXSTalonHealthCheckCase }.forEach{it.addFollowerTalon(fhc.talonFxs)}
        }
        healthChecks.removeAll(fxsFollowerHealthChecks)

        //gget all methods from subsystem annotated with @AfterHealthCHeck
        val afterHealthCheckMethods = io.afterHealthCheckAnnotatedMethods()

        //create lifecycleHealthCheck from annotated methods and append them to health checks for this io layer
        healthChecks.addAll(afterHealthCheckMethods.map { LifecycleHealthCheckIOBuilder(io, it).build() })

        val name = (io as? Checkable)?.getName() ?: io.toString()
        return  IOHealthCheck(name, healthChecks.toImmutableList())
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

class LifecycleHealthCheckIOBuilder(private val io: Checkable, private val method: Method) {
    fun build(): HealthCheck {
        val ioName = io.getName()

        //throw exception if method annotated with @BeforeHealthCheck is inaccessible
        //rather crach the robot program than run improper health check
        if(!method.trySetAccessible()) {
            val msg = "$ioName.${method.name}: inaccessible, crashing to prevent health check running without setup"
            logger.error { msg }
            throw java.lang.IllegalStateException(msg)
        }

        //methods annotated with @BeforeHealthCheck should have no params and return a boolean
        if(method.parameterCount != 0) {
            val msg = "$ioName.${method.name}: should have no parameters"
            logger.error { msg }
            throw java.lang.IllegalArgumentException(msg)
        }

        if(method.returnType != Boolean::class.java) {
            val msg = "$ioName.${method.name}: should return a boolean"
            logger.error { msg }
            throw java.lang.IllegalArgumentException(msg)
        }

        return LifecycleIOHealthCheck(io, method)
    }
}

private const val AZIMUTH_LEADER_ID = 0
private const val DRIVE_LEADER_ID = 10

class SwerveDriveHealthCheckBuilder(private val subsystem: Subsystem, private val field: Field) {

    private val swerveDrive = field.get(subsystem) as? SwerveDrive
        ?: throw IllegalArgumentException("$subsystem: field '${field.name}' is not a SwerveDrive")

    fun build(): List<HealthCheck> {
        val talonHealthChecks = mutableListOf<HealthCheck>()

        var azimuthLeader: BaseTalon? = null
        var driveLeader: TalonFX? = null
        var driveLeaderId: Int ?= null
        val azimuthFollowers = mutableListOf<BaseTalon>()
        val driveFollowers = mutableListOf<TalonFX>()

        // extract talons from swerve drive modules and
        // create TalonTimedHealthChecks for leader azimuth and drive talons
        // create TalonFollowerHealthCheck for remainder
        // follow associated leaders
        swerveDrive.swerveModules.forEach {
            val module =
                it as? V6TalonSwerveModule ?: throw IllegalArgumentException("$subsystem: $it is not TalonSwerveModule")
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
                talonHealthChecks.add(P6TalonTimedHealthCheckBuilder(drive).build())
                driveLeader = drive
                driveLeaderId = drive.deviceID
            } else {
                talonHealthChecks.add(P6TalonFollowerHealthCheck(drive, DRIVE_LEADER_ID))
                driveFollowers.add(drive)
            }
        }

        requireNotNull(azimuthLeader) { "$subsystem: swerve azimuth talon with id $AZIMUTH_LEADER_ID not found" }
        requireNotNull(driveLeader) { "$subsystem: swerve drive talon with id $DRIVE_LEADER_ID not found" }

        azimuthFollowers.forEach { it.follow(azimuthLeader) }
        driveFollowers.forEach { it.setControl(Follower(driveLeaderId ?: DRIVE_LEADER_ID,false) )}

        return talonHealthChecks
    }
}

class SwerveDriveHealthCheckIOBuilder(private val io: Checkable, private  val field: Field) {
    private val swerveDrive = field.get(io) as? SwerveDrive ?: throw IllegalArgumentException("$io: field '${field.name}' is not a SwerveDrive")

    fun build(): List<HealthCheck> {
        val talonHealthChecks = mutableListOf<HealthCheck>()

        var azimuthLeader: BaseTalon? = null
        var driveLeader: TalonFX? = null
        var driveLeaderId: Int ?= null
        val azimuthFollowers = mutableListOf<BaseTalon>()
        val driveFollowers = mutableListOf<TalonFX>()

        // extract talons from swerve drive modules and
        // create TalonTimedHealthChecks for leader azimuth and drive talons
        // create TalonFollowerHealthCheck for remainder
        // follow associated leaders
        swerveDrive.swerveModules.forEach {
            val module =
                it as? V6TalonSwerveModule ?: throw IllegalArgumentException("$io: $it is not TalonSwerveModule")
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
                talonHealthChecks.add(P6TalonTimedHealthCheckBuilder(drive).build())
                driveLeader = drive
                driveLeaderId = drive.deviceID
            } else {
                talonHealthChecks.add(P6TalonFollowerHealthCheck(drive, DRIVE_LEADER_ID))
                driveFollowers.add(drive)
            }
        }

        requireNotNull(azimuthLeader) { "$io: swerve azimuth talon with id $AZIMUTH_LEADER_ID not found" }
        requireNotNull(driveLeader) { "$io: swerve drive talon with id $DRIVE_LEADER_ID not found" }

        azimuthFollowers.forEach { it.follow(azimuthLeader) }
        driveFollowers.forEach { it.setControl(Follower(driveLeaderId ?: DRIVE_LEADER_ID,false) )}

        return talonHealthChecks
    }
}

class FXSwerveDriveHealthCheckIOBuilder(private val io: Checkable, private  val field: Field) {
    private val swerveDrive = field.get(io) as? SwerveDrive ?: throw IllegalArgumentException("$io: field '${field.name}' is not a SwerveDrive")

    fun build(): List<HealthCheck> {
        val talonHealthChecks = mutableListOf<HealthCheck>()

        var azimuthLeader: TalonFXS? = null
        var driveLeader: TalonFX? = null
        var driveLeaderId: Int ?= null
        var azimuthLeaderId: Int ?= null
        val azimuthFollowers = mutableListOf<TalonFXS>()
        val driveFollowers = mutableListOf<TalonFX>()

        // extract talons from swerve drive modules and
        // create TalonTimedHealthChecks for leader azimuth and drive talons
        // create TalonFollowerHealthCheck for remainder
        // follow associated leaders
        swerveDrive.swerveModules.forEach {
            val module =
                it as? FXSwerveModule ?: throw IllegalArgumentException("$io: $it is not FXTalonSwerveModule")
            val azimuth = module.azimuthTalon
            val drive = module.driveTalon

            if (azimuth.deviceID == AZIMUTH_LEADER_ID) {
                talonHealthChecks.add(FXSTalonTimedHealthCheckBuilder(azimuth).build())
                azimuthLeader = azimuth
            } else {
                talonHealthChecks.add(FXSTalonFollowerHealthCheck(azimuth, AZIMUTH_LEADER_ID))
                azimuthFollowers.add(azimuth)
            }

            if (drive.deviceID == DRIVE_LEADER_ID) {
                talonHealthChecks.add(P6TalonTimedHealthCheckBuilder(drive).build())
                driveLeader = drive
                driveLeaderId = drive.deviceID
            } else {
                talonHealthChecks.add(P6TalonFollowerHealthCheck(drive, DRIVE_LEADER_ID))
                driveFollowers.add(drive)
            }
        }

        requireNotNull(azimuthLeader) { "$io: swerve azimuth talon with id $AZIMUTH_LEADER_ID not found" }
        requireNotNull(driveLeader) { "$io: swerve drive talon with id $DRIVE_LEADER_ID not found" }

        azimuthFollowers.forEach { it.setControl(Follower(azimuthLeaderId ?: AZIMUTH_LEADER_ID, false))}
        driveFollowers.forEach { it.setControl(Follower(driveLeaderId ?: DRIVE_LEADER_ID,false) )}

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
            builder.encoderChange = positionAnnotation.encoderChange.toInt()
            return builder.build()
        }

        if (followAnnotation != null) return TalonFollowerHealthCheck(talon, followAnnotation.leader)

        // default to timed check if not specified
        return TalonTimedHealthCheckBuilder(talon).build()
    }
}

class TalonHealthCheckIOBuilder(private val io: Checkable, private val field: Field) {
    val talon = field.get(io) as? BaseTalon ?: throw  IllegalArgumentException("$io: field '${field.name}' is not a subclass of BaseTalon")

    fun build(): TalonHealthCheck {
        val timedAnnotation = field.getAnnotation(Timed::class.java)
        val positionAnnotation = field.getAnnotation(Position::class.java)
        val followAnnotation = field.getAnnotation(Follow::class.java)

        if(arrayListOf(timedAnnotation, positionAnnotation, followAnnotation).filterNotNull().count() > 1) throw IllegalArgumentException("$io: only one of @Timed, @Position, or @Follow may be specified.")

        if(timedAnnotation != null) {
            val builder = TalonTimedHealthCheckBuilder(talon)
            builder.percentOutput = timedAnnotation.percentOutput
            builder.duration = timedAnnotation.duration
            return builder.build()
        }

        if(positionAnnotation != null) {
            val builder = TalonPositionHealthCheckBuilder(talon)
            builder.percentOutput = positionAnnotation.percentOutput
            builder.encoderChange = positionAnnotation.encoderChange.toInt()
            return builder.build()
        }

        if(followAnnotation != null) return TalonFollowerHealthCheck(talon, followAnnotation.leader)

        //default to timed if not specified
        return TalonTimedHealthCheckBuilder(talon).build()
    }
}

class P6TalonHealthCheckBuilder(private val subsystem: Subsystem, private val field: Field) {
    val talonFx = field.get(subsystem) as? TalonFX?: throw java.lang.IllegalArgumentException("$subsystem: field '${field.name}' is not subclass of TalonFX")

    fun build(): P6TalonHealthCheck {
        val timedAnnotation = field.getAnnotation(Timed::class.java)
        val positionAnnotation = field.getAnnotation(Position::class.java)
        val followAnnotation = field.getAnnotation(Follow::class.java)

        if(arrayListOf(timedAnnotation, positionAnnotation, followAnnotation).filterNotNull().count() > 1)
            throw IllegalArgumentException("$subsystem: only one of @Timed, @Position, or @Follow may be specified.")

        if(timedAnnotation != null) {
            val builder = P6TalonTimedHealthCheckBuilder(talonFx)
            builder.percentOutput = timedAnnotation.percentOutput
            builder.duration = timedAnnotation.duration
            return builder.build()
        }

        if(positionAnnotation != null) {
            val builder = P6TalonPositionHealthCheckBuilder(talonFx)
            builder.percentOutput = positionAnnotation.percentOutput
            builder.encoderChange = positionAnnotation.encoderChange
            return  builder.build()
        }

        if(followAnnotation != null) return P6TalonFollowerHealthCheck(talonFx, followAnnotation.leader)

        //Default ot timed if not specified
        return P6TalonTimedHealthCheckBuilder(talonFx).build()
    }
}

class P6TalonHealthCheckIOBuilder(private val io: Checkable, private val field: Field) {
    val talonFx = field.get(io) as? TalonFX?: throw java.lang.IllegalArgumentException("$io: field '${field.name}' is not subclass of TalonFX")

    fun build(): P6TalonHealthCheck {
        val timedAnnotation = field.getAnnotation(Timed::class.java)
        val positionAnnotation = field.getAnnotation(Position::class.java)
        val followAnnotation = field.getAnnotation(Follow::class.java)

        if(arrayListOf(timedAnnotation, positionAnnotation, followAnnotation).filterNotNull().count() > 1)
            throw IllegalArgumentException("$io: only one of @Timed, @Position, or @Follow may be specified.")

        if(timedAnnotation != null) {
            val builder = P6TalonTimedHealthCheckBuilder(talonFx)
            builder.percentOutput = timedAnnotation.percentOutput
            builder.duration = timedAnnotation.duration
            return  builder.build()
        }

        if(positionAnnotation != null) {
            val builder = P6TalonPositionHealthCheckBuilder(talonFx)
            builder.percentOutput = positionAnnotation.percentOutput
            builder.encoderChange = positionAnnotation.encoderChange
            return  builder.build()
        }

        if(followAnnotation != null) return P6TalonFollowerHealthCheck(talonFx, followAnnotation.leader)

        //Default to timed if not specified
        return P6TalonTimedHealthCheckBuilder(talonFx).build()
    }
}

class FXSTalonHealthCheckIOBuilder(private val io: Checkable, private val field: Field) {
    val talonFxs = field.get(io) as? TalonFXS?: throw java.lang.IllegalArgumentException("$io: field '${field.name}' is not subclass of TalonFXS")

    fun build(): FXSTalonHealthCheck {
        val timedAnnotation = field.getAnnotation(Timed::class.java)
        val positionAnnotation = field.getAnnotation(Position::class.java)
        val followAnnotation = field.getAnnotation(Follow::class.java)

        if(arrayListOf(timedAnnotation, positionAnnotation, followAnnotation).filterNotNull().count() > 1)
            throw IllegalArgumentException("$io: only one of @Timed, @Position, or @Follow may be specified.")

        if(timedAnnotation != null) {
            val builder = FXSTalonTimedHealthCheckBuilder(talonFxs)
            builder.percentOutput = timedAnnotation.percentOutput
            builder.duration = timedAnnotation.duration
            return  builder.build()
        }

        if(positionAnnotation != null) {
            val builder = FXSTalonPositionHealthCheckBuilder(talonFxs)
            builder.percentOutput = positionAnnotation.percentOutput
            builder.encoderChange = positionAnnotation.encoderChange
            return  builder.build()
        }

        if(followAnnotation != null) return FXSTalonFollowerHealthCheck(talonFxs, followAnnotation.leader)

        //Default to timed if not specified
        return FXSTalonTimedHealthCheckBuilder(talonFxs).build()
    }
}

class TalonTimedHealthCheckBuilder(val talon: BaseTalon) {

    var percentOutput = doubleArrayOf(0.2, 1.0, -0.2, -1.0)
    var duration = 2.0
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

class P6TalonTimedHealthCheckBuilder(val talonFx: TalonFX) {
    var percentOutput = doubleArrayOf(0.2, 1.0, -0.2, -1.0)
    var duration = 2.0
    var limits: DoubleArray? = null

    fun build(): P6TalonHealthCheck {
        val cases = percentOutput.let {
            var previousCase: P6TalonTimedHealthCheckCase? = null
            it.map { pctOut ->
                val case = P6TalonTimedHealthCheckCase(previousCase, talonFx, pctOut, (duration * 1e6).toLong())
                previousCase = case
                case
            }
        }

        return P6TalonTimedHealthCheck(talonFx, cases, percentOutput)
    }
}

class FXSTalonTimedHealthCheckBuilder(val talonFxs: TalonFXS) {
    var percentOutput = doubleArrayOf(0.2, 1.0, -0.2, -1.0)
    var duration = 2.0
    var limits: DoubleArray? = null

    fun build(): FXSTalonHealthCheck {
        val cases = percentOutput.let {
            var previousCase: FXSTalonTimedHealthCheckCase? = null
            it.map { pctOut ->
                val case = FXSTalonTimedHealthCheckCase(previousCase, talonFxs, pctOut, (duration * 1e6).toLong())
                previousCase = case
                case
            }
        }

        return FXSTalonTimedHealthCheck(talonFxs, cases, percentOutput)
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

class P6TalonPositionHealthCheckBuilder(var talonFx: TalonFX) {
    var percentOutput = doubleArrayOf(0.25, -0.25)
    var encoderChange = 0.0
    var limits: DoubleArray? = null

    fun build(): P6TalonHealthCheck {
        if(encoderChange == 0.0) logger.warn { "Talon ${talonFx.deviceID}: position health check encoderChange is zero" }

        val cases = percentOutput.let {
            var previousCase: P6TalonPositionHealthCheckCase? = null
            it.map { pctOut ->
                val case = P6TalonPositionHealthCheckCase(previousCase, talonFx, pctOut, encoderChange)
                previousCase = case
                case
            }
        }

        return P6TalonPositionHealthCheck(talonFx, cases, percentOutput)
    }
}

class FXSTalonPositionHealthCheckBuilder(var talonFxs: TalonFXS) {
    var percentOutput = doubleArrayOf(0.25, -0.25)
    var encoderChange = 0.0
    var limits: DoubleArray? = null

    fun build(): FXSTalonHealthCheck {
        if(encoderChange == 0.0) logger.warn { "Talon ${talonFxs.deviceID}: position health check encoderChange is zero" }

        val cases = percentOutput.let {
            var previousCase: FXSTalonPositionHealthCheckCase? = null
            it.map { pctOut ->
                val case = FXSTalonPositionHealthCheckCase(previousCase, talonFxs, pctOut, encoderChange)
                previousCase = case
                case
            }
        }

        return FXSTalonPositionHealthCheck(talonFxs, cases, percentOutput)
    }
}
