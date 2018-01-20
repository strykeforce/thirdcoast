package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.google.auto.factory.AutoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TalonSRX that reproduces the 2017 {@code set(double)} interface, reduces CAN bus overhead by
 * coalescing {@link TalonSRX#set(ControlMode, double)} commands and implements logical equality.
 *
 * <p>TalonSRX superclass appears to use the default {@link Object#equals(Object)} so we provide
 * logical equality based on device ID.
 */
@AutoFactory(allowSubclasses = true)
public class ThirdCoastTalon extends WPI_TalonSRX {
  private static final Logger logger = LoggerFactory.getLogger(ThirdCoastTalon.class);

  private double setpoint = Double.NaN;
  private ControlMode controlMode = ControlMode.Disabled;

  public ThirdCoastTalon(int deviceNumber) {
    super(deviceNumber);
    logger.debug("initializing {}", getDescription());
  }

  public void changeControlMode(ControlMode controlMode) {
    if (controlMode == this.controlMode) {
      logger.debug("{}: control mode {} not changed", getDescription(), controlMode);
      return;
    }
    logger.info("{}: changed from {} to {}", getDescription(), this.controlMode, controlMode);
    setpoint = Double.NaN;
    this.controlMode = controlMode;
  }

  @Override
  public ControlMode getControlMode() {
    return controlMode;
  }

  @Override
  public void set(double setpoint) {
    if (setpoint == this.setpoint) {
      return;
    }
    this.setpoint = setpoint;
    set(controlMode, setpoint);
  }

  /**
   * Returns a hashcode value for this TalonSRX.
   *
   * @return a hashcode value for this TalonSRX.
   */
  @Override
  public int hashCode() {
    return getDeviceID();
  }

  /**
   * Indicates if some other TalonSRX has the same device ID as this one.
   *
   * @param obj the reference object with which to compare.
   * @return true if this TalonSRX has the same device ID, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ThirdCoastTalon)) {
      return false;
    }
    ThirdCoastTalon other = (ThirdCoastTalon) obj;
    return other.getDeviceID() == getDeviceID();
  }

  @Override
  public String toString() {
    return "ThirdCoastTalon{" + "id=" + getDeviceID() + "}";
  }
}
