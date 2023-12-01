package org.strykeforce.healthcheck.internal

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.BaseTalon
import edu.wpi.first.wpilibj.RobotController
import edu.wpi.first.wpilibj2.command.Subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase
import mu.KotlinLogging
import java.lang.reflect.Method
import java.util.*
import kotlin.math.abs

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
        iterator = healthChecks.iterator()
        if (!iterator.hasNext()) {
            logger.error { "no health checks assigned" }
            isFinished = true
            return
        }
        current = iterator.next()
        current.initialize()
        isFinished = false
    }

    override fun execute() {
        if (current.isFinished) {
            if (iterator.hasNext()) {
                current = iterator.next()
                current.initialize()
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
) : TalonHealthCheck(talon, healthChecks)

class TalonPositionHealthCheck(
    talon: BaseTalon,
    healthChecks: List<HealthCheck>,
    val percentOutput: DoubleArray,
) : TalonHealthCheck(talon, healthChecks)

class TalonFollowerHealthCheck(talon: BaseTalon, val leaderId: Int) : TalonHealthCheck(talon, listOf())

class LifecycleHealthCheck(private val subsystem: Subsystem, private val method: Method) : HealthCheck {

    override val name = subsystem.let {
        val subsystemName = (it as? SubsystemBase)?.name ?: it.toString()
        "$subsystemName.${method.name}"
    }

    override var isFinished = false

    override fun accept(visitor: HealthCheckVisitor) = visitor.visit(this)

    override fun initialize() {
        isFinished = false
    }

    override fun execute() {
        // @BeforeHealthCheck annotated methods will return true when finished
        isFinished = method.invoke(subsystem) as Boolean
    }
}

var caseId = 0

class ResetCaseNum() {
    fun resetCaseId() {
        caseId = 0;
    }
}

abstract class TalonHealthCheckCase(
    val talon: BaseTalon,
    val isReversing: Boolean,
    val type: String,
    val output: Double,
    val duration: Long
) : HealthCheck {

    val case = caseId++
    var uuid: UUID = UUID.randomUUID()

    override var isFinished = false

    private var state = State.INITIALIZING

    private var start = 0L

    val data: MutableList<TalonHealthCheckData> = mutableListOf(TalonHealthCheckData(case, talon))

//    abstract fun addFollowerTalon(talon: BaseTalon)

    fun addFollowerTalon(talon: BaseTalon) {
        data.add(TalonHealthCheckData(case, talon))
    }

    override fun accept(visitor: HealthCheckVisitor) = visitor.visit(this)

    override fun initialize() {
        uuid = UUID.randomUUID()
        data.forEach(TalonHealthCheckData::reset)
        state = State.INITIALIZING
        isFinished = false
        start = 0
    }

    abstract fun isRunning(elapsed: Long): Boolean

    abstract fun setTalon(talon: BaseTalon)

    fun measure(timestamp: Long) = data.forEach { it.measure(timestamp) }

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
                val elapsed = time - start
                measure(elapsed)
            }

            State.RUNNING -> {
                val elapsed = time - start
                if (isRunning(elapsed)) {
                    state = State.STOPPING
                    return
                }
                measure(elapsed)
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
    duration: Long
) : TalonHealthCheckCase(
    talon,
    (previousCase?.percentOutput ?: 0.0) * percentOutput < 0.0,
    "time",
    percentOutput,
    duration
) {

    override val name = "TalonTimedHealthCheckCase: ${percentOutput * 100} percent output"
//    override val data = mutableListOf(TalonHealthCheckData(case, talon))

//    override fun addFollowerTalon(talon: BaseTalon) {
//        data.add(TalonHealthCheckData(case, talon))
//    }

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


class TalonPositionHealthCheckCase(
    previousCase: TalonPositionHealthCheckCase?,
    talon: BaseTalon,
    val percentOutput: Double,
    private val encoderChange: Int
) : TalonHealthCheckCase(
    talon,
    (previousCase?.percentOutput ?: 0.0) * percentOutput < 0.0,
    "position",
    percentOutput,
    encoderChange.toLong()
) {

    override val name = "TalonPositionHealthCheckCase: ${percentOutput * 100} percent output"
//    override val data = mutableListOf(TalonHealthCheckData(case, talon))

//    override fun addFollowerTalon(talon: BaseTalon) {
//        data.add(TalonHealthCheckData(case, talon))
//    }

    private var encoderStart: Int = 0

    override fun initialize() {
        super.initialize()
        encoderStart = talon.selectedSensorPosition.toInt()
    }

    override fun isRunning(elapsed: Long): Boolean {
        val encoderCurrent = talon.selectedSensorPosition.toInt()
        return abs(encoderCurrent - encoderStart) >= encoderChange
    }

    override fun setTalon(talon: BaseTalon) {
        talon.set(ControlMode.PercentOutput, percentOutput)
    }


    override fun toString(): String {
        return "TalonPositionHealthCheckCase(" +
                "percentOutput=$percentOutput, " +
                "encoderChange=$encoderChange, " +
                "isReversing=$isReversing" +
                ")"
    }
}
