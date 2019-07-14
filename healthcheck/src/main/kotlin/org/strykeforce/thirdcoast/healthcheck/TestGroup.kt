package org.strykeforce.thirdcoast.healthcheck

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.table
import mu.KotlinLogging
import org.strykeforce.thirdcoast.healthcheck.TestGroup.TestGroupState.*
import org.strykeforce.thirdcoast.healthcheck.tests.TalonPosition
import org.strykeforce.thirdcoast.healthcheck.tests.TalonPositionTest
import org.strykeforce.thirdcoast.healthcheck.tests.TalonTimedTest

private val logger = KotlinLogging.logger {}

abstract class TestGroup(val healthCheck: HealthCheck) : Test {
    override var name = "name not set"

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

    @Suppress("unused")
    private enum class TestGroupState {
        STARTING,
        RUNNING,
        STOPPED
    }

}


class TalonGroup(healthCheck: HealthCheck) : TestGroup(healthCheck) {
    var talons = emptyList<TalonSRX>()

    fun timedTest(init: TalonTimedTest.() -> Unit): Test {
        val spinTest = TalonTimedTest(this)
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
