package org.strykeforce.thirdcoast.telemetry.item;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;

/**
 * Represents a {@link DigitalInput} telemetry-enable Item.
 */

public class DigitalOutputItem extends AbstractItem {

  public final static String TYPE = "digitalOutput";
  public final static Set<Measure> MEASURES = Collections.unmodifiableSet(EnumSet.of(
      Measure.VALUE
  ));

  private final DigitalOutput digitalOutput;

  public DigitalOutputItem(DigitalOutput digitalOutput, String description) {
    super(TYPE, description, MEASURES);
    this.digitalOutput = digitalOutput;
  }

  public DigitalOutputItem(DigitalOutput digitalOutput) {
    this(digitalOutput, "Digital Output " + digitalOutput.getChannel());
  }

  @Override
  public int id() {
    return digitalOutput.getChannel();
  }

  @Override
  public DoubleSupplier measurementFor(Measure measure) {
    if (!MEASURES.contains(measure)) {
      throw new IllegalArgumentException("invalid measure: " + measure.name());
    }
    return () -> digitalOutput.get() ? 1.0 : 0.0;
  }

  /**
   * Indicates if some other {@code DigitalOutputItem} has the same underlying {@code DigitalOutput}
   * as this one.
   *
   * @param obj the reference object with which to compare.
   * @return true if this DigitalOutput has the same channel ID, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof DigitalOutputItem)) {
      return false;
    }
    DigitalOutputItem item = (DigitalOutputItem) obj;
    return item.digitalOutput.getChannel() == digitalOutput.getChannel();
  }

  /**
   * Returns a hashcode value for this DigitalOutputItem.
   *
   * @return a hashcode value for this DigitalOutputItem.
   */
  @Override
  public int hashCode() {
    return digitalOutput.getChannel();
  }


  @Override
  public String toString() {
    return "DigitalInputItem{" +
        "digitalOutput=" + digitalOutput +
        "} " + super.toString();
  }
}
