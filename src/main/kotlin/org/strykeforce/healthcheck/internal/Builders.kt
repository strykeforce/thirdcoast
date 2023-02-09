package org.strykeforce.healthcheck.internal

import com.ctre.phoenix.motorcontrol.can.BaseTalon
import edu.wpi.first.wpilibj2.command.Subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase
import mu.KotlinLogging
import okhttp3.internal.toImmutableList
import org.strykeforce.healthcheck.Follow
import org.strykeforce.healthcheck.Position
import org.strykeforce.healthcheck.Timed
import java.lang.reflect.Field

private val logger = KotlinLogging.logger {}

class RobotHealthCheckBuilder(vararg subsystems: Subsystem) {

    private val subsystemSet = subsystems.toList()

    fun build(): RobotHealthCheck {
        val subsystemHealthChecks = subsystemSet.map { SubsystemHealthCheckBuilder(it).build() }
        return RobotHealthCheck("Robot Health Check", subsystemHealthChecks)
    }
}

class SubsystemHealthCheckBuilder(private val subsystem: Subsystem) {

    fun build(): SubsystemHealthCheck {
        val talonHealthChecks =
            subsystem.javaClass.declaredFields.filter { it.isAnnotationPresent(org.strykeforce.healthcheck.HealthCheck::class.java) }
                .map { TalonHealthCheckBuilder(subsystem, it).build() }.toMutableList()

        if (talonHealthChecks.isEmpty()) logger.error { "SubsystemHealthCheckBuilder: no health checks found: $subsystem" }

        // process any follower talon health checks by adding their talons to be measured to the
        // associated leader health checks
        val followerHealthChecks = talonHealthChecks.mapNotNull { it as? TalonFollowerHealthCheck }
        for (fhc in followerHealthChecks) {
            val lhc = talonHealthChecks.find { it.talon.deviceID == fhc.leaderId }
            if (lhc == null) {
                logger.error { "$subsystem: no leader (id = ${fhc.leaderId}) found for follower (id = ${fhc.talon.deviceID})" }
                continue
            }
            lhc.healthChecks.map { it as TalonHealthCheckCase }.forEach { it.addFollowerTalon(fhc.talon) }
        }
        // follower health checks are removed since they don't run anything
        talonHealthChecks.removeAll(followerHealthChecks)

        val name = (subsystem as? SubsystemBase)?.name ?: subsystem.toString()
        return SubsystemHealthCheck(name, talonHealthChecks.toImmutableList())
    }
}


class TalonHealthCheckBuilder(val subsystem: Subsystem, val field: Field) {

    init {
        if (!field.trySetAccessible())
            logger.error { "trySetAccessible() failed for $subsystem: ${field.name}" }
    }

    val talon = field.get(subsystem) as? BaseTalon
        ?: throw IllegalArgumentException("Subsystem $subsystem field '${field.name}' is not a subclass of BaseTalon")

    fun build(): TalonHealthCheck {
        val timedAnnotation = field.getAnnotation(Timed::class.java)
        val positionAnnotation = field.getAnnotation(Position::class.java)
        val followAnnotation = field.getAnnotation(Follow::class.java)

        if (arrayListOf(timedAnnotation, positionAnnotation, followAnnotation).filterNotNull().count() > 1)
            throw IllegalArgumentException("Only one of @Timed, @Position, or @Follow may be specified.")

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

        return TalonTimedHealthCheck(talon, cases, percentOutput, duration, doubleArrayOf())
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

        return TalonPositionHealthCheck(talon, cases, percentOutput, encoderChange, limits)
    }
}
