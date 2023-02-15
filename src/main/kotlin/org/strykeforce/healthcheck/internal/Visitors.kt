package org.strykeforce.healthcheck.internal

import mu.KotlinLogging

interface HealthCheckVisitor {
    fun visit(healthCheck: RobotHealthCheck)
    fun visit(healthCheck: SubsystemHealthCheck)
    fun visit(healthCheck: TalonHealthCheck)
    fun visit(healthCheck: TalonHealthCheckCase)
}

class DumpVisitor : HealthCheckVisitor {

    private val buffer = StringBuilder()

    private val logger = KotlinLogging.logger {}
    override fun visit(healthCheck: RobotHealthCheck) {
        buffer.appendLine()
        buffer.appendLine()
        buffer.appendLine(healthCheck.name)
        healthCheck.healthChecks.forEach { it.accept(this) }
        buffer.appendLine()
        buffer.appendLine()
        logger.info { buffer }
    }

    override fun visit(healthCheck: SubsystemHealthCheck) {
        buffer.appendLine("    ${healthCheck.name}")
        healthCheck.healthChecks.forEach { it.accept(this) }
    }

    override fun visit(healthCheck: TalonHealthCheck) {
        buffer.appendLine("        TalonHealthCheck(${healthCheck.name})")
        healthCheck.healthChecks.forEach { it.accept(this) }
    }

    override fun visit(healthCheck: TalonHealthCheckCase) {
        buffer.appendLine("                $healthCheck")
        healthCheck.data.forEach {
            buffer.appendLine("                    talon ${it.id} avg. voltage        = ${it.averageVoltage.format()} volts")
            buffer.appendLine("                    talon ${it.id} avg. speed          = ${it.averageSpeed.format()} ticks/100ms")
            buffer.appendLine("                    talon ${it.id} avg. supply current = ${it.averageSupplyCurrent.format()} amps")
            buffer.appendLine("                    talon ${it.id} avg. stator current = ${it.averageStatorCurrent.format()} amps")
        }
    }
}

private fun Double.format() = "%.2f".format(this)