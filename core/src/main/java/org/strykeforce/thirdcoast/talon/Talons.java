package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Singleton;
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
    logger.info("loading settings from '{}'", TABLE);
    Toml settingsTable = settings.getTable(TABLE);

    final int timeout = settingsTable.getLong("timeout").intValue();
    logger.debug("TalonSRX configuration timeout = {}", timeout);

    List<TalonConfiguration> talonConfigurations = new ArrayList<>();
    List<Toml> talonConfigTables = settings.getTables(TALONS);
    if (talonConfigTables.size() == 0) logger.warn("no TalonSRX configurations available");
    else
      logger.info(
          "loading {} TalonSRX configurations from '{}' table array",
          talonConfigTables.size(),
          TALONS);

    for (Toml toml : talonConfigTables) {
      TalonConfiguration config = TalonConfiguration.create(toml);
      talonConfigurations.add(config);
      logger.debug("added '{}' for TalonSRX ids: {}", config.getName(), config.getTalonIds());
    }

    for (TalonConfiguration config : talonConfigurations) {
      for (Integer id : config.getTalonIds()) {
        if (talons.containsKey(id)) {
          logger.error("TalonSRX {} already configured, ignoring '{}'", id, config.getName());
          continue;
        }
        TalonSRX talon = factory.create(id);
        config.configure(talon, timeout);
        talons.put(id, talon);
      }
    }
  }

  @SuppressWarnings("unused")
  public static void dump(TalonSRX talon) {
    logger.debug("Talon ID = {}", talon.getDeviceID());
    int timeout = 10;
    for (int i = 0; i < 4; i++) {
      logger.debug(
          "Slot {} P = {}", i, talon.configGetParameter(ParamEnum.eProfileParamSlot_P, i, timeout));
      logger.debug(
          "Slot {} I = {}", i, talon.configGetParameter(ParamEnum.eProfileParamSlot_I, i, timeout));
      logger.debug(
          "Slot {} D = {}", i, talon.configGetParameter(ParamEnum.eProfileParamSlot_D, i, timeout));
      logger.debug(
          "Slot {} F = {}", i, talon.configGetParameter(ParamEnum.eProfileParamSlot_F, i, timeout));
      logger.debug(
          "Slot {} Izone = {}",
          i,
          talon.configGetParameter(ParamEnum.eProfileParamSlot_IZone, i, timeout));
      logger.debug(
          "Slot {} Max I Accum = {}",
          i,
          talon.configGetParameter(ParamEnum.eProfileParamSlot_MaxIAccum, i, timeout));
      logger.debug(
          "Slot {} Allowable Error = {}",
          i,
          talon.configGetParameter(ParamEnum.eProfileParamSlot_AllowableErr, i, timeout));
    }
    logger.debug(
        "Motion Magic Acceleration = {}",
        talon.configGetParameter(ParamEnum.eMotMag_Accel, 0, timeout));
    logger.debug(
        "Motion Magic Cruise Velocity = {}",
        talon.configGetParameter(ParamEnum.eMotMag_VelCruise, 0, timeout));
    logger.debug(
        "Feedback Sensor Type = {}",
        talon.configGetParameter(ParamEnum.eFeedbackSensorType, 0, timeout));

    logger.debug("Closed Loop Error 0 = {}", talon.getClosedLoopError(0));
    logger.debug("Closed Loop Error 1 = {}", talon.getClosedLoopError(1));
    logger.debug("Closed Loop Target 0 = {}", talon.getClosedLoopTarget(0));
    logger.debug("Closed Loop Target 1 = {}", talon.getClosedLoopTarget(1));
    logger.debug("Control Mode = {}", talon.getControlMode());

    //    logger.debug("Error Derivative 0 = {}", talon.getErrorDerivative(0));
    //    logger.debug("Error Derivative 1 = {}", talon.getErrorDerivative(1));
    //    logger.debug("Active Trajectory Heading = {}", talon.getActiveTrajectoryHeading());
    //    logger.debug("Active Trajectory Position = {}", talon.getActiveTrajectoryPosition());
    //    logger.debug("Active Trajectory Velocity = {}", talon.getActiveTrajectoryVelocity());
    //    logger.debug("Base ID = {}", talon.getBaseID());
    //    logger.debug("Bus Voltage = {}", talon.getBusVoltage());

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
    logger.debug("Pulse Width Rise to Fall usec = {}", sensors.getPulseWidthRiseToFallUs());
    logger.debug("Pulse Width Rise to Rise usec = {}", sensors.getPulseWidthRiseToRiseUs());
    logger.debug("Pulse Width Position = {}", sensors.getPulseWidthPosition());
    logger.debug("Pulse Width Velocity = {}", sensors.getPulseWidthVelocity());
    logger.debug("Quadrature Position = {}", sensors.getQuadraturePosition());
    logger.debug("Quadrature Velocity = {}", sensors.getQuadratureVelocity());
    logger.debug("Forward Limit Switch Closed = {}", sensors.isFwdLimitSwitchClosed());
    logger.debug("Reverse Limit Switch Closed = {}", sensors.isRevLimitSwitchClosed());
    logger.debug("Temperature = {}", talon.getTemperature());
    logger.debug("Reset Has Occurred = {}", talon.hasResetOccurred());

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
  public TalonSRX getTalon(int id) {
    if (!talons.containsKey(id)) {
      logger.error("TalonSRX {} not found", id);
    }
    return talons.get(id);
  }

  /**
   * Convenience method to get ordered list of {@link TalonSRX}.
   *
   * @param ids List of TalonSRX ids
   * @return List of TalonSRX objects
   */
  public List<TalonSRX> getTalons(List<? extends Integer> ids) {
    return ids.stream().map(this::getTalon).collect(Collectors.toList());
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
