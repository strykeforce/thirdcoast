package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.di;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Set;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.di.SubConfigScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonModeMenu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.ReverseOutputCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.ReverseSensorCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.SelectTypeCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.SetPositionCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.VelocityMeasurementPeriodCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.VelocityMeasurementWindowCommand;

@Module
public abstract class EncoderMenuModule {

  @SubConfigScoped
  @Provides
  @EncoderMenu
  public static CommandAdapter configCommandsAdapter(@EncoderMenu Set<Command> commands) {
    return new CommandAdapter("TALON_CONFIG_ENC", commands);
  }

  @SubConfigScoped
  @Provides
  @EncoderMenu
  public static Menu configMenu(@EncoderMenu CommandAdapter commandAdapter, LineReader reader,
      TalonSet talonSet) {
    return new TalonModeMenu(commandAdapter, reader, talonSet);
  }

  @SubConfigScoped
  @Binds
  @IntoSet
  @EncoderMenu
  public abstract Command selectTypeCommand(SelectTypeCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @EncoderMenu
  public abstract Command setPositionCommand(SetPositionCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @EncoderMenu
  public abstract Command reverseSensorCommand(ReverseSensorCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @EncoderMenu
  public abstract Command reverseOutputCommand(ReverseOutputCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @EncoderMenu
  public abstract Command velocityMeasurementWindowCommand(
      VelocityMeasurementWindowCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @EncoderMenu
  public abstract Command velocityMeasurementPeriodCommand(
      VelocityMeasurementPeriodCommand command);
}
