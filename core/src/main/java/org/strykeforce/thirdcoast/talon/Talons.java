package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.util.Settings;

/** Instantiate {@link TalonSRX} instances with defaults. */
@Singleton
@ParametersAreNonnullByDefault
public class Talons {

  private static final Logger logger = LoggerFactory.getLogger(Talons.class);
  private static final String TABLE = "THIRDCOAST.TALONS";
  private static final String TALONS = "TALON";
  private final Map<Integer, TalonSRX> talons = new HashMap<>();

  @Inject
  public Talons(Settings settings, Factory factory) {
    Toml settingsTable = settings.getTable(TABLE);
    final int timeout = settingsTable.getLong("timeout").intValue();

    List<TalonConfiguration> talonConfigurations = new ArrayList<>();
    for (Toml toml : settings.getTables(TALONS)) {
      TalonConfiguration config = TalonConfiguration.create(toml);
      logger.debug("added config '{}' for talons {}", config.getName(), config.getTalonIds());
      talonConfigurations.add(config);
    }

    for (TalonConfiguration configuration : talonConfigurations) {
      for (Integer id : configuration.getTalonIds()) {
        if (talons.containsKey(id)) {
          logger.error("Talon {} already configured, skipping", id);
          continue;
        }
        TalonSRX talon = factory.create(id);
        configuration.configure(talon, timeout);
        talons.put(id, talon);
      }
    }
    logger.debug("timeout = {}", timeout);
  }

  @SuppressWarnings("unused")
  public static void dump(TalonSRX talon) {
    logger.debug("Active Trajectory Heading = {}", talon.getActiveTrajectoryHeading());
    logger.debug("Active Trajectory Position = {}", talon.getActiveTrajectoryPosition());
    logger.debug("Active Trajectory Velocity = {}", talon.getActiveTrajectoryVelocity());
    logger.debug("Base ID = {}", talon.getBaseID());
    logger.debug("Bus Voltage = {}", talon.getBusVoltage());
    logger.debug("Closed Loop Error 0 = {}", talon.getClosedLoopError(0));
    logger.debug("Closed Loop Error 1 = {}", talon.getClosedLoopError(1));
    logger.debug("Closed Loop Target 0 = {}", talon.getClosedLoopTarget(0));
    logger.debug("Closed Loop Target 1 = {}", talon.getClosedLoopTarget(1));
    logger.debug("Control Mode = {}", talon.getControlMode());
    logger.debug("Device ID = {}", talon.getDeviceID());
    logger.debug("Error Derivative 0 = {}", talon.getErrorDerivative(0));
    logger.debug("Error Derivative 1 = {}", talon.getErrorDerivative(1));

    Faults faults = new Faults();
    talon.getFaults(faults);
    logger.debug("Faults = {}", faults);

    logger.debug("Firmware Version = {}", talon.getFirmwareVersion());
    logger.debug("Integral Accumulator 0 = {}", talon.getIntegralAccumulator(0));
    logger.debug("Integral Accumulator 1 = {}", talon.getIntegralAccumulator(1));
    logger.debug("Inverted = {}", talon.getInverted());
    logger.debug("Last Error = {}", talon.getLastError());
    // TODO: MotionProfileStatus
    logger.debug("Motor Output Percent = {}", talon.getMotorOutputPercent());
    logger.debug("Motor Output Voltage = {}", talon.getMotorOutputVoltage());
    logger.debug("Output Current = {}", talon.getOutputCurrent());
    logger.debug("Selected Sensor Position 0 = {}", talon.getSelectedSensorPosition(0));
    logger.debug("Selected Sensor Position 1 = {}", talon.getSelectedSensorPosition(1));
    logger.debug("Selected Sensor Velocity 0 = {}", talon.getSelectedSensorVelocity(0));
    logger.debug("Selected Sensor Velocity 1 = {}", talon.getSelectedSensorVelocity(1));

    SensorCollection sensors = talon.getSensorCollection();
    logger.debug("Analog In = {}", sensors.getAnalogIn());
    logger.debug("Analog In Raw = {}", sensors.getAnalogInRaw());
    logger.debug("Analog In Velocity = {}", sensors.getAnalogInVel());
    logger.debug("Pin State Quad A = {}", sensors.getPinStateQuadA());
    logger.debug("Pin State Quad B = {}", sensors.getPinStateQuadB());
    logger.debug("Pin State Quad Index = {}", sensors.getPinStateQuadIdx());
    logger.debug("Pulse Width Position = {}", sensors.getPulseWidthPosition());
    logger.debug("Pulse Width Rise to Fall µsec = {}", sensors.getPulseWidthRiseToFallUs());
    logger.debug("Pulse Width Rise to Rise µsec = {}", sensors.getPulseWidthRiseToRiseUs());
    logger.debug("Pulse Width Position = {}", sensors.getPulseWidthPosition());
    logger.debug("Pulse Width Velocity = {}", sensors.getPulseWidthVelocity());
    logger.debug("Quadrature Position = {}", sensors.getQuadraturePosition());
    logger.debug("Quadrature Velocity = {}", sensors.getQuadratureVelocity());
    logger.debug("Forward Limit Switch Closed = {}", sensors.isFwdLimitSwitchClosed());
    logger.debug("Reverse Limit Switch Closed = {}", sensors.isRevLimitSwitchClosed());
    logger.debug("Temperature = {}", talon.getTemperature());
    logger.debug("Reset Has Occurred = {}", talon.hasResetOccurred());

    int timeout = 10;
    for (StatusFrameEnhanced sfe : StatusFrameEnhanced.values()) {
      logger.debug("Status Frame Period {} = {}", sfe, talon.getStatusFramePeriod(sfe, timeout));
    }
  }

  /**
   * Gets a {@link TalonSRX} with appropriate default values.
   *
   * @param id the device ID of the TalonSRX to create
   * @return the TalonSRX
   */
  @NotNull
  public TalonSRX getTalon(int id) {
    if (!talons.containsKey(id)) {
      throw new NoSuchElementException("Talon " + id);
    }
    return talons.get(id);
  }

  static class Factory {
    @Inject
    Factory() {}

    TalonSRX create(int id) {
      logger.debug("creating TalonSRX with id = {}", id);
      return new TalonSRX(id);
    }
  }
}
