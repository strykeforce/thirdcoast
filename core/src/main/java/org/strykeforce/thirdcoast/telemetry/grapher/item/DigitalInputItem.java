package org.strykeforce.thirdcoast.telemetry.grapher.item;

import edu.wpi.first.wpilibj.DigitalInput;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;

public class DigitalInputItem implements Item {

  public final static String TYPE = "digitalInput";
  public final static Set<Measure> MEASURES = Collections.unmodifiableSet(EnumSet.of(
      Measure.VALUE
  ));

  private final DigitalInput digitalInput;
  private final String description;

  public DigitalInputItem(DigitalInput digitalInput, String description) {
    this.digitalInput = digitalInput;
    this.description = description;
  }

  public DigitalInputItem(DigitalInput digitalInput) {
    this(digitalInput, "Digital Input " + digitalInput.getChannel());
  }

  @Override
  public int id() {
    return digitalInput.getChannel();
  }

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public Set<Measure> measures() {
    return MEASURES;
  }

  @Override
  public DoubleSupplier measurementFor(Measure measure) {
    if (!MEASURES.contains(measure)) {
      throw new IllegalArgumentException("invalid measure: " + measure.name());
    }
    return () -> digitalInput.get() ? 1.0 : 0.0;
  }
}
