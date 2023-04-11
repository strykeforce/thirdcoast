package org.strykeforce.healthcheck.internal

import com.ctre.phoenix.motorcontrol.can.BaseTalon
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.ByteArrayOutputStream

class JsonVisitorTest {

    @Test
    @Disabled
    fun testJson() {
        val os = ByteArrayOutputStream()
        os.use {
            val jsonVisitor = JsonVisitor(it)
            jsonVisitor.visit(newRobotHealthCheck())
            assertEquals("foo", it.toString())

        }

    }

    @Test
    @Disabled
    fun testDump() {
        val dumpVisitor = DumpVisitor()
        dumpVisitor.visit(newRobotHealthCheck())
        assertEquals("foo", dumpVisitor.buffer.toString())
    }

    @Test
    @Disabled
    fun `RobotHealthCheck contains correct data`() {
        val robotHealthCheck = newRobotHealthCheck()
        assertEquals(1, robotHealthCheck.healthChecks.size)
        val subsystemHealthCheck = robotHealthCheck.healthChecks.first() as SubsystemHealthCheck
        assertEquals(1, subsystemHealthCheck.healthChecks.size)
        val talonHealthCheck = subsystemHealthCheck.healthChecks.first() as TalonHealthCheck
        assertEquals(2, talonHealthCheck.healthChecks.size)
        val talonHealthCheckCase = talonHealthCheck.healthChecks.first() as TalonHealthCheckCase
        assertEquals(3, talonHealthCheckCase.data.size)
        val data = talonHealthCheckCase.data.first()
        assertEquals(33, data.deviceId)
        assertEquals(20, data.timestamp.first())
        assertEquals(3.0, data.voltage.first())
        assertEquals(10_000.0, data.position.first())
        assertEquals(5_000.0, data.speed.first())
        assertEquals(0.75, data.supplyCurrent.first())
        assertEquals(1.5, data.statorCurrent.first())
    }
}


private fun newRobotHealthCheck(): RobotHealthCheck {

    val talon = mock<BaseTalon>()
    val case = 0
    whenever(talon.deviceID).thenReturn(33)

    val data1 = TalonHealthCheckData(0, talon).apply {
        timestamp.add(20)
        voltage.add(3.0)
        position.add(10_000.0)
        speed.add(5000.0)
        supplyCurrent.add(0.75)
        statorCurrent.add(1.5)
    }
    val data2 = TalonHealthCheckData(0, talon).apply {
        timestamp.add(40)
        voltage.add(3.1)
        position.add(10_00.1)
        speed.add(5000.1)
        supplyCurrent.add(0.76)
        statorCurrent.add(1.6)
    }

    val case1 = object : TalonHealthCheckCase(talon, false, "TestType", 1.0, 1_000) {
        override val name = "Case 1"
        override fun isRunning(elapsed: Long) = false
        override fun setTalon(talon: BaseTalon) = Unit
    }.apply {
        data.add(data1)
        data.add(data2)
    }

    val case2 = object : TalonHealthCheckCase(talon, false, "TestType", 1.0, 1_000) {
        override val name = "Case 2"
        override fun isRunning(elapsed: Long) = false
        override fun setTalon(talon: BaseTalon) = Unit
    }.apply {
        data.add(data1)
        data.add(data2)
    }
    val talonHealthCheck = object : TalonHealthCheck(talon, listOf(case1, case2)) {}


    val subsystemHealthCheck = SubsystemHealthCheck("TestSubsystem", listOf(talonHealthCheck))
    return RobotHealthCheck("RobotHealthCheck", listOf(subsystemHealthCheck))
}