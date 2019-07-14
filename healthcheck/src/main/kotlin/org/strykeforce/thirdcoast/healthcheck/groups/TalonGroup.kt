package org.strykeforce.thirdcoast.healthcheck.groups

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.strykeforce.thirdcoast.healthcheck.HealthCheck
import org.strykeforce.thirdcoast.healthcheck.Test
import org.strykeforce.thirdcoast.healthcheck.tests.TalonPosition
import org.strykeforce.thirdcoast.healthcheck.tests.TalonPositionTest
import org.strykeforce.thirdcoast.healthcheck.tests.TalonTimedTest

class TalonGroup(healthCheck: HealthCheck) : TestGroup(healthCheck) {
    var talons = emptyList<TalonSRX>()

    @Suppress("unused")
    fun timedTest(init: TalonTimedTest.() -> Unit): Test {
        val spinTest = TalonTimedTest(this)
        spinTest.init()
        tests.add(spinTest)
        return spinTest
    }

    @Suppress("unused")
    fun positionTest(init: TalonPositionTest.() -> Unit): Test {
        val positionTest = TalonPositionTest(this)
        positionTest.init()
        tests.add(positionTest)
        return positionTest
    }

    @Suppress("unused")
    fun positionTalon(init: TalonPosition.() -> Unit): Test {
        val position = TalonPosition(this)
        position.init()
        tests.add(position)
        return position
    }
}