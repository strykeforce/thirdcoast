package org.strykeforce.thirdcoast.talon;

import javax.annotation.ParametersAreNonnullByDefault;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
public final class LimitSwitch {

  @NotNull public static final LimitSwitch DEFAULT = new LimitSwitch();

  private final boolean enabled;
  private final boolean normallyOpen;

  public LimitSwitch(boolean enabled, boolean normallyOpen) {
    this.enabled = enabled;
    this.normallyOpen = normallyOpen;
  }

  public LimitSwitch() {
    this(false, false);
  }

  @NotNull
  public LimitSwitch copyWithEnabled(boolean enabled) {
    return new LimitSwitch(enabled, normallyOpen);
  }

  @NotNull
  public LimitSwitch copyWithNormallyOpen(boolean normallyOpen) {
    return new LimitSwitch(enabled, normallyOpen);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isNormallyOpen() {
    return normallyOpen;
  }

  @NotNull
  @Override
  public String toString() {
    return "LimitSwitch{" + "enabled=" + enabled + ", normallyOpen=" + normallyOpen + '}';
  }
}
