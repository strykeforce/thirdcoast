package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Instantiate {@link TalonSRX} instances with defaults. */
@Singleton
@ParametersAreNonnullByDefault
public class TalonFactory {

  public static final int CONTROL_FRAME_MS = 10;
  private static final int TIMEOUT_MS = 10;
  private static final Logger logger = LoggerFactory.getLogger(TalonFactory.class);

  @NotNull private static final Set<TalonSRX> seen = new HashSet<>();

  @NotNull private final TalonProvisioner provisioner;
  //  @NotNull private final WrapperFactory wrapperFactory; // FIXME

  @Inject
  public TalonFactory(TalonProvisioner provisioner) {
    logger.debug("initializing TalonFactory: {}", provisioner);
    this.provisioner = provisioner;
    //    this.wrapperFactory = wrapperFactory;
  }

  public boolean hasSeen(int id) {
    return seen.stream().anyMatch(it -> it.getDeviceID() == id);
  }

  /**
   * Create a wrapped {@link TalonSRX} with appropriate default values.
   *
   * @param id the device ID of the TalonSRX to create
   * @return the wrapped TalonSRX
   */
  @NotNull
  private TalonSRX createTalon(int id) {
    TalonSRX talon = new TalonSRX(id);
    StatusFrameRate.DEFAULT.configure(talon);
    //    talon.changeControlMode(TalonControlMode.Voltage);
    talon.setIntegralAccumulator(0, 0, TIMEOUT_MS);
    talon.setIntegralAccumulator(0, 1, TIMEOUT_MS);
    talon.clearMotionProfileHasUnderrun(TIMEOUT_MS);
    talon.clearMotionProfileTrajectories();
    talon.clearStickyFaults(TIMEOUT_MS);
    //    talon.enableZeroSensorPositionOnForwardLimit(false);
    //    talon.enableZeroSensorPositionOnIndex(false, false);
    //    talon.enableZeroSensorPositionOnReverseLimit(false);
    talon.setInverted(false);
    SensorCollection sensorCollection = talon.getSensorCollection();
    sensorCollection.setAnalogPosition(0, TIMEOUT_MS);
    sensorCollection.setPulseWidthPosition(0, TIMEOUT_MS);
    sensorCollection.setQuadraturePosition(0, TIMEOUT_MS);
    talon.selectProfileSlot(0, 0);
    if (!seen.add(talon)) {
      throw new IllegalStateException("creating an already-existing talon");
    }
    logger.info("added {} to seen Set", talon);
    return talon;
  }

  /**
   * Gets a wrapped {@link TalonSRX} with appropriate default values.
   *
   * @param id the device ID of the TalonSRX to create
   * @return the TalonSRX
   */
  @NotNull
  public TalonSRX getTalon(final int id) {
    Optional<TalonSRX> optTalon = seen.stream().filter(it -> it.getDeviceID() == id).findFirst();
    if (optTalon.isPresent()) {
      logger.info("returning cached talon {}", id);
      return optTalon.get();
    }
    logger.info("talon not cached, creating talon {}", id);
    return createTalon(id);
  }

  /**
   * A convenience method to get a wrapped {@link TalonSRX} with the specified {@link
   * TalonConfiguration}.
   *
   * @param id the device ID of the TalonSRX to create
   * @param config the device ID of the TalonSRX to create
   * @return the wrapped TalonSRX
   * @see TalonProvisioner
   */
  @NotNull
  public TalonSRX getTalonWithConfiguration(int id, String config) {
    TalonSRX talon = getTalon(id);
    provisioner.configurationFor(config).configure(talon);
    return talon;
  }

  @NotNull
  public TalonProvisioner getProvisioner() {
    return provisioner;
  }

  //  /** Factory class for {@link Wrapper}, facilitates testing. */
  //  static class WrapperFactory {
  //
  //    @Inject
  //    WrapperFactory() {
  //      logger.debug("initializing WrapperFactory");
  //    }
  //
  //    @NotNull
  //    public Wrapper createWrapper(int id, int controlPeriodMs) {
  //      return new Wrapper(id, controlPeriodMs);
  //    }
  //  }
  //
  //  /**
  //   * TalonSRX that reduces CAN bus overhead by coalescing {@link TalonSRX#set(double)} commands
  // and
  //   * implements logical equality. By default the Talon flushes the Tx buffer on every set call.
  // See
  //   * com.team254.lib.util.drivers.LazyTalonSRX.
  //   *
  //   * <p>TalonSRX superclass appears to use the default {@link Object#equals(Object)} so we
  // provide
  //   * logical equality based on device ID.
  //   */
  //  static class Wrapper extends TalonSRX {
  //
  //    static final Logger logger = LoggerFactory.getLogger(Wrapper.class);
  //    private double setpoint = Double.NaN;
  //    @Nullable private TalonControlMode controlMode = null;
  //
  //    public Wrapper(int deviceNumber) {
  //      super(deviceNumber);
  //      logger.debug("initializing Wrapper for {}", getDescription());
  //    }
  //
  //    public Wrapper(int deviceNumber, int controlPeriodMs) {
  //      super(deviceNumber, controlPeriodMs);
  //      logger.debug(
  //          "initializing Wrapper for {} with control frame period {}",
  //          getDescription(),
  //          controlPeriodMs);
  //    }
  //
  //    public Wrapper(int deviceNumber, int controlPeriodMs, int enablePeriodMs) {
  //      super(deviceNumber, controlPeriodMs, enablePeriodMs);
  //      logger.debug(
  //          "initializing Wrapper for {} with control frame period {} and enable period {}",
  //          getDescription(),
  //          controlPeriodMs,
  //          enablePeriodMs);
  //    }
  //
  //    @Override
  //    public void changeControlMode(TalonControlMode controlMode) {
  //      super.changeControlMode(controlMode);
  //      if (controlMode != this.controlMode) {
  //        logger.info("{}: changed from {} to {}", getDescription(), this.controlMode,
  // controlMode);
  //        setpoint = Double.NaN;
  //        this.controlMode = controlMode;
  //        return;
  //      }
  //      logger.debug("{}: control mode {} not changed", getDescription(), controlMode);
  //    }
  //
  //    @Override
  //    public void setControlMode(int mode) {
  //      throw new AssertionError("use changeControlMode");
  //    }
  //
  //    @Override
  //    public void set(double setpoint) {
  //      if (setpoint != this.setpoint) {
  //        this.setpoint = setpoint;
  //        super.set(setpoint);
  //      }
  //    }
  //
  //    /**
  //     * Returns a hashcode value for this TalonSRX.
  //     *
  //     * @return a hashcode value for this TalonSRX.
  //     */
  //    @Override
  //    public int hashCode() {
  //      return getDeviceID();
  //    }
  //
  //    /**
  //     * Indicates if some other TalonSRX has the same device ID as this one.
  //     *
  //     * @param obj the reference object with which to compare.
  //     * @return true if this TalonSRX has the same device ID, false otherwise.
  //     */
  //    @Override
  //    public boolean equals(Object obj) {
  //      if (obj == this) {
  //        return true;
  //      }
  //      if (!(obj instanceof Wrapper)) {
  //        return false;
  //      }
  //      Wrapper wt = (Wrapper) obj;
  //      return wt.getDeviceID() == getDeviceID();
  //    }
  //
  //    @Override
  //    public String toString() {
  //      return "TalonFactory$Wrapper{" + "id=" + super.getDeviceID() + "}";
  //    }
  //  }
}
