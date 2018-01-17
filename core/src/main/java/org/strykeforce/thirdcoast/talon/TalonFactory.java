package org.strykeforce.thirdcoast.talon;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Instantiate {@link CANTalon} instances with defaults. */
@Singleton
@ParametersAreNonnullByDefault
public class TalonFactory {

  public static final int CONTROL_FRAME_MS = 10;
  static final Logger logger = LoggerFactory.getLogger(TalonFactory.class);

  @NotNull private static final Set<CANTalon> seen = new HashSet<>();

  @NotNull private final TalonProvisioner provisioner;
  @NotNull private final WrapperFactory wrapperFactory;

  @Inject
  public TalonFactory(TalonProvisioner provisioner, WrapperFactory wrapperFactory) {
    logger.debug("initializing TalonFactory: {}", provisioner);
    this.provisioner = provisioner;
    this.wrapperFactory = wrapperFactory;
  }

  public boolean hasSeen(int id) {
    return seen.stream().anyMatch(it -> it.getDeviceID() == id);
  }

  /**
   * Create a wrapped {@link CANTalon} with appropriate default values.
   *
   * @param id the device ID of the CANTalon to create
   * @return the wrapped CANTalon
   * @see Wrapper
   */
  @NotNull
  private CANTalon createTalon(int id) {
    CANTalon talon = wrapperFactory.createWrapper(id, CONTROL_FRAME_MS);
    StatusFrameRate.DEFAULT.configure(talon);
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
    if (!seen.add(talon)) {
      throw new IllegalStateException("creating an already-existing talon");
    }
    logger.info("added {} to seen Set", talon);
    return talon;
  }

  /**
   * Gets a wrapped {@link CANTalon} with appropriate default values.
   *
   * @param id the device ID of the CANTalon to create
   * @return the wrapped CANTalon
   * @see Wrapper
   */
  @NotNull
  public CANTalon getTalon(final int id) {
    Optional<CANTalon> optTalon = seen.stream().filter(it -> it.getDeviceID() == id).findFirst();
    if (optTalon.isPresent()) {
      logger.info("returning cached talon {}", id);
      return optTalon.get();
    }
    logger.info("talon not cached, creating talon {}", id);
    return createTalon(id);
  }

  /**
   * A convenience method to get a wrapped {@link CANTalon} with the specified {@link
   * TalonConfiguration}.
   *
   * @param id the device ID of the CANTalon to create
   * @param config the device ID of the CANTalon to create
   * @return the wrapped CANTalon
   * @see Wrapper
   * @see TalonProvisioner
   */
  @NotNull
  public CANTalon getTalonWithConfiguration(int id, String config) {
    CANTalon talon = getTalon(id);
    provisioner.configurationFor(config).configure(talon);
    return talon;
  }

  @NotNull
  public TalonProvisioner getProvisioner() {
    return provisioner;
  }

  /** Factory class for {@link Wrapper}, facilitates testing. */
  static class WrapperFactory {

    @Inject
    WrapperFactory() {
      logger.debug("initializing WrapperFactory");
    }

    @NotNull
    public Wrapper createWrapper(int id, int controlPeriodMs) {
      return new Wrapper(id, controlPeriodMs);
    }
  }

  /**
   * CANTalon that reduces CAN bus overhead by coalescing {@link CANTalon#set(double)} commands and
   * implements logical equality. By default the Talon flushes the Tx buffer on every set call. See
   * com.team254.lib.util.drivers.LazyCANTalon.
   *
   * <p>CANTalon superclass appears to use the default {@link Object#equals(Object)} so we provide
   * logical equality based on device ID.
   */
  static class Wrapper extends CANTalon {

    static final Logger logger = LoggerFactory.getLogger(Wrapper.class);
    private double setpoint = Double.NaN;
    @Nullable private TalonControlMode controlMode = null;

    public Wrapper(int deviceNumber) {
      super(deviceNumber);
      logger.debug("initializing Wrapper for {}", getDescription());
    }

    public Wrapper(int deviceNumber, int controlPeriodMs) {
      super(deviceNumber, controlPeriodMs);
      logger.debug(
          "initializing Wrapper for {} with control frame period {}",
          getDescription(),
          controlPeriodMs);
    }

    public Wrapper(int deviceNumber, int controlPeriodMs, int enablePeriodMs) {
      super(deviceNumber, controlPeriodMs, enablePeriodMs);
      logger.debug(
          "initializing Wrapper for {} with control frame period {} and enable period {}",
          getDescription(),
          controlPeriodMs,
          enablePeriodMs);
    }

    @Override
    public void changeControlMode(TalonControlMode controlMode) {
      super.changeControlMode(controlMode);
      if (controlMode != this.controlMode) {
        logger.info("{}: changed from {} to {}", getDescription(), this.controlMode, controlMode);
        setpoint = Double.NaN;
        this.controlMode = controlMode;
        return;
      }
      logger.debug("{}: control mode {} not changed", getDescription(), controlMode);
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

    @Override
    public String toString() {
      return "TalonFactory$Wrapper{" + "id=" + super.getDeviceID() + "}";
    }
  }
}
