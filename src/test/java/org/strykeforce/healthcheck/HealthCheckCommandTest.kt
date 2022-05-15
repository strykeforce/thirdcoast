package org.strykeforce.healthcheck

import edu.wpi.first.wpilibj2.command.Subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class HealthCheckCommandTest {

    @Test
    fun `health check with no subsystems throws exception`() {
        assertThrows(IllegalArgumentException::class.java) { HealthCheckCommand() }
    }


    @Test
    fun `constructor adds subsystems to requirements`() {
        val subsystems = setOf(AlphaSubsystem(), BetaSubsystem(), GammaSubsystem())
        val command = HealthCheckCommand(*subsystems.toTypedArray())
        assertEquals(subsystems, command.requirements)
    }
}

internal class AlphaSubsystem : Subsystem
internal class BetaSubsystem : Subsystem
internal class GammaSubsystem : Subsystem