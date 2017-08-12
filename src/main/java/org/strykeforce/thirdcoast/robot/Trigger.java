package org.strykeforce.thirdcoast.robot;

public abstract class Trigger {

  private boolean isActiveLast = false;

  /**
   * Subclasses implement this to signal when a trigger is active. It usually
   * is polled by a control loop.
   * @return true if the trigger is active
   */
  public abstract boolean get();

  public boolean hasActivated() {
    if (get()) {
      if (!isActiveLast) {
        isActiveLast = true;
        return true;
      }
    } else {
      isActiveLast = false;
    }
    return false;
  }

}
