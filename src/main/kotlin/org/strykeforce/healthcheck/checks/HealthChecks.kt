package org.strykeforce.healthcheck.checks

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.BaseTalon
import edu.wpi.first.wpilibj.RobotController
import mu.KotlinLogging
import org.strykeforce.healthcheck.DataPoint

interface HealthCheck {
    val name: String
    var isFinished: Boolean
    fun accept(visitor: HealthCheckVisitor)
    fun initialize()
    fun execute()
}

abstract class HealthCheckCollection(override val name: String, val healthChecks: List<HealthCheck>) :
    HealthCheck {

    private val logger = KotlinLogging.logger {}

    private lateinit var iterator: Iterator<HealthCheck>
    private lateinit var current: HealthCheck

    override var isFinished = false

    override fun initialize() {
        healthChecks.forEach(HealthCheck::initialize)
        iterator = healthChecks.iterator()
        if (!iterator.hasNext()) {
            logger.error { "no health checks assigned" }
            isFinished = true
            return
        }
        current = iterator.next()
        isFinished = false
    }

    override fun execute() {
        if (current.isFinished) {
            if (iterator.hasNext()) {
                current = iterator.next()
            } else {
                isFinished = true
            }
            return
        }
        current.execute()
    }
}


class RobotHealthCheck(name: String, healthChecks: List<SubsystemHealthCheck>) :
    HealthCheckCollection(name, healthChecks) {
    override fun accept(visitor: HealthCheckVisitor) {
        visitor.visit(this)
    }
}

class SubsystemHealthCheck(name: String, healthChecks: List<HealthCheck>) :
    HealthCheckCollection(name, healthChecks) {
    override var isFinished = false
    override fun accept(visitor: HealthCheckVisitor) {
        visitor.visit(this)
    }
}

abstract class TalonHealthCheck(val talon: BaseTalon, healthChecks: List<HealthCheck>) :
    HealthCheckCollection("Talon ${talon.deviceID}", healthChecks) {
    override fun accept(visitor: HealthCheckVisitor) {
        visitor.visit(this)
    }
}

enum class State {
    INITIALIZING,
    REVERSING,
    STARTING,
    RUNNING,
    STOPPING,
}

private const val REVERSING_DURATION = 1e6 // microseconds

class TalonTimedHealthCheck(
    talon: BaseTalon,
    healthChecks: List<HealthCheck>,
    val percentOutput: DoubleArray,
    val duration: Double,
    val limits: DoubleArray?
) : TalonHealthCheck(talon, healthChecks) {
    override fun toString(): String {
        return "TalonTimedHealthCheck(" +
                "talon=${talon.deviceID}, " +
                "duration=$duration, " +
                "limits=${limits?.contentToString()}" +
                ")"
    }
}

abstract class TalonHealthCheckCase(
    val talon: BaseTalon,
    val isReversing: Boolean
) : HealthCheck {
    private val logger = KotlinLogging.logger {}

    override var isFinished = false

    private var state = State.INITIALIZING

    private var start = 0L

    override fun accept(visitor: HealthCheckVisitor) {
        visitor.visit(this)
    }

    override fun initialize() {
        state = State.INITIALIZING
        isFinished = false
        start = 0
    }

    abstract fun isRunning(elapsed: Long): Boolean

    abstract fun setTalon(talon: BaseTalon)

    fun measure() {
        val dataPoint = DataPoint(
            talon.motorOutputVoltage, talon.selectedSensorVelocity, talon.supplyCurrent, talon.statorCurrent,
        )
        logger.info { dataPoint.toString() }
    }

    override fun execute() {
        val time = RobotController.getFPGATime()

        when (state) {
            State.INITIALIZING -> {
                talon.set(ControlMode.PercentOutput, 0.0)
                start = time
                state = if (isReversing) State.REVERSING else State.STARTING
            }

            State.REVERSING -> {
                val elapsed = time - start
                state = if (elapsed > REVERSING_DURATION) State.STARTING else State.REVERSING
            }

            State.STARTING -> {
                setTalon(talon)
                start = time
                state = State.RUNNING
            }

            State.RUNNING -> {
                val elapsed = time - start
                if (isRunning(elapsed)) {
                    state = State.STOPPING
                    return
                }
                measure()
            }

            State.STOPPING -> {
                talon.set(ControlMode.PercentOutput, 0.0)
                isFinished = true
            }
        }
    }
}

class TalonTimedHealthCheckCase(
    previousCase: TalonTimedHealthCheckCase?,
    talon: BaseTalon,
    val percentOutput: Double,
    val duration: Long
) : TalonHealthCheckCase(talon, (previousCase?.percentOutput ?: 0.0) * percentOutput < 0.0) {

    override val name = "TalonTimedHealthCheckCase: ${percentOutput * 100} percent output"
    override fun isRunning(elapsed: Long) = elapsed > duration

    override fun setTalon(talon: BaseTalon) {
        talon.set(ControlMode.PercentOutput, percentOutput)
    }

    override fun toString(): String {
        return "TalonTimedHealthCheckCase(" +
                "percentOutput=$percentOutput, " +
                "duration=$duration, " +
                "isReversing=$isReversing" +
                ")"
    }


}
