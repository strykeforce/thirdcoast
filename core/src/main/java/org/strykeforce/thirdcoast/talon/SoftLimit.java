package org.strykeforce.thirdcoast.talon;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
public final class SoftLimit {

  @NotNull
  public final static SoftLimit DEFAULT = new SoftLimit();
  private final boolean enabled;
  private final double position;

  SoftLimit(boolean enabled, double position) {
    this.enabled = enabled;
    this.position = position;
  }

  SoftLimit(@Nullable Double position) {
    this(position != null, position != null ? position : 0);
  }

  SoftLimit() {
    this(false, 0);
  }

  public SoftLimit copyWithEnabled(boolean enabled) {
    return new SoftLimit(enabled, position);
  }

  public SoftLimit copyWithPosition(double position) {
    return new SoftLimit(enabled, position);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public double getPosition() {
    return position;
  }

  @Override
  @NotNull
  public String toString() {
    return "SoftLimit{" +
        "enabled=" + enabled +
        ", position=" + position +
        '}';
  }
}
