package org.strykeforce.healthcheck

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Subsystem
import edu.wpi.first.wpilibj2.command.button.InternalButton
import kotlinx.html.BUTTON
import mu.KotlinLogging
import org.strykeforce.healthcheck.internal.*

private val logger = KotlinLogging.logger {}

class HealthCheckCommand(vararg subsystems: Subsystem) : Command() {

    companion object {
        @JvmField
        val BUTTON = InternalButton()
    }

    private val robotHealthCheckBuilder = RobotHealthCheckBuilder(*subsystems)
    private lateinit var robotHealthCheck: RobotHealthCheck

    private var isFinished: Boolean = false

    private var reportServer: ReportServer? = null

    private val subsystemSet = subsystems.toSet()
    override fun getRequirements() = subsystemSet

    override fun initialize() {
        robotHealthCheck = robotHealthCheckBuilder.build()
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
        reportServer?.stop()
        reportServer = ReportServer(robotHealthCheck)

        DumpVisitor().visit(robotHealthCheck)
        isFinished = false
    }

    override fun runsWhenDisabled() = true
}

class IOHealthCheckCommand(var requirements: List<Subsystem>, vararg ios: Checkable): Command() {

    companion object {
        @JvmField
        val BUTTON = InternalButton()
    }

    private val robotHealthCheckBuilder = RobotHealCheckIOBuilder(*ios)
    private lateinit var robotHealthCheck: RobotIOHealthCheck

    private  var isFinished: Boolean = false

    private var reportServer: ReportServerIO? = null

    private val ioSet = ios.toSet()

    private val requirementsSet = requirements.toSet()

    override fun getRequirements() = requirementsSet

    override fun initialize() {
        robotHealthCheck = robotHealthCheckBuilder.build()
        robotHealthCheck.initialize()
        BUTTON.setPressed(false)
    }

    override fun execute() {
        if(robotHealthCheck.isFinished) {
            isFinished = true
            return
        }
        robotHealthCheck.execute()
    }

    override fun isFinished() = isFinished

    override fun end(interrupted: Boolean) {
        reportServer?.stop()
        reportServer = ReportServerIO(robotHealthCheck)

        DumpVisitor().visit(robotHealthCheck)
        isFinished = false
    }

    override fun runsWhenDisabled() = true
}