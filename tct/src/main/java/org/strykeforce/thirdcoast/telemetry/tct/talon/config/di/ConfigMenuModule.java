package org.strykeforce.thirdcoast.telemetry.tct.talon.config.di;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.Command;
import org.strykeforce.thirdcoast.telemetry.tct.CommandAdapter;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.di.ConfigScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonModeMenu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.ClosedLoopConfigCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.EncoderConfigCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.LimitConfigCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.OutputConfigCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.SelectOperatingModeCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.cl.di.ClosedLoopMenuComponent;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.di.EncoderMenuComponent;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim.di.LimitMenuComponent;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.di.OutputMenuComponent;

@Module(subcomponents = {
    ClosedLoopMenuComponent.class,
    OutputMenuComponent.class,
    EncoderMenuComponent.class,
    LimitMenuComponent.class,
})
public abstract class ConfigMenuModule {

  public final static List<String> MENU_ORDER = Arrays.asList(
      SelectOperatingModeCommand.NAME,
      ClosedLoopConfigCommand.NAME,
      OutputConfigCommand.NAME,
      EncoderConfigCommand.NAME,
      LimitConfigCommand.NAME
  );

  @ConfigScoped
  @Provides
  @TalonConfigMenu
  public static CommandAdapter configCommandsAdapter(@TalonConfigMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @ConfigScoped
  @Provides
  @TalonConfigMenu
  public static Menu configMenu(@TalonConfigMenu CommandAdapter commandAdapter, LineReader reader,
      TalonSet talonSet) {
    return new TalonModeMenu(commandAdapter, reader, talonSet);
  }

  @ConfigScoped
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command selectOperatingModeCommand(SelectOperatingModeCommand command);

  @ConfigScoped
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command closedLoopConfigCommand(ClosedLoopConfigCommand command);

  @ConfigScoped
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command voltageRampRateCommand(OutputConfigCommand command);

  @ConfigScoped
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command encoderConfigCommand(EncoderConfigCommand command);

  @ConfigScoped
  @Binds
  @IntoSet
  @TalonConfigMenu
  public abstract Command limitConfigCommand(LimitConfigCommand command);

}
