package org.strykeforce.thirdcoast.talon;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
public final class SoftLimit {

  @NotNull public static final SoftLimit DEFAULT = new SoftLimit();
  private final boolean enabled;
  private final int position;

  private SoftLimit(boolean enabled, int position) {
    this.enabled = enabled;
    this.position = position;
  }

  SoftLimit(@Nullable Integer position) {
    this(position != null, position != null ? position : 0);
  }

  private SoftLimit() {
    this(false, 0);
  }

  public SoftLimit copyWithEnabled(boolean enabled) {
    return new SoftLimit(enabled, position);
  }

  public SoftLimit copyWithPosition(int position) {
    return new SoftLimit(enabled, position);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public int getPosition() {
    return position;
  }

  @Override
  @NotNull
  public String toString() {
    return "SoftLimit{" + "enabled=" + enabled + ", position=" + position + '}';
  }
}
