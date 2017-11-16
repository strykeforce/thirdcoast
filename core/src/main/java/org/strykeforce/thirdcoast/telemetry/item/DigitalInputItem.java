package org.strykeforce.thirdcoast.telemetry.item;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalInput;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;

/**
 * Represents a {@link DigitalInput} telemetry-enable Item.
 */

public class DigitalInputItem extends AbstractItem {

  public final static String TYPE = "digitalInput";
  public final static Set<Measure> MEASURES = Collections.unmodifiableSet(EnumSet.of(
      Measure.VALUE
  ));

  private final DigitalInput digitalInput;

  public DigitalInputItem(DigitalInput digitalInput, String description) {
    super(TYPE, description, MEASURES);
    this.digitalInput = digitalInput;
  }

  public DigitalInputItem(DigitalInput digitalInput) {
    this(digitalInput, "Digital Input " + digitalInput.getChannel());
  }

  @Override
  public int deviceId() {
    return digitalInput.getChannel();
  }

  @Override
  public DoubleSupplier measurementFor(Measure measure) {
    if (!MEASURES.contains(measure)) {
      throw new IllegalArgumentException("invalid measure: " + measure.name());
    }
    return () -> digitalInput.get() ? 1.0 : 0.0;
  }

  /**
   * Indicates if some other {@code DigitalInputItem} has the same underlying {@code DigitalInput}
   * as this one.
   *
   * @param obj the reference object with which to compare.
   * @return true if this DigitalInput has the same channel ID, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof DigitalInputItem)) {
      return false;
    }
    DigitalInputItem item = (DigitalInputItem) obj;
    return item.digitalInput.getChannel() == digitalInput.getChannel();
  }

  /**
   * Returns a hashcode value for this DigitalInputItem.
   *
   * @return a hashcode value for this DigitalInputItem.
   */
  @Override
  public int hashCode() {
    return digitalInput.getChannel();
  }


  @Override
  public String toString() {
    return "DigitalInputItem{" +
        "digitalInput=" + digitalInput +
        "} " + super.toString();
  }
}
