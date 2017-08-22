package org.strykeforce.thirdcoast.robot;

/**
 * Detects triggering events for polled inputs. For example, detecting a button "down" event.
 */
public abstract class Trigger {

  private boolean isActiveLast = false;

  /**
   * Subclasses implement this to signal when a input is active. It usually is polled by a control
   * loop.
   *
   * @return true if the trigger is active
   */
  public abstract boolean get();

  /**
   * This will return true upon the first call after the input is active. It resets when the input
   * is deactivated.
   *
   * @return true when input is first activated
   */
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
