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
import org.strykeforce.thirdcoast.healthcheck.Test
import org.strykeforce.thirdcoast.healthcheck.groups.TalonGroup
import org.strykeforce.thirdcoast.healthcheck.statusOf
import org.strykeforce.thirdcoast.healthcheck.tests.TalonPositionTest.TalonPositionTestState.*
import kotlin.math.absoluteValue

private val logger = KotlinLogging.logger {}

/**
 * Run a Talon SRX to a position while logging encoder speed and current draw.
 *
 * @property name name of test to appear on HTML report.
 * @property currentRange range of acceptable currents for color coding HTML report.
 * @property speedRange range of acceptable encoder speeds for color coding HTML report.
 * @property warmUp time to wait after starting test before beginning to log data.
 * @property peakVoltage maximum output voltage.
 * @property encoderChangeTarget encoder position delta to move.
 * @property encoderTimeOutCount loop count after which to stop test if no delta is measured.
 *
 */
@Suppress("MemberVisibilityCanBePrivate")
class TalonPositionTest(private val group: TalonGroup) : Test, Reportable {
    override var name = "position test"
    var percentOutput = 0.0
    var currentRange = 0.0..0.0
    var speedRange = 0..0
    var warmUp = 0.25
    var peakVoltage = 12.0

    var encoderChangeTarget = 0
    var encoderTimeOutCount = 0

    private var state = STARTING
    private var startTime = 0.0
    private var iteration = 0
    private var startingPosition = 0

    private lateinit var talon: TalonSRX
    private var currents = mutableListOf<Double>()
    private var speeds = mutableListOf<Int>()


    override fun execute() {
        when (state) {
            STARTING -> {
                name = "position test at ${percentOutput * peakVoltage} volts"
                if (group.talons.size != 1) {
                    logger.error { "position test valid for one talon, has ${group.talons.size}, skipping" }
                    state = STOPPED
                    return
                }
                logger.info { "$name starting" }
                talon = group.talons.first()
                startingPosition = talon.selectedSensorPosition
                talon.set(PercentOutput, percentOutput)
                startTime = Timer.getFPGATimestamp()
                state = WARMING
            }

            WARMING -> if (Timer.getFPGATimestamp() - startTime >= warmUp) {
                state = RUNNING
            }

            RUNNING -> {
                currents.add(talon.outputCurrent)
                speeds.add(talon.selectedSensorVelocity)

                if ((talon.selectedSensorPosition - startingPosition).absoluteValue > encoderChangeTarget) {
                    logger.info { "reached encoder target $encoderChangeTarget" }
                    talon.set(PercentOutput, 0.0)
                    state = STOPPED
                    return
                }

                if (iteration++ > encoderTimeOutCount) {
                    logger.warn { "timed out waiting for encoder count, failing test" }
                    talon.set(PercentOutput, 0.0)
                    state = STOPPED
                }
            }


            STOPPED -> logger.info { "position test stopped" }

        }
    }

    override fun isFinished() = state == STOPPED

    override fun report(tagConsumer: TagConsumer<Appendable>) = reportTable(tagConsumer)

    override fun reportHeader(tagConsumer: TagConsumer<Appendable>) {
        tagConsumer.tr {
            th { +"talon ID" }
            th { +"Setpoint (volts)" }
            th { +"Position (ticks)" }
            th { +"Current (amps)" }
            th { +"Speed (ticks/100ms)" }
            th { +"Current range" }
            th { +"Speed range" }
        }
    }

    override fun reportRows(tagConsumer: TagConsumer<Appendable>) {
        val current = currents.average()
        val speed = speeds.average().toInt()

        tagConsumer.tr {
            td { +"${talon.deviceID}" }
            td { +"%.1f".format(percentOutput * peakVoltage) }
            td { +"$encoderChangeTarget" }
            td(classes = currentRange.statusOf(current)) { +"%.2f".format(current) }
            td(classes = speedRange.statusOf(speed)) { +"$speed" }
            td { +"${currentRange.start}, ${currentRange.endInclusive}" }
            td { +"${speedRange.start}, ${speedRange.endInclusive}" }
        }
    }

    @Suppress("unused")
    private enum class TalonPositionTestState {
        STARTING,
        WARMING,
        RUNNING,
        STOPPED
    }
}