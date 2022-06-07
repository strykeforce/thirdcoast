package org.strykeforce.healthcheck

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.BaseTalon
import mu.KotlinLogging
import org.strykeforce.healthcheck.State.*
import java.lang.reflect.Field

private const val MULTIPLE_HEALTHCHECKS_ERR =
    "Only one of @Timed, @Position, or @Follow may be specified."

private const val REVERSING_TIMEOUT = 1.0

private val logger = KotlinLogging.logger {}

internal fun Field.diagnosticFor(talon: BaseTalon): Diagnostic {

    var diagnostic: Diagnostic? = null

    val limits: DoubleArray? = getAnnotation(Limits::class.java)?.value

    val timed = getAnnotation(Timed::class.java)
    if (timed != null) {
        diagnostic = TimedDiagnostic(talon, timed.percentOutput, timed.duration, limits)
    }

    val position = getAnnotation(Position::class.java)
    if (position != null) {
        check(timed == null) { MULTIPLE_HEALTHCHECKS_ERR }
        diagnostic =
            PositionDiagnostic(talon, position.percentOutput, position.encoderChange, limits)
    }

    val followCheck = getAnnotation(Follow::class.java)
    if (followCheck != null) {
        check(timed == null && position == null) { MULTIPLE_HEALTHCHECKS_ERR }
        diagnostic = FollowDiagnostic(talon, limits)
    }

    return diagnostic ?: TimedDiagnostic(talon)
}


enum class State {
    INITIALIZING,
    REVERSING,
    STARTING,
    RUNNING,
    STOPPING,
}

class DiagnosticGroup {
    val diagnostics: List<Diagnostic> = mutableListOf()
}


abstract class Diagnostic(val talon: BaseTalon, val limits: DoubleArray?) {

    var isFinished: Boolean = false
        protected set

    lateinit var statistics: Statistics

    open fun initialize() {
        logger.debug { "initializing $this" }
        statistics = Statistics()
        isFinished = false
    }

    open fun execute(time: Double) {
        isFinished = true
    }


}

class TimedDiagnostic(
    talon: BaseTalon,
    percentOutput: DoubleArray,
    duration: Double,
    limits: DoubleArray?
) : Diagnostic(talon, limits) {

    constructor(talon: BaseTalon) : this(
        talon,
        doubleArrayOf(0.25, 0.5, 0.75, -0.25, -0.5, -0.75),
        5.0,
        null
    )

    private val cases: List<Case> = percentOutput.let {
        var previousCase: Case? = null
        it.map { output ->
            val case = Case(previousCase, output, duration)
            previousCase = case
            case
        }
    }

    private lateinit var caseIterator: Iterator<Case>
    private lateinit var currentCase: Case

    override fun initialize() {
        super.initialize()
        cases.forEach { it.initialize() }
        caseIterator = cases.iterator()
        if (!caseIterator.hasNext()) {
            logger.error { "initialize: $this has no percent outputs assigned" }
            isFinished = true
            return
        }
        currentCase = caseIterator.next()
        isFinished = false
    }

    override fun execute(time: Double) {
        if (currentCase.isFinished) {
            logger.debug { "execute:$currentCase is done" }
            if (caseIterator.hasNext()) {
                currentCase = caseIterator.next()
                logger.debug { "execute: iterator has next, current = $currentCase" }
            } else {
                logger.debug { "execute: $currentCase is finished" }
                isFinished = true
            }
            return
        }
        logger.debug { "execute: running case: $cases" }
        currentCase.execute(time)
    }

    override fun toString(): String {
        return "TimedDiagnostic(cases=$cases)"
    }


    inner class Case(previousCase: Case?, val percentOutput: Double, val duration: Double) {
        private var state: State = INITIALIZING
        private lateinit var dataSeries: DataSeries

        private val isReversing = (previousCase?.percentOutput ?: 0.0) * percentOutput < 0.0

        var isFinished: Boolean = false
            private set

        private var start = 0.0

        fun initialize() {
            state = INITIALIZING
            dataSeries = statistics.newDataSeries(talon.toString())
            isFinished = false
            start = 0.0
        }

        fun execute(time: Double) {

            logger.info { "execute: $this state = $state" }

            when (state) {
                INITIALIZING -> {
                    talon.set(ControlMode.PercentOutput, 0.0)
                    start = time
                    state = if (isReversing) REVERSING else STARTING
                }
                REVERSING -> {
                    val elapsed = time - start
                    logger.info { "reversing: elapsed = $elapsed" }
                    state = if (elapsed > REVERSING_TIMEOUT) STARTING else REVERSING
                }
                STARTING -> {
                    talon.set(ControlMode.PercentOutput, percentOutput)
                    start = time
                    state = RUNNING
                }
                RUNNING -> {
                    val elapsed = time - start
                    logger.info { "execute: elapsed = $elapsed" }
                    if (elapsed > duration) {
                        state = STOPPING
                        return
                    }
                    measure()
                }
                STOPPING -> {
                    talon.set(ControlMode.PercentOutput, 0.0)
                    isFinished = true
                }
            }
        }

        fun measure() {
            val dataPoint = DataPoint(
                talon.motorOutputVoltage,
                talon.statorCurrent,
                talon.supplyCurrent,
                talon.selectedSensorVelocity
            )
            dataSeries.compute(dataPoint)
        }

        override fun toString(): String {
            return "Case(percentOutput=$percentOutput, duration=$duration)"
        }
    }
}

class PositionDiagnostic(
    talon: BaseTalon,
    var percentOutput: DoubleArray,
    var encoderChange: Int,
    limits: DoubleArray?
) : Diagnostic(talon, limits) {

    override fun toString(): String {
        return "PositionDiagnostic(percentOutput=${percentOutput.contentToString()}, encoderChange=$encoderChange)"
    }


}

class FollowDiagnostic(talon: BaseTalon, limits: DoubleArray?) : Diagnostic(talon, limits) {

}

