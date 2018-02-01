package org.strykeforce.thirdcoast.talon.config;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Errors;

@ParametersAreNonnullByDefault
public class Output {

  public static final Output DEFAULT =
      new Output(
          Limits.DEFAULT,
          Limits.DEFAULT,
          RampRates.DEFAULT,
          VoltageCompensation.DEFAULT,
          0.04,
          false,
          NeutralMode.Coast);

  private static final Logger logger = LoggerFactory.getLogger(Output.class);

  private final double neutralDeadband;
  private final boolean inverted;
  private final NeutralMode neutralMode;
  private final Limits forward;
  private final Limits reverse;
  private final RampRates rampRates;
  private final VoltageCompensation voltageCompensation;

  public Output(
      Limits forward,
      Limits reverse,
      RampRates rampRates,
      VoltageCompensation voltageCompensation,
      double neutralDeadband,
      boolean inverted,
      NeutralMode neutralMode) {
    this.forward = forward;
    this.reverse = reverse;
    this.rampRates = rampRates;
    this.voltageCompensation = voltageCompensation;
    this.neutralDeadband = neutralDeadband;
    this.inverted = inverted;
    this.neutralMode = neutralMode;
  }

  public static Output create(@Nullable Toml toml) {
    if (toml == null) {
      return DEFAULT;
    }
    return new Output(
        Limits.create(toml.getTable("forward")),
        Limits.create(toml.getTable("reverse")),
        RampRates.create(toml.getTable("rampRates")),
        VoltageCompensation.create(toml.getTable("voltageCompensation")),
        toml.getDouble("neutralDeadband", 0.04),
        toml.getBoolean("inverted", false),
        NeutralMode.valueOf(toml.getString("neutralMode", DEFAULT.neutralMode.name())));
  }

  public void configure(TalonSRX talon, int timeout) {
    ErrorCode err = talon.configNeutralDeadband(neutralDeadband, timeout);
    Errors.check(talon, "configNeutralDeadband", err, logger);
    talon.setInverted(inverted);

    err = talon.configNominalOutputForward(forward.nominal, timeout);
    Errors.check(talon, "configNominalOutputForward", err, logger);
    err = talon.configPeakOutputForward(forward.peak, timeout);
    Errors.check(talon, "configPeakOutputForward", err, logger);
    err = talon.configNominalOutputReverse(reverse.nominal, timeout);
    Errors.check(talon, "configNominalOutputReverse", err, logger);
    err = talon.configPeakOutputReverse(reverse.peak, timeout);
    Errors.check(talon, "configPeakOutputReverse", err, logger);

    err = talon.configOpenloopRamp(rampRates.openLoop, timeout);
    Errors.check(talon, "configOpenloopRamp", err, logger);
    err = talon.configClosedloopRamp(rampRates.closedLoop, timeout);
    Errors.check(talon, "configClosedloopRamp", err, logger);

    err = talon.configVoltageCompSaturation(voltageCompensation.saturation, timeout);
    Errors.check(talon, "configVoltageCompSaturation", err, logger);
    talon.enableVoltageCompensation(voltageCompensation.enabled);
    // TODO: verify measurement filter default below - this is a guess!
    //    err = talon.configVoltageMeasurementFilter(voltageCompensation.measurementFilter,
    // timeout);
    //    Errors.check(talon, "configVoltageMeasurementFilter", err, logger);
    talon.setNeutralMode(neutralMode);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Output output = (Output) o;
    return Double.compare(output.neutralDeadband, neutralDeadband) == 0
        && inverted == output.inverted
        && neutralMode == output.neutralMode
        && Objects.equals(forward, output.forward)
        && Objects.equals(reverse, output.reverse)
        && Objects.equals(rampRates, output.rampRates)
        && Objects.equals(voltageCompensation, output.voltageCompensation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        neutralDeadband, inverted, neutralMode, forward, reverse, rampRates, voltageCompensation);
  }

  @Override
  public String toString() {
    return "Output{"
        + "neutralDeadband="
        + neutralDeadband
        + ", inverted="
        + inverted
        + ", neutralMode="
        + neutralMode
        + ", forward="
        + forward
        + ", reverse="
        + reverse
        + ", rampRates="
        + rampRates
        + ", voltageCompensation="
        + voltageCompensation
        + '}';
  }

  public static class Limits {
    public static final Limits DEFAULT = new Limits(0d, 1d);
    private static final Toml DEFAULT_TOML =
        new Toml().read(new TomlWriter().write(Limits.DEFAULT));

    private final double peak;
    private final double nominal;

    Limits(double nominal, double peak) {
      this.peak = peak;
      this.nominal = nominal;
    }

    public static Limits create(@Nullable Toml toml) {
      if (toml == null) {
        return DEFAULT;
      }
      return new Toml(DEFAULT_TOML).read(toml).to(Limits.class);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Limits limits = (Limits) o;
      return Double.compare(limits.peak, peak) == 0 && Double.compare(limits.nominal, nominal) == 0;
    }

    @Override
    public int hashCode() {
      return Objects.hash(peak, nominal);
    }

    @Override
    public String toString() {
      return "State{" + "peak=" + peak + ", nominal=" + nominal + '}';
    }
  }

  public static class RampRates {
    public static final RampRates DEFAULT = new RampRates(0d, 0d);
    private static final Toml DEFAULT_TOML =
        new Toml().read(new TomlWriter().write(RampRates.DEFAULT));

    private final double openLoop;
    private final double closedLoop;

    RampRates(double openLoop, double closedLoop) {
      this.openLoop = openLoop;
      this.closedLoop = closedLoop;
    }

    public static RampRates create(@Nullable Toml toml) {
      if (toml == null) {
        return DEFAULT;
      }
      return new Toml(DEFAULT_TOML).read(toml).to(RampRates.class);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      RampRates rampRates = (RampRates) o;
      return Double.compare(rampRates.openLoop, openLoop) == 0
          && Double.compare(rampRates.closedLoop, closedLoop) == 0;
    }

    @Override
    public int hashCode() {
      return Objects.hash(openLoop, closedLoop);
    }

    @Override
    public String toString() {
      return "RampRates{" + "openLoop=" + openLoop + ", closedLoop=" + closedLoop + '}';
    }
  }

  public static class VoltageCompensation {
    public static final VoltageCompensation DEFAULT = new VoltageCompensation(12d, true, 32);
    private static final Toml DEFAULT_TOML =
        new Toml().read(new TomlWriter().write(VoltageCompensation.DEFAULT));

    private final double saturation;
    private final boolean enabled;
    private final int measurementFilter;

    VoltageCompensation(double saturation, boolean enabled, int measurementFilter) {
      this.saturation = saturation;
      this.enabled = enabled;
      this.measurementFilter = measurementFilter;
    }

    public static VoltageCompensation create(@Nullable Toml toml) {
      if (toml == null) {
        return DEFAULT;
      }
      return new Toml(DEFAULT_TOML).read(toml).to(VoltageCompensation.class);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      VoltageCompensation that = (VoltageCompensation) o;
      return Double.compare(that.saturation, saturation) == 0
          && enabled == that.enabled
          && measurementFilter == that.measurementFilter;
    }

    @Override
    public int hashCode() {
      return Objects.hash(saturation, enabled, measurementFilter);
    }

    @Override
    public String toString() {
      return "VoltageCompensation{"
          + "saturation="
          + saturation
          + ", enabled="
          + enabled
          + ", measurementFilter="
          + measurementFilter
          + '}';
    }
  }
}
