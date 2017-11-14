package org.strykeforce.thirdcoast.telemetry.tct.dio.di;

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
import org.strykeforce.thirdcoast.telemetry.tct.dio.DemoPulseDigitalOutputCommand;
import org.strykeforce.thirdcoast.telemetry.tct.dio.DemoPwmDigitalOutputCommand;
import org.strykeforce.thirdcoast.telemetry.tct.dio.PulseDigitalOutputCommand;
import org.strykeforce.thirdcoast.telemetry.tct.dio.PwmDigitalOutputCommand;
import org.strykeforce.thirdcoast.telemetry.tct.dio.RunDigitalOutputCommand;
import org.strykeforce.thirdcoast.telemetry.tct.dio.SelectDigitalOutputCommand;

@Module
public abstract class DioMenuModule {

  @ModeScoped
  @Provides
  @DioMenu
  static CommandAdapter talonCommandsAdapter(@DioMenu Set<Command> commands) {
    return new CommandAdapter("DIO", commands);
  }

  @ModeScoped
  @Provides
  @DioMenu
  static Menu talonMenu(@DioMenu CommandAdapter commandAdapter, LineReader reader) {
    return new Menu(commandAdapter, reader);
  }

  @ModeScoped
  @Binds
  @IntoSet
  @DioMenu
  public abstract Command selectDigitalOutputCommand(SelectDigitalOutputCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @DioMenu
  public abstract Command runDigitalOutputCommand(RunDigitalOutputCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @DioMenu
  public abstract Command pulseDigitalOutputCommand(PulseDigitalOutputCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @DioMenu
  public abstract Command demoPulseDigitalOutputCommand(DemoPulseDigitalOutputCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @DioMenu
  public abstract Command pwmDigitalOutputCommand(PwmDigitalOutputCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @DioMenu
  public abstract Command demoPwmDigitalOutputCommand(DemoPwmDigitalOutputCommand command);

}
