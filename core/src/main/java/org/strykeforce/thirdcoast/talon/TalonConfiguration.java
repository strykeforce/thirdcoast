package org.strykeforce.thirdcoast.talon;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.config.CurrentLimits;
import org.strykeforce.thirdcoast.talon.config.FeedbackSensor;
import org.strykeforce.thirdcoast.talon.config.LimitSwitches;
import org.strykeforce.thirdcoast.talon.config.MotionMagic;
import org.strykeforce.thirdcoast.talon.config.Output;
import org.strykeforce.thirdcoast.talon.config.SoftLimits;
import org.strykeforce.thirdcoast.talon.config.VelocityMeasurement;

/**
 * Represents a Talon configuration.
 *
 * @see com.ctre.phoenix.motorcontrol.can.TalonSRX
 */
@ParametersAreNonnullByDefault
public class TalonConfiguration {
  public static final TalonConfiguration DEFAULT =
      new TalonConfiguration(
          "DEFAULT",
          FeedbackSensor.DEFAULT,
          LimitSwitches.DEFAULT,
          SoftLimits.DEFAULT,
          CurrentLimits.DEFAULT,
          VelocityMeasurement.DEFAULT,
          Output.DEFAULT,
          MotionMagic.DEFAULT,
          Arrays.asList(
              ClosedLoopProfile.DEFAULT,
              ClosedLoopProfile.DEFAULT,
              ClosedLoopProfile.DEFAULT,
              ClosedLoopProfile.DEFAULT),
          Collections.emptyList());
  static final int PROFILE_COUNT = 4;
  private static final Logger logger = LoggerFactory.getLogger(TalonConfiguration.class);

  private final String name;
  private final FeedbackSensor selectedFeedbackSensor;
  private final LimitSwitches limitSwitch;
  private final SoftLimits softLimit;
  private final CurrentLimits currentLimit;
  private final VelocityMeasurement velocityMeasurement;
  private final Output output;
  private final MotionMagic motionMagic;
  private final List<ClosedLoopProfile> closedLoopProfile;
  private final List<Integer> talonIds;

  public TalonConfiguration(
      String name,
      FeedbackSensor selectedFeedbackSensor,
      LimitSwitches limitSwitch,
      SoftLimits softLimit,
      CurrentLimits currentLimit,
      VelocityMeasurement velocityMeasurement,
      Output output,
      MotionMagic motionMagic,
      List<ClosedLoopProfile> closedLoopProfile,
      List<Integer> talonIds) {
    this.name = name;
    this.selectedFeedbackSensor = selectedFeedbackSensor;
    this.limitSwitch = limitSwitch;
    this.softLimit = softLimit;
    this.currentLimit = currentLimit;
    this.velocityMeasurement = velocityMeasurement;
    this.output = output;
    this.motionMagic = motionMagic;
    this.closedLoopProfile = closedLoopProfile;
    this.talonIds = talonIds;
  }

  public static TalonConfiguration create(@Nullable Toml toml) {
    if (toml == null) {
      return DEFAULT;
    }
    String name = toml.getString("name", DEFAULT.name);
    List<Integer> talonIds = new ArrayList<>();
    for (Long l : toml.getList("talonIds", Collections.<Long>emptyList())) {
      talonIds.add(l.intValue());
    }

    FeedbackSensor feedbackSensor;
    LimitSwitches limitSwitches;
    SoftLimits softLimits;
    CurrentLimits currentLimits;
    VelocityMeasurement velocityMeasurement;
    Output output;
    MotionMagic motionMagic;
    List<ClosedLoopProfile> closedLoopProfiles;

    feedbackSensor = FeedbackSensor.create(toml.getTable("selectedFeedbackSensor"));
    limitSwitches = LimitSwitches.create(toml.getTable("limitSwitch"));
    softLimits = SoftLimits.create(toml.getTable("softLimit"));
    currentLimits = CurrentLimits.create(toml.getTable("currentLimit"));
    velocityMeasurement = VelocityMeasurement.create(toml.getTable("velocityMeasurement"));
    output = Output.create(toml.getTable("output"));
    motionMagic = MotionMagic.create(toml.getTable("motionMagic"));
    closedLoopProfiles = getClosedLoopProfiles(toml, name);

    TalonConfiguration configuration =
        new TalonConfiguration(
            name,
            feedbackSensor,
            limitSwitches,
            softLimits,
            currentLimits,
            velocityMeasurement,
            output,
            motionMagic,
            closedLoopProfiles,
            talonIds);

    assert (configuration.closedLoopProfile.size() == PROFILE_COUNT);

    return configuration;
  }

