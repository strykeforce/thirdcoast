package org.strykeforce.thirdcoast.telemetry.tct.talon.di;

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
import org.strykeforce.thirdcoast.telemetry.tct.di.ModeScoped;
import org.strykeforce.thirdcoast.telemetry.tct.talon.ConfigModeCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.InspectCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.ListCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.LoadConfigsCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.RunCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.SaveConfigCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.SelectCommand;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonMenu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.di.ConfigMenuComponent;

@Module(subcomponents = ConfigMenuComponent.class)
public abstract class TalonMenuModule {

  public static final List<String> MENU_ORDER = Arrays.asList(
      LoadConfigsCommand.NAME,
      SelectCommand.NAME,
      ListCommand.NAME,
      ConfigModeCommand.NAME,
      InspectCommand.NAME,
      SaveConfigCommand.NAME,
      RunCommand.NAME
  );

  @ModeScoped
  @Provides
  @org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenu
  public static CommandAdapter talonCommandsAdapter(
      @org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenu Set<Command> commands) {
    return new CommandAdapter(commands);
  }

  @ModeScoped
  @Provides
  @org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenu
  public static Menu talonMenu(
      @org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenu CommandAdapter commandAdapter,
      Terminal terminal, TalonSet talonSet) {
    return new TalonMenu(commandAdapter, terminal, talonSet);
  }

  @ModeScoped
  @Binds
  @IntoSet
  @org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenu
  public abstract Command configCommand(ConfigModeCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenu
  public abstract Command listCommand(ListCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenu
  public abstract Command selectCommand(SelectCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenu
  public abstract Command loadCommand(LoadConfigsCommand command);


  @ModeScoped
  @Binds
  @IntoSet
  @org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenu
  public abstract Command inspectCommand(InspectCommand command);


  @ModeScoped
  @Binds
  @IntoSet
  @org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenu
  public abstract Command runCommand(RunCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @org.strykeforce.thirdcoast.telemetry.tct.talon.di.TalonMenu
  public abstract Command saveConfigCommand(SaveConfigCommand command);


}
