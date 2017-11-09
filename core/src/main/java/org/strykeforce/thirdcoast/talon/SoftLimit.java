package org.strykeforce.thirdcoast.talon;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
public final class SoftLimit {

  @NotNull
  public final static SoftLimit DEFAULT = new SoftLimit(null);
  private final boolean enabled;
  private final double position;

  SoftLimit(@Nullable Double position) {
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
  @NotNull
  public String toString() {
    return "SoftLimit{" +
        "enabled=" + enabled +
        ", position=" + position +
        '}';
  }
}
