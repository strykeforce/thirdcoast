package org.strykeforce.healthcheck.internal

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.BaseTalon
import com.ctre.phoenix6.controls.DutyCycleOut
import com.ctre.phoenix6.hardware.TalonFX
import edu.wpi.first.units.Units
import edu.wpi.first.wpilibj.RobotController
import edu.wpi.first.wpilibj2.command.Subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlinx.html.OL
import mu.KotlinLogging
import org.strykeforce.healthcheck.Checkable
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

class RobotIOHealthCheck(name: String, healthChecks: List<IOHealthCheck>): HealthCheckCollection(name, healthChecks) {
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

class IOHealthCheck(name: String, healthChecks: List<HealthCheck>): HealthCheckCollection(name, healthChecks) {
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

abstract class P6TalonHealthCheck(val talonFx: TalonFX, healthChecks: List<HealthCheck>) :
    HealthCheckCollection("TalonFX ${talonFx.deviceID}", healthChecks) {
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

class P6TalonTimedHealthCheck(
    talonFx: TalonFX,
    healthChecks: List<HealthCheck>,
    val percentOutput: DoubleArray,
): P6TalonHealthCheck(talonFx, healthChecks)

class TalonPositionHealthCheck(
    talon: BaseTalon,
    healthChecks: List<HealthCheck>,
    val percentOutput: DoubleArray,
) : TalonHealthCheck(talon, healthChecks)

class P6TalonPositionHealthCheck(
    talonFx: TalonFX,
    healthChecks: List<HealthCheck>,
    val percentOutput: DoubleArray,
): P6TalonHealthCheck(talonFx, healthChecks)

class TalonFollowerHealthCheck(talon: BaseTalon, val leaderId: Int) : TalonHealthCheck(talon, listOf())

class P6TalonFollowerHealthCheck(talonFX: TalonFX, val leaderId: Int) : P6TalonHealthCheck(talonFX, listOf())

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

class LifecycleIOHealthCheck(private val io: Checkable, private val method: Method) : HealthCheck {
    override val name = io.let {
        val ioName = (it as? Checkable)?.getName() ?: it.toString()
        "$ioName.${method.name}"
    }

    override var isFinished = false

    override fun accept(visitor: HealthCheckVisitor) = visitor.visit(this)

    override fun initialize() {
        isFinished = false
    }

    override fun execute() {
        isFinished = method.invoke(io) as Boolean
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

abstract class P6TalonHealthCheckCase(
    val talonFx: TalonFX,
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

    val data: MutableList<P6TalonHealthCheckData> = mutableListOf(P6TalonHealthCheckData(case, talonFx))

    fun addFollowerTalon(talonFx: TalonFX) {
        data.add(P6TalonHealthCheckData(case, talonFx))
    }

    override fun accept(visitor: HealthCheckVisitor) = visitor.visit(this)

    override fun initialize() {
        uuid = UUID.randomUUID()
        data.forEach(P6TalonHealthCheckData::reset)
        state = State.INITIALIZING
        isFinished = false
        start = 0
    }

    abstract fun isRunning(elapsed: Long) : Boolean

    abstract fun setTalon(talonFx: TalonFX)

    fun measure(timestamp: Long) = data.forEach { it.measure(timestamp) }

    override fun execute() {
        val time = RobotController.getFPGATime()

        when(state) {
            State.INITIALIZING -> {
                talonFx.setControl(DutyCycleOut(0.0))
                start = time
                state = if(isReversing) State.REVERSING else State.STARTING
            }

            State.REVERSING -> {
                val elapsed = time - start
                state = if(elapsed > REVERSING_DURATION) State.STARTING else State.REVERSING
            }

            State.STARTING -> {
                setTalon(talonFx)
                start = time
                state = State.RUNNING
                val elapsed = time - start
                measure(elapsed)
            }

            State.RUNNING -> {
                val elapsed = time - start
                if(isRunning(elapsed)) {
                    state = State.STOPPING
                    return
                }
                measure(elapsed)
            }

            State.STOPPING -> {
                talonFx.setControl(DutyCycleOut(0.0))
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

class P6TalonTimedHealthCheckCase(
    previousCase: P6TalonTimedHealthCheckCase?,
    talonFx :TalonFX,
    val percentOutput: Double,
    duration: Long
) : P6TalonHealthCheckCase(talonFx, (previousCase?.percentOutput ?: 0.0) * percentOutput < 0.0, "time", percentOutput, duration) {
    override val name = "P6TalonTimedHealthCheckCase: ${percentOutput * 100} percent output"

    override fun isRunning(elapsed: Long) = elapsed > duration

    override fun setTalon(talonFx: TalonFX) {
        talonFx.setControl(DutyCycleOut(percentOutput))
    }

    override fun toString(): String {
        return "P6TalonTimedHealthCheckCase(" +
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

class P6TalonPositionHealthCheckCase(
    previousCase: P6TalonPositionHealthCheckCase?,
    talonFx: TalonFX,
    val percentOutput: Double,
    private val encoderChange: Double
): P6TalonHealthCheckCase(
    talonFx,
    (previousCase?.percentOutput ?: 0.0) * percentOutput < 0.0,
    "position",
    percentOutput,
    encoderChange.toLong()
) {
    override val name = "P6TalonPositionHeathCheckCase: ${percentOutput * 100} percent output"

    private var encoderStart: Double = 0.0

    override fun initialize() {
        super.initialize()
        encoderStart = talonFx.position.value.`in`(Units.Rotations)
    }

    override fun isRunning(elapsed: Long): Boolean {
        val encoderCurrent = talonFx.position.value.`in`(Units.Rotations)
        return abs(encoderCurrent - encoderStart) >= encoderChange
    }

    override fun setTalon(talonFx: TalonFX) {
        talonFx.setControl(DutyCycleOut(percentOutput))
    }

    override fun toString(): String {
        return "P6TalonPositionHealthCheckCase(" +
                "percentOutput=$percentOutput, " +
                "encoderChange=$encoderChange, " +
                "isReversing=$isReversing" +
                ")"
    }
}
