package org.strykeforce.thirdcoast.healthcheck.tests

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.BaseTalon
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
import kotlin.math.absoluteValue

private val logger = KotlinLogging.logger { }

class TalonPositionTest(private val group: TalonGroup) : Test, Reportable {
    override var name = "position test"
    var percentOutput = 0.0
    var supplyCurrentRange = 0.0..0.0
    var statorCurrentRange = 0.0..0.0
    var speedRange = 0..0
    var warmUp = 0.5
    var peakVoltage = 12.0

    var encoderChangeTarget = 0
    var encoderGoodEnough = 5
    var encoderTimeCount = 0

    private var state = State.STARTING
    private var startTime = 0.0
    private var iteration = 0
    private var startingPosition = 0

    private lateinit var talon: BaseTalon
    private var supplyCurrents = mutableListOf<Double>()
    private var statorCurrents = mutableListOf<Double>()
    private var speeds = mutableListOf<Int>()

    override fun execute() {
        when (state) {
            State.STARTING -> {
                name = "position test at ${percentOutput * peakVoltage} volts"
                if (group.talons.size != 1) {
                    logger.error { "position test valid for one talon, has ${group.talons.size}, skipping" }
                    state = State.STOPPED
                    return
                }
                logger.info { "$name starting" }
                talon = group.talons.first()
                startingPosition = talon.selectedSensorPosition
                talon.configOpenloopRamp(0.75 * warmUp)
                talon.set(ControlMode.PercentOutput, percentOutput)
                startTime = Timer.getFPGATimestamp()
                state = State.WARMING
            }
            State.WARMING -> if (Timer.getFPGATimestamp() - startTime >= warmUp) {
                state = State.RUNNING
            }
            State.RUNNING -> {
                supplyCurrents.add(talon.supplyCurrent)
                statorCurrents.add(talon.statorCurrent)
                speeds.add(talon.selectedSensorVelocity)

                if ((talon.selectedSensorPosition - startingPosition).absoluteValue > encoderChangeTarget) {
                    logger.info { "reached encoder target $encoderChangeTarget" }
                    talon.set(ControlMode.PercentOutput, 0.0)
                    state = State.STOPPED
                    return
                }

                if (iteration++ > encoderTimeCount) {
                    logger.warn { "timed out waiting for encoder count, failing test" }
                    talon.set(ControlMode.PercentOutput, 0.0)
                    talon.configOpenloopRamp(0.0)
                    state = State.STOPPED
                }
            }
            State.STOPPED -> logger.info { "position test stopped" }
        }
    }

    override fun isFinished() = state == State.STOPPED

    override fun report(tagConsumer: TagConsumer<Appendable>) = reportTable(tagConsumer)

    override fun reportHeader(tagConsumer: TagConsumer<Appendable>) {
        tagConsumer.tr {
            th { +"talon ID" }
            th { +"Setpoint (volts)" }
            th { +"Position (ticks" }
            th { +"Supply Current (amps" }
            th { +"Stator Current (amps)" }
            th { +"Speed (ticks/100ms)" }
            th { +"Supply Current range" }
            th { +"Stator Current range" }
            th { +"Speed range" }
        }
    }

    override fun reportRows(tagConsumer: TagConsumer<Appendable>) {
        val supplyCurrent = supplyCurrents.average()
        val statorCurrent = statorCurrents.average()
        val speed = speeds.average().toInt()

        tagConsumer.tr {
            td { +"${talon.deviceID}" }
            td { +"%.1f".format((percentOutput * peakVoltage)) }
            td { +"$encoderChangeTarget" }
            td(classes = supplyCurrentRange.statusOf(supplyCurrent)) { +"%.2f".format(supplyCurrent) }
            td(classes = statorCurrentRange.statusOf(statorCurrent)) { +"%.2f".format(statorCurrent) }
            td(classes = speedRange.statusOf(speed)) { +"$speed" }
            td { +"${supplyCurrentRange.start}, ${supplyCurrentRange.endInclusive}" }
            td { +"${statorCurrentRange.start}, ${statorCurrentRange.endInclusive}" }
            td { +"${speedRange.start}, ${speedRange.endInclusive}" }
        }
    }

    private enum class State {
        STARTING,
        WARMING,
        RUNNING,
        STOPPED
    }
}