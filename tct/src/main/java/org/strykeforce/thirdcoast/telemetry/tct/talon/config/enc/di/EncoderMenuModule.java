package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.di;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.di.SubConfigScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.EncoderMenu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.ReverseOutputCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.ReverseSensorCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.SelectTypeCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.SetPositionCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.VelocityMeasurementPeriodCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.VelocityMeasurementWindowCommand;

@Module
public abstract class EncoderMenuModule {

  public final static List<String> MENU_ORDER = Arrays.asList(
      SelectTypeCommand.NAME,
      SetPositionCommand.NAME,
      ReverseSensorCommand.NAME,
      ReverseOutputCommand.NAME,
      VelocityMeasurementPeriodCommand.NAME,
      VelocityMeasurementWindowCommand.NAME
  );

  @SubConfigScoped
  @Provides
  @EncoderMenu
  public static CommandAdapter configCommandsAdapter(@EncoderMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @SubConfigScoped
  @Provides
  @EncoderMenu
  public static Menu configMenu(@EncoderMenu CommandAdapter commandAdapter, Terminal terminal) {
    return new Menu(commandAdapter, terminal);
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
  public abstract Command velocityMeasurementWindowCommand(VelocityMeasurementWindowCommand command);

  @SubConfigScoped
  @Binds
  @IntoSet
  @EncoderMenu
  public abstract Command velocityMeasurementPeriodCommand(VelocityMeasurementPeriodCommand command);
}