  @NotNull
  private static List<ClosedLoopProfile> getClosedLoopProfiles(@NotNull Toml toml, String name) {
    List<ClosedLoopProfile> closedLoopProfiles = new ArrayList<>(PROFILE_COUNT);
    final String TABLE = "closedLoopProfile";
    List<Toml> profileTables =
        toml.containsTable(TABLE) ? toml.getTables(TABLE) : Collections.emptyList();
    if (profileTables.size() > PROFILE_COUNT) {
      logger.error(
          "{}: truncating closed-loop profiles {} > {}", name, profileTables.size(), PROFILE_COUNT);
      profileTables = profileTables.subList(0, PROFILE_COUNT);
    }

    for (Toml table : profileTables) {
      closedLoopProfiles.add(ClosedLoopProfile.create(table));
    }
    // fill in any remaining with default
    for (int i = 0; i < PROFILE_COUNT - profileTables.size(); i++) {
      closedLoopProfiles.add(ClosedLoopProfile.DEFAULT);
    }
    return closedLoopProfiles;
  }

  /**
   * Configure a Talon with saved settings.
   *
   * @param talon the Talon to configure
   * @param timeout the configuration CAN bus timeout
   */
  public void configure(TalonSRX talon, int timeout) {
    selectedFeedbackSensor.configure(talon, timeout);
    limitSwitch.configure(talon, timeout);
    softLimit.configure(talon, timeout);
    currentLimit.configure(talon, timeout);
    velocityMeasurement.configure(talon, timeout);
    output.configure(talon, timeout);
    motionMagic.configure(talon, timeout);
    for (int i = 0; i < closedLoopProfile.size(); i++) {
      closedLoopProfile.get(i).configure(talon, i, timeout);
    }
    logger.info("Configured Talon {} with '{}'", talon.getDeviceID(), name);
  }

  public String getName() {
    return name;
  }

  public FeedbackSensor getSelectedFeedbackSensor() {
    return selectedFeedbackSensor;
  }

  public LimitSwitches getLimitSwitch() {
    return limitSwitch;
  }

  public SoftLimits getSoftLimit() {
    return softLimit;
  }

  public CurrentLimits getCurrentLimit() {
    return currentLimit;
  }

  public VelocityMeasurement getVelocityMeasurement() {
    return velocityMeasurement;
  }

  public Output getOutput() {
    return output;
  }

  public MotionMagic getMotionMagic() {
    return motionMagic;
  }

  public List<ClosedLoopProfile> getClosedLoopProfiles() {
    return Collections.unmodifiableList(closedLoopProfile);
  }

  public List<Integer> getTalonIds() {
    return Collections.unmodifiableList(talonIds);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TalonConfiguration that = (TalonConfiguration) o;
    return Objects.equals(name, that.name)
        && Objects.equals(selectedFeedbackSensor, that.selectedFeedbackSensor)
        && Objects.equals(limitSwitch, that.limitSwitch)
        && Objects.equals(softLimit, that.softLimit)
        && Objects.equals(currentLimit, that.currentLimit)
        && Objects.equals(velocityMeasurement, that.velocityMeasurement)
        && Objects.equals(output, that.output)
        && Objects.equals(motionMagic, that.motionMagic)
        && Objects.equals(closedLoopProfile, that.closedLoopProfile)
        && Objects.equals(talonIds, that.talonIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        name,
        selectedFeedbackSensor,
        limitSwitch,
        softLimit,
        currentLimit,
        velocityMeasurement,
        output,
        motionMagic,
        closedLoopProfile,
        talonIds);
  }

