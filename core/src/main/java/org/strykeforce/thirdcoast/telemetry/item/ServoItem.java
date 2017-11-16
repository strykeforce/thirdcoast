package org.strykeforce.thirdcoast.telemetry.item;

import edu.wpi.first.wpilibj.Servo;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;

/**
 * Represents a {@link Servo} telemetry-enable Item.
 */
public class ServoItem extends AbstractItem {

  public final static String TYPE = "servo";
  public final static Set<Measure> MEASURES = Collections.unmodifiableSet(EnumSet.of(
      Measure.POSITION,
      Measure.ANGLE
  ));

  private final Servo servo;

  public ServoItem(Servo servo, String description) {
    super(TYPE, description, MEASURES);
    this.servo = servo;
  }

  public ServoItem(Servo servo) {
    this(servo, "Servo " + servo.getChannel());
  }

  @Override
  public int deviceId() {
    return servo.getChannel();
  }

  @Override
  public DoubleSupplier measurementFor(Measure measure) {
    if (!MEASURES.contains(measure)) {
      throw new IllegalArgumentException("invalid measure: " + measure.name());
    }
    switch (measure) {
      case POSITION:
        return servo::getPosition;
      case ANGLE:
        return servo::getAngle;
      default:
        throw new AssertionError(measure);
    }
  }

  /**
   * Indicates if some other {@code ServoItem} has the same underlying {@code Servo} as this one.
   *
   * @param obj the reference object with which to compare.
   * @return true if this Servo has the same channel ID, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ServoItem)) {
      return false;
    }
    ServoItem item = (ServoItem) obj;
    return item.servo.getChannel() == servo.getChannel();
  }

  /**
   * Returns a hashcode value for this ServoItem.
   *
   * @return a hashcode value for this ServoItem.
   */
  @Override
  public int hashCode() {
    return servo.getChannel();
  }


  @Override
  public String toString() {
    return "ServoItem{" +
        "servo=" + servo +
        "} " + super.toString();
  }
}
