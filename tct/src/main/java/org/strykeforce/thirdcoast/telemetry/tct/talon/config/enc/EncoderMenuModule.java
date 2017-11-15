package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Set;
import javax.inject.Named;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonModeMenu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.AnalogTempVbatFrameRateCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.FeedbackFrameRateCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.GeneralFrameRateCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.PulseWidthFrameRateCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.QuadEncoderFrameRateCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.ReverseOutputCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.ReverseSensorCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.SelectTypeCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.SetPositionCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.VelocityMeasurementPeriodCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.VelocityMeasurementWindowCommand;

@Module
public abstract class EncoderMenuModule {

  @Provides
  @Named("TALON_CONFIG_ENC")
  public static CommandAdapter configCommandsAdapter(
      @Named("TALON_CONFIG_ENC") Set<Command> commands) {
    return new CommandAdapter("TALON_CONFIG_ENC", commands);
  }

  @Provides
  @Named("TALON_CONFIG_ENC")
  public static Menu configMenu(@Named("TALON_CONFIG_ENC") CommandAdapter commandAdapter,
      LineReader reader, TalonSet talonSet) {
    return new TalonModeMenu(commandAdapter, reader, talonSet);
  }

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_ENC")
  public abstract Command selectTypeCommand(SelectTypeCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_ENC")
  public abstract Command setPositionCommand(SetPositionCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_ENC")
  public abstract Command reverseSensorCommand(ReverseSensorCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_ENC")
  public abstract Command reverseOutputCommand(ReverseOutputCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_ENC")
  public abstract Command velocityMeasurementWindowCommand(
      VelocityMeasurementWindowCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_ENC")
  public abstract Command velocityMeasurementPeriodCommand(
      VelocityMeasurementPeriodCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_ENC")
  public abstract Command analogTempVbatFrameRateCommand(AnalogTempVbatFrameRateCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_ENC")
  public abstract Command feedbackFrameRateCommand(FeedbackFrameRateCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_ENC")
  public abstract Command generalFrameRateCommand(GeneralFrameRateCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_ENC")
  public abstract Command pulseWidthFrameRateCommand(PulseWidthFrameRateCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_ENC")
  public abstract Command quadEncoderFrameRateCommand(QuadEncoderFrameRateCommand command);
}
