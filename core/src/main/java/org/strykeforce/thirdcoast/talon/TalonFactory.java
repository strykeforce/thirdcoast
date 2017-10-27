package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instantiate {@link CANTalon} instances with defaults.
 */
@Singleton
public class TalonFactory {
  // TODO: look at caching instance singletons, talons seem to behave badly if multiple copies made

  public final static int CONTROL_FRAME_MS = 10;
  final static Logger logger = LoggerFactory.getLogger(TalonFactory.class);

  private final TalonProvisioner provisioner;
  private final WrapperFactory wrapperFactory;

  @Inject
  public TalonFactory(TalonProvisioner provisioner, WrapperFactory wrapperFactory) {
    logger.debug("initializing TalonFactory: {}, {}", provisioner, wrapperFactory);
    this.provisioner = provisioner;
    this.wrapperFactory = wrapperFactory;
  }

  /**
   * Create a wrapped {@link CANTalon} with appropriate default values.
   *
   * @param id the device ID of the CANTalon to create
   * @return the wrapped CANTalon
   * @see Wrapper
   */
  public CANTalon createTalon(int id) {
    CANTalon talon = wrapperFactory.createWrapper(id, CONTROL_FRAME_MS);
    talon.changeControlMode(TalonControlMode.Voltage);
    talon.clearIAccum();
    talon.ClearIaccum();
    talon.clearMotionProfileHasUnderrun();
    talon.clearMotionProfileTrajectories();
    talon.clearStickyFaults();
    talon.enableZeroSensorPositionOnForwardLimit(false);
    talon.enableZeroSensorPositionOnIndex(false, false);
    talon.enableZeroSensorPositionOnReverseLimit(false);
    talon.reverseOutput(false);
    talon.reverseSensor(false);
    talon.setAnalogPosition(0);
    talon.setPosition(0);
    talon.setProfile(0);
    talon.setPulseWidthPosition(0);
    return talon;
  }

  /**
   * A convenience method to create a wrapped {@link CANTalon} with the specified {@link
   * TalonConfiguration}.
   *
   * @param id the device ID of the CANTalon to create
   * @param config the device ID of the CANTalon to create
   * @return the wrapped CANTalon
   * @see Wrapper
   * @see TalonProvisioner
   */
  public CANTalon createTalonWithConfiguration(int id, String config) {
    CANTalon talon = createTalon(id);
    provisioner.configurationFor(config).configure(talon);
    return talon;
  }

  /**
   * Factory class for {@link Wrapper}, facilitates testing.
   */
  static class WrapperFactory {

    @Inject
    WrapperFactory() {
      logger.debug("initializing WrapperFactory");
    }

    public Wrapper createWrapper(int id, int controlPeriodMs) {
      return new Wrapper(id, CONTROL_FRAME_MS);
    }
  }

  /**
   * CANTalon that reduces CAN bus overhead by coalescing {@link CANTalon#set(double)} commands and
   * implements logical equality. By default the Talon flushes the Tx buffer on every set call. See
   * com.team254.lib.util.drivers.LazyCANTalon.
   *
   * CANTalon superclass appears to use the default {@link Object#equals(Object)} so we provide
   * logical equality based on device ID.
   */
  static class Wrapper extends CANTalon {

    private double setpoint = Double.NaN;
    private TalonControlMode controlMode = null;

    public Wrapper(int deviceNumber) {
      super(deviceNumber);
      logger.debug("initializing Wrapper for {}", getDescription());
    }

    public Wrapper(int deviceNumber, int controlPeriodMs) {
      super(deviceNumber, controlPeriodMs);
      logger.debug("initializing Wrapper for {} with control frame period {}", getDescription(),
          controlPeriodMs);
    }

    public Wrapper(int deviceNumber, int controlPeriodMs, int enablePeriodMs) {
      super(deviceNumber, controlPeriodMs, enablePeriodMs);
      logger.debug("initializing Wrapper for {} with control frame period {} and enable period {}",
          getDescription(), controlPeriodMs, enablePeriodMs);
    }

    @Override
    public void changeControlMode(TalonControlMode controlMode) {
      super.changeControlMode(controlMode);
      if (controlMode != this.controlMode) {
        setpoint = Double.NaN;
        this.controlMode = controlMode;
        logger.info("changing {} from {} to {}", getDescription(), this.controlMode, controlMode);
      }
    }

    @Override
    public void setControlMode(int mode) {
      throw new AssertionError("use changeControlMode");
    }

    @Override
    public void set(double setpoint) {
      if (setpoint != this.setpoint) {
        this.setpoint = setpoint;
        super.set(setpoint);
      }
    }

    /**
     * Returns a hashcode value for this CANTalon.
     *
     * @return a hashcode value for this CANTalon.
     */
    @Override
    public int hashCode() {
      return getDeviceID();
    }

    /**
     * Indicates if some other CANTalon has the same device ID as this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this CANTalon has the same device ID, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof Wrapper)) {
        return false;
      }
      Wrapper wt = (Wrapper) obj;
      return wt.getDeviceID() == getDeviceID();
    }
  }


}
