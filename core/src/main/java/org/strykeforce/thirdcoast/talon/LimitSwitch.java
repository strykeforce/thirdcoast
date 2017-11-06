package org.strykeforce.thirdcoast.talon;

final class LimitSwitch {

  public final static LimitSwitch DEFAULT = new LimitSwitch(null);

  private final boolean enabled;
  private final boolean normallyOpen;

  LimitSwitch(String state) {
    if (state == null) {
      state = "disabled";
    }
    switch (state.toLowerCase()) {
      case "normallyopen":
        enabled = true;
        normallyOpen = true;
        break;
      case "normallyclosed":
        enabled = true;
        normallyOpen = false;
        break;
      case "disabled":
        enabled = false;
        normallyOpen = false;
        break;
      default:
        throw new IllegalStateException("limit switch configuration invalid: " + state);
    }
  }

  public String configString() {
    if (enabled) {
      if (normallyOpen) {
        return "NormallyOpen";
      }
      return "NormallyClosed";
    }
    return "Disabled";
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
