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
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonMenu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;

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
  public static Menu configMenu(
      @Named("TALON_CONFIG_ENC") CommandAdapter commandAdapter,
      LineReader reader,
      TalonSet talonSet) {
    return new TalonMenu(commandAdapter, reader, talonSet);
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
  public abstract Command defaultFrameRatesCommand(DefaultFrameRatesCommand command);

  @Binds
  @IntoSet
  @Named("TALON_CONFIG_ENC")
  public abstract Command grapherFrameRatesCommand(GrapherFrameRatesCommand command);

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
