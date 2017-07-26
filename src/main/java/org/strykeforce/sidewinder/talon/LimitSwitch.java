package org.strykeforce.sidewinder.talon;

import com.ctre.CANTalon;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LimitSwitch that = (LimitSwitch) o;

    if (isEnabled != that.isEnabled) {
      return false;
    }
    return isNormallyOpen == that.isNormallyOpen;
  }

  @Override
  public int hashCode() {
    int result = (isEnabled ? 1 : 0);
    result = 31 * result + (isNormallyOpen ? 1 : 0);
    return result;
  }
}
