package org.strykeforce.healthcheck.old

import edu.wpi.first.wpilibj.TimedRobot
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import mu.KotlinLogging
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val HTML_PATH = "/var/local/natinst/www/healthcheck/index.html"
private val logger = KotlinLogging.logger {}

class HealthCheckRunner {
    var period = TimedRobot.kDefaultPeriod

    private val testGroups = mutableListOf<TestGroup>()
    private var state = State.STARTING
    private lateinit var iterator: Iterator<TestGroup>
    private lateinit var currentTestGroup: TestGroup

    fun talonCheck(init: TalonGroup.() -> Unit): TalonGroup {
        val test = TalonGroup(this)
        test.init()
        testGroups.add(test)
        return test
    }

    fun execute() = when (state) {
        State.STARTING -> {
            check(testGroups.isNotEmpty()) { "no test groups" }
            iterator = testGroups.iterator()
            currentTestGroup = iterator.next()
            state = State.RUNNING
        }
        State.RUNNING -> if (!currentTestGroup.isFinished()) {
            currentTestGroup.execute()
        } else if (iterator.hasNext()) {
            currentTestGroup = iterator.next()
        } else {
            state = State.STOPPING
        }
        State.STOPPING -> {
            logger.info { "health check finished" }
            state = State.STOPPED
        }
        State.STOPPED -> throw IllegalStateException()
    }

    fun isFinished() = state == State.STOPPED

    fun report() {
        File(HTML_PATH).writer().use { writer ->
            writer.appendLine("<!DOCTYPE html>")
            val tagConsumer = writer.appendHTML()
            tagConsumer.html {
                attributes["lang"] = "en"
                head {
                    title { +"Health Check" }
                    style {
                        unsafe {
                            raw(javaClass.getResource("/healthcheck.css").readText())
                        }
                    }
                }
                body {
                    h1 { +"Health Check  ${SimpleDateFormat("HH:mm:ss").format(Date())}" }
                    testGroups.forEach { it.report(tagConsumer) }
                }
            }
        }
        logger.info { "health check report: http://10.27.67.2/healthcheck/index.html" }

    }

    override fun toString(): String {
        return "HealthCheck(testGroups=$testGroups)"
    }


    private enum class State {
        STARTING,
        RUNNING,
        STOPPING,
        STOPPED
    }

}

fun healthCheck(init: HealthCheckRunner.() -> Unit): HealthCheckRunner = HealthCheckRunner().apply(init)

