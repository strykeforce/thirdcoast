package org.strykeforce.healthcheck

import com.ctre.phoenix.motorcontrol.can.BaseTalon
import edu.wpi.first.wpilibj2.command.Subsystem
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import java.lang.reflect.Field

internal class HealthCheckTest {
    private val subsystem = TestSubsystem()

    @Test
    fun `get default health check`() {
        val field: Field = subsystem::class.java.getDeclaredField("talonOne")
        assertTrue(field.trySetAccessible())
        val healthCheck = field.diagnosticFor(field.get(subsystem) as BaseTalon) as TimedDiagnostic
        assertEquals(5.0, healthCheck.duration)
        assertArrayEquals(
            doubleArrayOf(0.25, 0.5, 0.75, -0.25, -0.5, -0.75),
            healthCheck.percentOutput
        )
        assertNull(healthCheck.limits)
    }

    @Test
    fun `get timed health check`() {
        val field: Field = subsystem::class.java.getDeclaredField("talonTwo")
        assertTrue(field.trySetAccessible())
        val healthCheck = field.diagnosticFor(field.get(subsystem) as BaseTalon) as TimedDiagnostic
        assertEquals(3.0, healthCheck.duration)
        assertArrayEquals(
            doubleArrayOf(0.5, 1.0, -0.5, -1.0),
            healthCheck.percentOutput
        )
        assertNull(healthCheck.limits)

    }

    @Test
    fun `get timed health check with limits`() {
        val field = subsystem::class.java.getDeclaredField("talonFour")
        assertTrue(field.trySetAccessible())
        val healthCheck = field.diagnosticFor(field.get(subsystem) as BaseTalon) as TimedDiagnostic
        assertEquals(4.0, healthCheck.duration)
        assertArrayEquals(
            doubleArrayOf(0.5, -0.5),
            healthCheck.percentOutput
        )
        assertArrayEquals(
            doubleArrayOf(0.5, 1.5, 1000.0, 2500.0, 0.5, 1.5, -1000.0, -2500.0),
            healthCheck.limits
        )
    }

    @Test
    fun `get position health check`() {
        val field = subsystem::class.java.getDeclaredField("talonThree")
        assertTrue(field.trySetAccessible())
        val healthCheck = field.diagnosticFor(field.get(subsystem) as BaseTalon) as PositionDiagnostic
        assertEquals(20_000, healthCheck.encoderChange)
        assertArrayEquals(
            doubleArrayOf(0.25, -0.25),
            healthCheck.percentOutput
        )
        assertNull(healthCheck.limits)
    }

    @Test
    fun `get position health check with limits`() {
        val field = subsystem::class.java.getDeclaredField("talonFive")
        assertTrue(field.trySetAccessible())
        val healthCheck = field.diagnosticFor(field.get(subsystem) as BaseTalon) as PositionDiagnostic
        assertEquals(20_000, healthCheck.encoderChange)
        assertArrayEquals(
            doubleArrayOf(0.25),
            healthCheck.percentOutput
        )
        assertArrayEquals(
            doubleArrayOf(0.75, 2.0, 1500.0, 3500.0),
            healthCheck.limits
        )
    }

    // Subsystem


}

