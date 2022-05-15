package org.strykeforce.healthcheck.tests

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.BaseTalon
import kotlinx.html.TagConsumer
import mu.KotlinLogging
import org.strykeforce.healthcheck.old.TalonGroup
import org.strykeforce.healthcheck.old.Test
import kotlin.math.absoluteValue

private val logger = KotlinLogging.logger {}

class TalonPosition(private val group: TalonGroup) : Test {
    override var name = "position talon"
    var controlMode = ControlMode.MotionMagic
    var encoderTarget = 0
    var encoderGoodEnough = 10

    private var state = State.STARTING
    private lateinit var talon: BaseTalon

    override fun execute() {
        when (state) {
            State.STARTING -> {
                if (group.talons.size != 1) {
                    logger.error { "position test valid for one talon, has ${group.talons.size}, skipping" }
                    state = State.STOPPED
                    return
                }
                logger.info { "$name starting" }
                talon = group.talons.first()
                talon.set(controlMode, encoderTarget.toDouble())
                state = State.RUNNING
            }
            State.RUNNING -> {
                if ((encoderTarget - talon.selectedSensorPosition).absoluteValue < encoderGoodEnough) {
                    logger.info { "repositioned to $encoderTarget, finishing" }
                    state = State.STOPPED
                }
            }
            State.STOPPED -> logger.info { "position talon stopped" }
        }
    }

    override fun isFinished() = state == State.STOPPED

    override fun report(tagConsumer: TagConsumer<Appendable>) {}

    private enum class State {
        STARTING,
        RUNNING,
        STOPPED
    }
}