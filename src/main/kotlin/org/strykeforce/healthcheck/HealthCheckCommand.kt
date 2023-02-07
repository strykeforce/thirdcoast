package org.strykeforce.healthcheck

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Subsystem
import edu.wpi.first.wpilibj2.command.button.InternalButton
import mu.KotlinLogging
import org.strykeforce.healthcheck.checks.CommandVisitor
import org.strykeforce.healthcheck.checks.DumpVisitor
import org.strykeforce.healthcheck.checks.RobotHealthCheck
import org.strykeforce.healthcheck.checks.RobotHealthCheckBuilder
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

class HealthCheckCommand(vararg subsystems: Subsystem) : Command {

    companion object {
        @JvmField
        val BUTTON = InternalButton()
    }


    private val robotHealthCheck: RobotHealthCheck = RobotHealthCheckBuilder(*subsystems).build()

    private var isFinished: Boolean = false

    private var reportServer = ReportServer()

    private val subsystemSet = subsystems.toSet()
    override fun getRequirements() = subsystemSet

    override fun initialize() {
        DumpVisitor().visit(robotHealthCheck)
        robotHealthCheck.initialize()
        BUTTON.setPressed(false)
    }

    override fun execute() {
        if (robotHealthCheck.isFinished) {
            isFinished = true
            return
        }
        robotHealthCheck.execute()
    }

    override fun isFinished() = isFinished

    override fun end(interrupted: Boolean) {
    }

    override fun runsWhenDisabled() = false
}