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
        buffer.appendLine( "                    avg. voltage        = ${healthCheck.data.averageVoltage.format()} volts" )
        buffer.appendLine( "                    avg. speed          = ${healthCheck.data.averageSpeed.format()} ticks/100ms" )
        buffer.appendLine( "                    avg. supply current = ${healthCheck.data.averageSupplyCurrent.format()} amps" )
        buffer.appendLine( "                    avg. stator current = ${healthCheck.data.averageStatorCurrent.format()} amps" )
    }
}

private fun Double.format() = "%.2f".format(this)