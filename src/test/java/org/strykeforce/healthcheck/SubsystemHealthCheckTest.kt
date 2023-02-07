package org.strykeforce.healthcheck

import com.ctre.phoenix.motorcontrol.can.BaseTalon
import edu.wpi.first.wpilibj2.command.Subsystem
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class SubsystemHealthCheckTest {

    @Test
    fun `health check on non-talon class throws exception`() {
        assertThrows(IllegalArgumentException::class.java) { LegacySubsystemHealthCheck(NonTalonSubsystem()) }
    }

    @Test
    fun `health check on null talon throws exception`() {
        assertThrows(IllegalArgumentException::class.java) { LegacySubsystemHealthCheck(NullTalonSubsystem()) }
    }

    @Test
    fun `health check on empty subsystem is finished`() {
        val shc = LegacySubsystemHealthCheck(EmptySubsystem())
        assertTrue(shc.isFinished)
    }

    internal class EmptySubsystem : Subsystem {}

    internal class NonTalonSubsystem : Subsystem {
        // not a talon - should throw
        @HealthCheck
        private val talonSeven = "Ha Ha"
    }

    internal class NullTalonSubsystem : Subsystem {
        // null - should throw
        @HealthCheck
        private val talonEight: BaseTalon? = null
    }

}