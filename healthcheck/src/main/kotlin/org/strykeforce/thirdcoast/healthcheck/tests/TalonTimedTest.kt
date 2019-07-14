package org.strykeforce.thirdcoast.healthcheck.tests

import com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.Timer
import kotlinx.html.TagConsumer
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging
import org.strykeforce.thirdcoast.healthcheck.Reportable
import org.strykeforce.thirdcoast.healthcheck.TalonGroup
import org.strykeforce.thirdcoast.healthcheck.Test
import org.strykeforce.thirdcoast.healthcheck.statusOf
import org.strykeforce.thirdcoast.healthcheck.tests.TalonTimedTest.TalonTimedTestState.*
import kotlin.math.roundToInt

private val logger = KotlinLogging.logger {}

@Suppress("MemberVisibilityCanBePrivate")
class TalonTimedTest(private val group: TalonGroup) : Test, Reportable {
    override var name = "talon timed test"
    var percentOutput = 0.0
    var currentRange = 0.0..0.0
    var speedRange = 0..0
    var warmUp = 0.25
    var duration = 2.0

    private var state = STARTING
    private var startTime = 0.0
    private var iterations = 0
    private var iteration = 0
    private lateinit var talonCurrents: Map<TalonSRX, MutableList<Double>>
    private lateinit var talonSpeeds: Map<TalonSRX, MutableList<Int>>

    override fun execute() {
        when (state) {
            STARTING -> {
                name = "timed test at ${percentOutput * 12.0} volts"
                logger.info { "$name starting" }
                iterations = (duration / group.healthCheck.period).roundToInt()
                talonCurrents = group.talons.associateWith { mutableListOf<Double>() }
                talonSpeeds = group.talons.associateWith { mutableListOf<Int>() }
                group.talons.forEach { it.set(PercentOutput, percentOutput) }
                startTime = Timer.getFPGATimestamp()
                state = WARMING
            }

            WARMING -> if (Timer.getFPGATimestamp() - startTime >= warmUp) {
                state = RUNNING
            }

            RUNNING -> {
                talonCurrents.forEach { (talon, currents) -> currents.add(talon.outputCurrent) }
                talonSpeeds.forEach { (talon, speeds) -> speeds.add(talon.selectedSensorVelocity) }
                if (++iteration == iterations) state = STOPPING
            }

            STOPPING -> {
                group.talons.forEach { it.set(PercentOutput, 0.0) }

                talonCurrents.forEach { (talon, currents) ->
                    logger.info { "talon ${talon.deviceID} average current = ${currents.average()}" }
                }
                talonSpeeds.forEach { (talon, speeds) ->
                    logger.info { "talon ${talon.deviceID} average speed = ${speeds.average()}" }
                }
                logger.info { "timed test finished" }
                state = STOPPED
            }

            STOPPED -> logger.info { "timed test stopped" }

        }
    }

    override fun isFinished() = state == STOPPED

    override fun report(tagConsumer: TagConsumer<Appendable>) = reportTable(tagConsumer)

    override fun reportHeader(tagConsumer: TagConsumer<Appendable>) {
        tagConsumer.tr {
            th { +"talon ID" }
            th { +"Setpoint (volts)" }
            th { +"Duration (sec)" }
            th { +"Current (amps)" }
            th { +"Speed (ticks/100ms)" }
            th { +"Current range" }
            th { +"Speed range" }
        }
    }

    override fun reportRows(tagConsumer: TagConsumer<Appendable>) {
        group.talons.forEach {
            val current = talonCurrents[it]?.average() ?: 0.0
            val speed = talonSpeeds[it]?.average()?.toInt() ?: 0
            tagConsumer.tr {
                td { +"${it.deviceID}" }
                td { +"%.2f".format(percentOutput * 12.0) }
                td { +"%.2f".format(duration) }
                td(classes = currentRange.statusOf(current)) { +"%.2f".format(current) }
                td(classes = speedRange.statusOf(speed)) { +"$speed" }
                td { +"${currentRange.start}, ${currentRange.endInclusive}" }
                td { +"${speedRange.start}, ${speedRange.endInclusive}" }
            }
        }
    }

    @Suppress("unused")
    private enum class TalonTimedTestState {
        STARTING,
        WARMING,
        RUNNING,
        STOPPING,
        STOPPED
    }
}


