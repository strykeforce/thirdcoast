package org.strykeforce.healthcheck

import com.ctre.phoenix.motorcontrol.can.BaseTalon
import edu.wpi.first.wpilibj2.command.Subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase
import mu.KotlinLogging
import java.lang.reflect.Field

private val logger = KotlinLogging.logger {}

class SubsystemHealthCheck(subsystem: Subsystem) {

    val name: String =
        if (subsystem is SubsystemBase) subsystem.name else subsystem::class.java.simpleName

    var isFinished: Boolean = false
        private set

    private val diagnostics = subsystem.javaClass.declaredFields
        .filter { it.isAnnotationPresent(HealthCheck::class.java) }
        .map { it.diagnosticFor(talonFromSubsystemField(subsystem, it)) }

    private lateinit var diagnosticsIterator: Iterator<Diagnostic>

    private lateinit var currentDiagnostic: Diagnostic

    private fun talonFromSubsystemField(subsystem: Subsystem, field: Field): BaseTalon {
        if (!field.trySetAccessible())
            logger.error { "trySetAccessible() failed for $name: ${field.name}" }

        return field.get(subsystem) as? BaseTalon
            ?: throw IllegalArgumentException("Subsystem $name field '${field.name}' is not a subclass of BaseTalon")
    }

    fun initialize() {
        logger.debug { "initializing $this" }
        diagnostics.forEach { it.initialize() }

        diagnosticsIterator = diagnostics.iterator()
        if (!diagnosticsIterator.hasNext()) {
            logger.warn { "Subsystem '${this.name}' has no health checks, skipping" }
            isFinished = true
            return
        }
        currentDiagnostic = diagnosticsIterator.next()

        isFinished = false
    }

    fun execute() {
        logger.debug { "execute: current is $currentDiagnostic, finished = ${currentDiagnostic.isFinished}" }

        if (currentDiagnostic.isFinished) {
            logger.debug { "execute: $currentDiagnostic is finished" }
            if (diagnosticsIterator.hasNext()) {
                currentDiagnostic = diagnosticsIterator.next()
                logger.debug { "execute: iterator has next, current = $currentDiagnostic" }
            } else {
                logger.debug { "execute: $name is finished" }
                isFinished = true
            }
            return
        }

        logger.info { "execute: running health check: $currentDiagnostic" }
        currentDiagnostic.execute()
    }

}