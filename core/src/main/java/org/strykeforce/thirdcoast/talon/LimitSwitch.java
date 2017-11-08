package org.strykeforce.thirdcoast.talon;

final class LimitSwitch {

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
