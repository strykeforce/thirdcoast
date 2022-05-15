package org.strykeforce.healthcheck.tests

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.BaseTalon
import edu.wpi.first.wpilibj.Timer
import kotlinx.html.TagConsumer
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging
import org.strykeforce.healthcheck.old.Reportable
import org.strykeforce.healthcheck.old.TalonGroup
import org.strykeforce.healthcheck.old.Test
import org.strykeforce.healthcheck.old.statusOf
import kotlin.math.roundToInt

private val logger = KotlinLogging.logger { }

class TalonFollowerTimedTest(private val group: TalonGroup) : Test, Reportable {
    override var name = "talon follower timed test"
    var percentOutput = 0.0
    var supplyCurrentRange = 0.0..0.0
    var statorCurrentRange = 0.0..0.0
    var speedRange = 0..0
    var warmUp = 1.0
    var duration = 2.0

    private var state = State.STARTING
    private var startTime = 0.0
    private var iterations = 0
    private var iteration = 0
    private lateinit var talonSupplyCurrents: Map<BaseTalon, MutableList<Double>>
    private lateinit var talonStatorCurrents: Map<BaseTalon, MutableList<Double>>
    private lateinit var talonSpeeds: Map<BaseTalon, MutableList<Int>>

    override fun execute() {
        when (state) {
            State.STARTING -> {
                name = "timed test (follower) at ${percentOutput * 12.0} volts"
                if (group.talons.size != 2) {
                    logger.error { "follower timed test valid for two talon, has ${group.talons.size}, skipping" }
                    state = State.STOPPED
                    return
                }
                logger.info { "$name starting" }
                iterations = (duration / group.healthCheckRunner.period).roundToInt()
                talonSupplyCurrents = group.talons.associateWith { mutableListOf<Double>() }
                talonStatorCurrents = group.talons.associateWith { mutableListOf<Double>() }
                talonSpeeds = group.talons.associateWith { mutableListOf<Int>() }
                group.talons.forEach { it.configOpenloopRamp(0.75 * warmUp) }
                group.talons[1].follow(group.talons[0])
                group.talons[0].set(ControlMode.PercentOutput, percentOutput)
                startTime = Timer.getFPGATimestamp()
                state = State.WARMING
            }
            State.WARMING -> if (Timer.getFPGATimestamp() - startTime >= warmUp) {
                state = State.RUNNING
            }
            State.RUNNING -> {
                talonSupplyCurrents.forEach { (talon, supplyCurrents) -> supplyCurrents.add(talon.supplyCurrent) }
                talonStatorCurrents.forEach { (talon, statorCurrents) -> statorCurrents.add(talon.statorCurrent) }
                talonSpeeds.forEach { (talon, speeds) -> speeds.add(talon.selectedSensorVelocity.toInt()) }
                if (++iteration == iterations) state = State.STOPPING
            }
            State.STOPPING -> {
                group.talons[1].follow(group.talons[0])
                group.talons[0].set(ControlMode.PercentOutput, 0.0)
                group.talons.forEach { it.configOpenloopRamp(0.0) }

                talonSupplyCurrents.forEach { talon, supplyCurrents ->
                    logger.info { "talon ${talon.deviceID} average supply current = ${supplyCurrents.average()}" }
                }

                talonStatorCurrents.forEach { talon, statorCurrents ->
                    logger.info { "talon ${talon.deviceID} average stator current = ${statorCurrents.average()}" }
                }

                talonSpeeds.forEach { talon, speeds ->
                    logger.info { "talon ${talon.deviceID} average speed = ${speeds.average()}" }
                }
                logger.info { "timed test finished" }
                state = State.STOPPED
            }
            State.STOPPED -> logger.info { "timed test stopped" }
        }
    }

    override fun isFinished() = state == State.STOPPED

    override fun report(tagConsumer: TagConsumer<Appendable>) = reportTable(tagConsumer)

    override fun reportHeader(tagConsumer: TagConsumer<Appendable>) {
        tagConsumer.tr {
            th { +"talon Id" }
            th { +"Setpoint (volts)" }
            th { +"Duration (sec)" }
            th { +"Supply Current (amps)" }
            th { +"Stator Current (amps)" }
            th { +"Speed (ticks/100ms)" }
            th { +"Supply Current range" }
            th { +"Stator Current range" }
            th { +"Speed range" }
        }
    }

    override fun reportRows(tagConsumer: TagConsumer<Appendable>) {
        group.talons.forEach {
            val supplyCurrent = talonSupplyCurrents[it]?.average() ?: 0.0
            val statorCurrent = talonStatorCurrents[it]?.average() ?: 0.0
            val speed = talonSpeeds[it]?.average()?.toInt() ?: 0
            tagConsumer.tr {
                td { +"${it.deviceID}" }
                td { +"%.2f".format(percentOutput * 12.0) }
                td { +"%.2f".format(duration) }
                td(classes = supplyCurrentRange.statusOf(supplyCurrent)) { +"%.2f".format(supplyCurrent) }
                td(classes = statorCurrentRange.statusOf(statorCurrent)) { +"%.2f".format(statorCurrent) }
                td(classes = speedRange.statusOf(speed)) { +"$speed" }
                td { +"${supplyCurrentRange.start}, ${supplyCurrentRange.endInclusive}" }
                td { +"${statorCurrentRange.start}, ${statorCurrentRange.endInclusive}" }
                td { +"${speedRange.start}, ${speedRange.endInclusive}" }
            }
        }
    }


    private enum class State {
        STARTING,
        WARMING,
        RUNNING,
        STOPPING,
        STOPPED
    }
}