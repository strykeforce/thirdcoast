package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;

final class SoftLimit {

  public final static SoftLimit DEFAULT = new SoftLimit(null);
  private final boolean enabled;
  private final double value;

  SoftLimit(Double value) {
    this.enabled = value != null;
    this.value = enabled ? value : 0;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public double getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "SoftLimit{" +
        "enabled=" + enabled +
        ", value=" + value +
        '}';
  }
}
