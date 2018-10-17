package org.strykeforce.thirdcoast.telemetry.item

import edu.wpi.first.wpilibj.Servo
import org.strykeforce.thirdcoast.telemetry.grapher.Measure
import java.util.*
import java.util.function.DoubleSupplier

private const val TYPE = "servo"

/** Represents a [Servo] telemetry-enable Item.  */
class ServoItem @JvmOverloads constructor(private val servo: Servo, description: String = "Servo " + servo.channel) :
    AbstractItem(TYPE, description, MEASURES) {

    override fun deviceId(): Int {
        return servo.channel
    }

    override fun measurementFor(measure: Measure): DoubleSupplier {
        if (!MEASURES.contains(measure)) {
            throw IllegalArgumentException("invalid measure: " + measure.name)
        }
        return when (measure) {
            Measure.POSITION -> DoubleSupplier { servo.position }
            Measure.ANGLE -> DoubleSupplier { servo.angle }
            else -> throw AssertionError(measure)
        }
    }

    /**
     * Indicates if some other `ServoItem` has the same underlying `Servo` as this one.
     *
     * @param other the reference object with which to compare.
     * @return true if this Servo has the same channel ID, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is ServoItem) {
            return false
        }
        val item = other as ServoItem?
        return item!!.servo.channel == servo.channel
    }

    /**
     * Returns a hashcode value for this ServoItem.
     *
     * @return a hashcode value for this ServoItem.
     */
    override fun hashCode(): Int {
        return servo.channel
    }

    override fun toString(): String {
        return "ServoItem{" + "servo=" + servo + "} " + super.toString()
    }

    companion object {
        val MEASURES: Set<Measure> = Collections.unmodifiableSet(EnumSet.of(Measure.POSITION, Measure.ANGLE))
    }
}
