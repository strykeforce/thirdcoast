package org.strykeforce.thirdcoast.talon;

final class SoftLimit {

  public final static SoftLimit DEFAULT = new SoftLimit(null);
  private final boolean enabled;
  private final double position;

  SoftLimit(Double position) {
    this.enabled = position != null;
    this.position = enabled ? position : 0;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public double getPosition() {
    return position;
  }

  @Override
  public String toString() {
    return "SoftLimit{" +
        "enabled=" + enabled +
        ", position=" + position +
        '}';
  }
}
