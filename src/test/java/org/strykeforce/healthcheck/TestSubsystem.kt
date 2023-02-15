package org.strykeforce.healthcheck

import com.ctre.phoenix.motorcontrol.can.BaseTalon
import edu.wpi.first.wpilibj2.command.Subsystem
import org.mockito.kotlin.mock

internal class TestSubsystem : Subsystem {
    @HealthCheck
    private val talonOne = mock<BaseTalon>()


    @HealthCheck
    @Timed(percentOutput = [0.5, 1.0, -0.5, -1.0], duration = 3.0)
    private val talonTwo = mock<BaseTalon>()


    // position tests - no current or speed limits
    @HealthCheck
    @Position(percentOutput = [0.25, -0.25], encoderChange = 20000)
    private val talonThree = mock<BaseTalon>()

    // timed tests - specify limits: currentMin, currentMax, speedMin, speedMax
    @HealthCheck
    @Limits([0.5, 1.5, 1000.0, 2500.0, 0.5, 1.5, -1000.0, -2500.0])
    @Timed(percentOutput = [0.5, -0.5], duration = 4.0)
    private val talonFour = mock<BaseTalon>()

    // position test - specify limits
    @HealthCheck
    @Position(percentOutput = [0.25], encoderChange = 20000)
    @Limits([0.75, 2.0, 1500.0, 3500.0])
    private val talonFive = mock<BaseTalon>()

    // follower - just take measurements
    @HealthCheck
    @Follow(leader = 0)
    @Limits([0.5, 1.5, 1000.0, 2500.0])
    private val talonSix = mock<BaseTalon>()

}