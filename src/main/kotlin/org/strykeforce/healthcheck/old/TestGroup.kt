package org.strykeforce.healthcheck.old

import com.ctre.phoenix.motorcontrol.can.BaseTalon
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.table
import org.strykeforce.healthcheck.tests.*

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

abstract class TestGroup(val healthCheckRunner: HealthCheckRunner) : Test {
    override var name = "name not set"

    protected val tests = mutableListOf<Test>()
    private var state = State.STARTING
    private lateinit var iterator: Iterator<Test>
    private lateinit var currentTest: Test


    override fun execute() = when (state) {
        State.STARTING -> {
            logger.info { "$name starting" }
            check(tests.isNotEmpty()) { "no tests in test group '$name'" }
            iterator = tests.iterator()
            currentTest = iterator.next()
            state = State.RUNNING
        }

        State.RUNNING -> if (!currentTest.isFinished()) {
            currentTest.execute()
        } else if (iterator.hasNext()) {
            currentTest = iterator.next()
        } else {
            logger.info { "$name finished" }
            state = State.STOPPED
        }

        State.STOPPED -> throw IllegalStateException()
    }

    override fun isFinished() = state == State.STOPPED

    override fun report(tagConsumer: TagConsumer<Appendable>) {
        tagConsumer.div {
            h2 { +name }
            table {
                val reportable = tests.filterIsInstance<Reportable>()
                if (!reportable.isEmpty()) {
                    reportable.first().apply { reportHeader(tagConsumer) }
                    reportable.forEach { it.reportRows(tagConsumer) }
                }
            }
        }
    }


    override fun toString(): String {
        return "TestGroup(name='$name', tests=$tests)"
    }


    private enum class State {
        STARTING,
        RUNNING,
        STOPPED
    }

}


class TalonGroup(healthCheckRunner: HealthCheckRunner) : TestGroup(healthCheckRunner) {
    var talons = emptyList<BaseTalon>()

    fun timedTest(init: TalonTimedTest.() -> Unit): Test {
        val spinTest = TalonTimedTest(this)
        spinTest.init()
        tests.add(spinTest)
        return spinTest
    }

    fun followerTimedTest(init: TalonFollowerTimedTest.() -> Unit): Test {
        val spinTest = TalonFollowerTimedTest(this)
        spinTest.init()
        tests.add(spinTest)
        return spinTest
    }

    fun positionTest(init: TalonPositionTest.() -> Unit): Test {
        val positionTest = TalonPositionTest(this)
        positionTest.init()
        tests.add(positionTest)
        return positionTest
    }

    fun positionTalon(init: TalonPosition.() -> Unit): Test {
        val position = TalonPosition(this)
        position.init()
        tests.add(position)
        return position
    }
}
