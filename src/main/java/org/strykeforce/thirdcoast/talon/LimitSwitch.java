package org.strykeforce.thirdcoast.talon;

import java.util.Optional;

final class LimitSwitch {

  private final boolean isEnabled;
  private final boolean isNormallyOpen;

  LimitSwitch(Optional<String> state) {
    String stateString = state.orElse("disabled");
    switch (stateString.toLowerCase()) {
      case "normallyopen":
        isEnabled = true;
        isNormallyOpen = true;
        break;
      case "normallyclosed":
        isEnabled = true;
        isNormallyOpen = false;
        break;
      case "disabled":
        isEnabled = false;
        isNormallyOpen = false;
        break;
      default:
        throw new IllegalStateException("limit switch configuration invalid: " + state);
    }
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public boolean isNormallyOpen() {
    return isNormallyOpen;
  }

  @Override
  public String toString() {
    return "LimitSwitch{" +
        "isEnabled=" + isEnabled +
        ", isNormallyOpen=" + isNormallyOpen +
        '}';
  }

}
