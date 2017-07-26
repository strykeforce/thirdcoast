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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SoftLimit softLimit = (SoftLimit) o;

    if (isEnabled != softLimit.isEnabled) {
      return false;
    }
    return Double.compare(softLimit.value, value) == 0;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = (isEnabled ? 1 : 0);
    temp = Double.doubleToLongBits(value);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "SoftLimit{" +
        "isEnabled=" + isEnabled +
        ", value=" + value +
        '}';
  }
}
