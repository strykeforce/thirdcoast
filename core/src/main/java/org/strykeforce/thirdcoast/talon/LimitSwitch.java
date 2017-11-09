package org.strykeforce.thirdcoast.talon;

import javax.annotation.ParametersAreNonnullByDefault;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
public final class LimitSwitch {

  @NotNull
  public final static LimitSwitch DEFAULT = new LimitSwitch();

  private final boolean enabled;
  private final boolean normallyOpen;

  public LimitSwitch(boolean enabled, boolean normallyOpen) {
    this.enabled = enabled;
    this.normallyOpen = normallyOpen;
  }

  public LimitSwitch() {
    this(false,false);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isNormallyOpen() {
    return normallyOpen;
  }

  @Override
  public String toString() {
    return "LimitSwitch{" +
        "enabled=" + enabled +
        ", normallyOpen=" + normallyOpen +
        '}';
  }

}
