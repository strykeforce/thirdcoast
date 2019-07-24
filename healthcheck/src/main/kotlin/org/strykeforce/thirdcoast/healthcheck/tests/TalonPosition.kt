package org.strykeforce.thirdcoast.healthcheck.tests

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import kotlinx.html.TagConsumer
import mu.KotlinLogging
import org.strykeforce.thirdcoast.healthcheck.Test
import org.strykeforce.thirdcoast.healthcheck.groups.TalonGroup
import org.strykeforce.thirdcoast.healthcheck.tests.TalonPosition.TalonPositionState.*
import kotlin.math.absoluteValue

private val logger = KotlinLogging.logger {}


/**
 * Run a Talon SRX to a position. Current draw and encoder speeds are not logged and a report is not generated.
 *
 * @property name name of test to appear on HTML report.
 * @property controlMode talon control mode.
 * @property encoderTarget talon SRX position target (position setpoint).
 * @property encoderGoodEnough acceptable final position error.
 */
@Suppress("MemberVisibilityCanBePrivate")
class TalonPosition(private val group: TalonGroup) : Test {
    override var name = "position talon"
    var controlMode = ControlMode.MotionMagic
    var encoderTarget = 0
    var encoderGoodEnough = 10

    private var state = STARTING
    private lateinit var talon: TalonSRX

    override fun execute() {
        when (state) {
            STARTING -> {
                if (group.talons.size != 1) {
                    logger.error { "position test valid for one talon, has ${group.talons.size}, skipping" }
                    state = STOPPED
                    return
                }
                logger.info { "$name starting" }
                talon = group.talons.first()
                talon.set(controlMode, encoderTarget.toDouble())
                state = RUNNING
            }
            RUNNING -> {
                if ((encoderTarget - talon.selectedSensorPosition).absoluteValue < encoderGoodEnough) {
                    logger.info { "repositioned to $encoderTarget, finishing" }
                    state = STOPPED
                }
            }
            STOPPED -> logger.info { "position talon stopped" }
        }
    }

    override fun isFinished() = state == STOPPED

    override fun report(tagConsumer: TagConsumer<Appendable>) {}

    @Suppress("unused")
    private enum class TalonPositionState {
        STARTING,
        RUNNING,
        STOPPED
    }

}