package org.strykeforce.healthcheck.checks

import mu.KotlinLogging

interface HealthCheckVisitor {
    fun visit(healthCheck: RobotHealthCheck)
    fun visit(healthCheck: SubsystemHealthCheck)
    fun visit(healthCheck: TalonHealthCheck)
    fun visit(healthCheck: TalonHealthCheckCase)
}

class DumpVisitor : HealthCheckVisitor {

    private val logger = KotlinLogging.logger {}
    override fun visit(healthCheck: RobotHealthCheck) {
        logger.info { healthCheck.name }
        healthCheck.healthChecks.forEach { it.accept(this) }
    }

    override fun visit(healthCheck: SubsystemHealthCheck) {
        logger.info { "    ${healthCheck.name}" }
        healthCheck.healthChecks.forEach { it.accept(this) }
    }

    override fun visit(healthCheck: TalonHealthCheck) {
        logger.info { "        TalonHealthCheck(${healthCheck.name})" }
        healthCheck.healthChecks.forEach { it.accept(this) }
    }

    override fun visit(healthCheck: TalonHealthCheckCase) {
        logger.info { "                $healthCheck" }
    }
}