  @Override
  public String toString() {
    return "TalonConfiguration{"
        + "name='"
        + name
        + '\''
        + ", selectedFeedbackSensor="
        + selectedFeedbackSensor
        + ", limitSwitch="
        + limitSwitch
        + ", softLimit="
        + softLimit
        + ", currentLimit="
        + currentLimit
        + ", velocityMeasurement="
        + velocityMeasurement
        + ", output="
        + output
        + ", motionMagic="
        + motionMagic
        + ", closedLoopProfile="
        + closedLoopProfile
        + ", talonIds="
        + talonIds
        + '}';
  }

  /** Contains Talon closed-loop parameters that are associated with one of the profile slots. */
  static class ClosedLoopProfile {

    public static final ClosedLoopProfile DEFAULT =
        new ClosedLoopProfile(0d, 0d, 0d, 0d, 0, Double.MAX_VALUE, 0);
    private static final Toml DEFAULT_TOML =
        new Toml().read(new TomlWriter().write(ClosedLoopProfile.DEFAULT));

    private final double pGain;
    private final double iGain;
    private final double dGain;
    private final double fGain;
    private final int iZone;
    private final double maxIntegralAccumulator;
    private final int allowableClosedLoopError;

    ClosedLoopProfile(
        double pGain,
        double iGain,
        double dGain,
        double fGain,
        int iZone,
        double maxIntegralAccumulator,
        int allowableClosedLoopError) {
      this.pGain = pGain;
      this.iGain = iGain;
      this.dGain = dGain;
      this.fGain = fGain;
      this.iZone = iZone;
      this.maxIntegralAccumulator = maxIntegralAccumulator;
      this.allowableClosedLoopError = allowableClosedLoopError;
    }

    public static ClosedLoopProfile create(@Nullable Toml toml) {
      if (toml == null) {
        return DEFAULT;
      }
      return new Toml(DEFAULT_TOML).read(toml).to(ClosedLoopProfile.class);
    }

    void configure(TalonSRX talon, int slotIdx, int timeout) {
      ErrorCode err = talon.config_kP(slotIdx, pGain, timeout);
      Errors.check(talon, "config_kP", err, logger);
      err = talon.config_kI(slotIdx, iGain, timeout);
      Errors.check(talon, "config_kI", err, logger);
      err = talon.config_kD(slotIdx, dGain, timeout);
      Errors.check(talon, "config_kD", err, logger);
      err = talon.config_kF(slotIdx, fGain, timeout);
      Errors.check(talon, "config_kF", err, logger);
      err = talon.config_IntegralZone(slotIdx, iZone, timeout);
      Errors.check(talon, "config_IntegralZone", err, logger);
      err = talon.configMaxIntegralAccumulator(slotIdx, maxIntegralAccumulator, timeout);
      Errors.check(talon, "configMaxIntegralAccumulator", err, logger);
      err = talon.configAllowableClosedloopError(slotIdx, allowableClosedLoopError, timeout);
      Errors.check(talon, "configAllowableClosedloopError", err, logger);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ClosedLoopProfile that = (ClosedLoopProfile) o;
      return Double.compare(that.pGain, pGain) == 0
          && Double.compare(that.iGain, iGain) == 0
          && Double.compare(that.dGain, dGain) == 0
          && Double.compare(that.fGain, fGain) == 0
          && iZone == that.iZone
          && Double.compare(that.maxIntegralAccumulator, maxIntegralAccumulator) == 0
          && allowableClosedLoopError == that.allowableClosedLoopError;
    }

    @Override
    public int hashCode() {
      return Objects.hash(
          pGain, iGain, dGain, fGain, iZone, maxIntegralAccumulator, allowableClosedLoopError);
    }

    @Override
    public String toString() {
      return "ClosedLoopProfile{"
          + "pGain="
          + pGain
          + ", iGain="
          + iGain
          + ", dGain="
          + dGain
          + ", fGain="
          + fGain
          + ", iZone="
          + iZone
          + ", maxIntegralAccumulator="
          + maxIntegralAccumulator
          + ", allowableClosedLoopError="
          + allowableClosedLoopError
          + '}';
    }
  }
}
