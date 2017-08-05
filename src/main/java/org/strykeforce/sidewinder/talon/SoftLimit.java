package org.strykeforce.sidewinder.talon;

import java.util.Optional;

final class SoftLimit {

  private final boolean isEnabled;
  private final double value;

  SoftLimit(Optional<Double> value) {
    this.isEnabled = value.isPresent();
    this.value = value.orElse(0.0);
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public double getValue() {
    return value;
  }
  
  @Override
  public String toString() {
    return "SoftLimit{" +
        "isEnabled=" + isEnabled +
        ", value=" + value +
        '}';
  }
}
