package org.strykeforce.healthcheck.internal

import mu.KotlinLogging

interface HealthCheckVisitor {
    fun visit(healthCheck: RobotHealthCheck)
    fun visit(healthCheck: RobotIOHealthCheck)
    fun visit(healthCheck: SubsystemHealthCheck)
    fun visit(healthCheck: IOHealthCheck)
    fun visit(healthCheck: TalonHealthCheck)

    fun visit(healthCheck: P6TalonHealthCheck)
    fun visit(healthCheck: TalonHealthCheckCase)

    fun visit(healthCheck: P6TalonHealthCheckCase)

    fun visit(healthCheck: LifecycleHealthCheck)
    fun visit(healthCheck: LifecycleIOHealthCheck)
}

class DumpVisitor : HealthCheckVisitor {

    val buffer = StringBuilder()

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

    override fun visit(healthCheck: RobotIOHealthCheck) {
        buffer.appendLine()
        buffer.appendLine()
        buffer.appendLine(healthCheck.name)
        healthCheck.healthChecks.forEach{ it.accept(this)}
        buffer.appendLine()
        buffer.appendLine()
        logger.info { buffer }
    }

    override fun visit(healthCheck: SubsystemHealthCheck) {
        buffer.appendLine("    ${healthCheck.name}")
        healthCheck.healthChecks.forEach { it.accept(this) }
    }

    override fun visit(healthCheck: IOHealthCheck) {
        buffer.appendLine("    ${healthCheck.name}")
        healthCheck.healthChecks.forEach{ it.accept(this)}
    }

    override fun visit(healthCheck: LifecycleHealthCheck) {
        buffer.appendLine("        BeforeHealthCheck(${healthCheck.name})")
    }

    override fun visit(healthCheck: LifecycleIOHealthCheck) {
        buffer.appendLine("        BeforeHealthCheck(${healthCheck.name})")
    }

    override fun visit(healthCheck: TalonHealthCheck) {
        buffer.appendLine("        TalonHealthCheck(${healthCheck.name})")
        healthCheck.healthChecks.forEach { it.accept(this) }
    }

    override fun visit(healthCheck: P6TalonHealthCheck) {
        buffer.appendLine("        P6TalonHealthCheck(${healthCheck.name})")
        healthCheck.healthChecks.forEach { it.accept(this) }
    }

    override fun visit(healthCheck: TalonHealthCheckCase) {
        buffer.appendLine("                $healthCheck: ${healthCheck.data.size} measurements")
        healthCheck.data.forEach {
            buffer.appendLine("                    talon ${it.id} avg. voltage        = ${it.averageVoltage.format()} volts")
            buffer.appendLine("                    talon ${it.id} avg. speed          = ${it.averageSpeed.format()} ticks/100ms")
            buffer.appendLine("                    talon ${it.id} avg. supply current = ${it.averageSupplyCurrent.format()} amps")
            buffer.appendLine("                    talon ${it.id} avg. stator current = ${it.averageStatorCurrent.format()} amps")
        }
    }

    override fun visit(healthCheck: P6TalonHealthCheckCase) {
        buffer.appendLine("                $healthCheck: ${healthCheck.data.size} measurements")
        healthCheck.data.forEach {

        }
    }
}

private fun Double.format() = "%.2f".format(this)