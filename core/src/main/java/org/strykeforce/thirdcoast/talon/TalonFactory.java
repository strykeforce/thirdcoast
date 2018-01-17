package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.MotorSafety;
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
    talon.clearStickyFaults(TIMEOUT_MS);
    talon.setSafetyEnabled(false);
    talon.setExpiration(MotorSafety.DEFAULT_SAFETY_EXPIRATION);
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
    Optional<ThirdCoastTalon> optTalon =
        seen.stream().filter(it -> it.getDeviceID() == id).findFirst();
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
