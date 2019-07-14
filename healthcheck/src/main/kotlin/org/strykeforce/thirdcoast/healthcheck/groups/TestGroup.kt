package org.strykeforce.thirdcoast.healthcheck.groups

import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.table
import mu.KotlinLogging
import org.strykeforce.thirdcoast.healthcheck.HealthCheck
import org.strykeforce.thirdcoast.healthcheck.Reportable
import org.strykeforce.thirdcoast.healthcheck.Test
import org.strykeforce.thirdcoast.healthcheck.groups.TestGroup.TestGroupState.*

abstract class TestGroup(val healthCheck: HealthCheck) : Test {
    override var name = "name not set"
    private val logger = KotlinLogging.logger {}
    protected val tests = mutableListOf<Test>()
    private var state = STARTING
    private lateinit var iterator: Iterator<Test>
    private lateinit var currentTest: Test


    override fun execute() = when (state) {
        STARTING -> {
            logger.info { "$name starting" }
            check(tests.isNotEmpty()) { "no tests in test group '$name'" }
            iterator = tests.iterator()
            currentTest = iterator.next()
            state = RUNNING
        }

        RUNNING -> if (!currentTest.isFinished()) {
            currentTest.execute()
        } else if (iterator.hasNext()) {
            currentTest = iterator.next()
        } else {
            logger.info { "$name finished" }
            state = STOPPED
        }

        STOPPED -> throw IllegalStateException()
    }

    override fun isFinished() = state == STOPPED

    override fun report(tagConsumer: TagConsumer<Appendable>) {
        tagConsumer.div {
            h2 { +name }
            table {
                val reportable = tests.filterIsInstance<Reportable>()
                if (reportable.isNotEmpty()) {
                    reportable.first().apply { reportHeader(tagConsumer) }
                    reportable.forEach { it.reportRows(tagConsumer) }
                }
            }
        }
    }


    override fun toString(): String {
        return "TestGroup(name='$name', tests=$tests)"
    }

    @Suppress("unused")
    private enum class TestGroupState {
        STARTING,
        RUNNING,
        STOPPED
    }

}
