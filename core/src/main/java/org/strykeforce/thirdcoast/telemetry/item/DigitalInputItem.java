package org.strykeforce.thirdcoast.telemetry.item;

import edu.wpi.first.wpilibj.DigitalInput;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;

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
  public int id() {
    return digitalInput.getChannel();
  }

  @Override
  public DoubleSupplier measurementFor(Measure measure) {
    if (!MEASURES.contains(measure)) {
      throw new IllegalArgumentException("invalid measure: " + measure.name());
    }
    return () -> digitalInput.get() ? 1.0 : 0.0;
  }

  @Override
  public String toString() {
    return "DigitalInputItem{" +
        "digitalInput=" + digitalInput +
        "} " + super.toString();
  }
}
