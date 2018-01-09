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
  static final int TIMEOUT_MS = 10;
  private static final Logger logger = LoggerFactory.getLogger(TalonFactory.class);

  @NotNull private static final Set<ThirdCoastTalon> seen = new HashSet<>();

  @NotNull private final TalonProvisioner provisioner;
  @NotNull private final ThirdCoastTalonFactory wrapperFactory;

  @Inject
  public TalonFactory(TalonProvisioner provisioner, ThirdCoastTalonFactory wrapperFactory) {
    logger.debug("initializing TalonFactory: {}", provisioner);
    this.provisioner = provisioner;
    this.wrapperFactory = wrapperFactory;
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
    ThirdCoastTalon talon = wrapperFactory.create(id);
    StatusFrameRate.DEFAULT.configure(talon);
    talon.changeControlMode(TalonControlMode.Voltage);
    talon.enableVoltageCompensation(true);
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
    Optional<ThirdCoastTalon> optTalon = seen.stream().filter(it -> it.getDeviceID() == id).findFirst();
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
}
