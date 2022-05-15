package org.strykeforce.healthcheck

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Subsystem
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class HealthCheckCommand(vararg subsystems: Subsystem) : Command {

    init {
        require(subsystems.isNotEmpty()) {
            throw IllegalArgumentException("at least one Subsystem must be provided")
        }
    }

    private val subsystemSet = subsystems.toMutableSet()
    private var isFinished: Boolean = false

    private val healthChecks: List<SubsystemHealthCheck> =
        subsystems.map { SubsystemHealthCheck(it) }

    private lateinit var healthChecksIterator: Iterator<SubsystemHealthCheck>

    private lateinit var currentHealthCheck: SubsystemHealthCheck

    override fun getRequirements() = subsystemSet

    override fun initialize() {
        healthChecks.forEach { it.initialize() }
        healthChecksIterator = healthChecks.iterator()
        currentHealthCheck = healthChecksIterator.next()
        isFinished = false
    }

    override fun execute() {
        logger.debug { "execute: current is $currentHealthCheck, finished = ${currentHealthCheck.isFinished}" }
        if (currentHealthCheck.isFinished) {
            logger.debug { "execute: $currentHealthCheck is finished" }
            if (healthChecksIterator.hasNext()) {
                currentHealthCheck = healthChecksIterator.next()
                logger.debug { "execute: iterator has next, current = $currentHealthCheck" }
            } else {
                logger.debug { "execute: $this is finished" }
                isFinished = true
            }
            return
        }
        currentHealthCheck.execute()
    }

    override fun isFinished() = isFinished

    override fun runsWhenDisabled() = true
}