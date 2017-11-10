package org.strykeforce.thirdcoast.telemetry.tct.talon.di;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.util.Set;
import org.jline.reader.LineReader;
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
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonModeMenu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.di.ConfigMenuComponent;

@Module(subcomponents = ConfigMenuComponent.class)
public abstract class TalonMenuModule {

  @ModeScoped
  @Provides
  @TalonMenu
  public static CommandAdapter talonCommandsAdapter(@TalonMenu Set<Command> commands) {
    return new CommandAdapter("TALON", commands);
  }

  @ModeScoped
  @Provides
  @TalonMenu
  public static Menu talonMenu(@TalonMenu CommandAdapter commandAdapter, LineReader reader,
      TalonSet talonSet) {
    return new TalonModeMenu(commandAdapter, reader, talonSet);
  }

  @ModeScoped
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command configCommand(ConfigModeCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command listCommand(ListCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command selectCommand(SelectCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command loadCommand(LoadConfigsCommand command);


  @ModeScoped
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command inspectCommand(InspectCommand command);


  @ModeScoped
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command runCommand(RunCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @TalonMenu
  public abstract Command saveConfigCommand(SaveConfigCommand command);


}
