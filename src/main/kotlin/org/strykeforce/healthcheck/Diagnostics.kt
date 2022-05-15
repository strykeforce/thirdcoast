package org.strykeforce.healthcheck

import com.ctre.phoenix.motorcontrol.can.BaseTalon
import mu.KotlinLogging
import java.lang.reflect.Field

private const val MULTIPLE_HEALTHCHECKS_ERR =
    "Only one of @Timed, @Position, or @Follow may be specified."

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


internal enum class State {
    STARTING,
    RUNNING,
    STOPPED
}

class DiagnosticGroup() {
    val diagnostics: List<Diagnostic> = mutableListOf()
}


abstract class Diagnostic(val talon: BaseTalon, val limits: DoubleArray?) {

    var isFinished: Boolean = false
        private set

    open fun initialize() {
        logger.debug { "initializing $this" }
        isFinished = false
    }

    open fun execute() {
        isFinished = true
    }

    fun measure(iteration: Int) {
        TODO("Not yet implemented")
    }
}

class TimedDiagnostic(
    talon: BaseTalon,
    var percentOutput: DoubleArray,
    var duration: Double,
    limits: DoubleArray?
) : Diagnostic(talon, limits) {
    constructor(talon: BaseTalon) : this(
        talon,
        doubleArrayOf(0.25, 0.5, 0.75, -0.25, -0.5, -0.75),
        5.0,
        null
    )

    override fun toString(): String {
        return "TimedDiagnostic(percentOutput=${percentOutput.contentToString()}, duration=$duration)"
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

