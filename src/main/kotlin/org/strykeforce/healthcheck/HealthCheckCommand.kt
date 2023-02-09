package org.strykeforce.healthcheck

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Subsystem
import edu.wpi.first.wpilibj2.command.button.InternalButton
import mu.KotlinLogging
import org.strykeforce.healthcheck.internal.DumpVisitor
import org.strykeforce.healthcheck.internal.ReportServer
import org.strykeforce.healthcheck.internal.RobotHealthCheck
import org.strykeforce.healthcheck.internal.RobotHealthCheckBuilder

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
        DumpVisitor().visit(robotHealthCheck)
        isFinished = false
    }

    override fun runsWhenDisabled() = true
}