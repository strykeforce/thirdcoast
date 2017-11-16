package org.strykeforce.thirdcoast.telemetry.tct.dio;

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

@Module
public abstract class DioMenuModule {

  @Provides
  @Named("DIO")
  static CommandAdapter talonCommandsAdapter(@Named("DIO") Set<Command> commands) {
    return new CommandAdapter("DIO", commands);
  }

  @Provides
  @Named("DIO")
  static Menu talonMenu(@Named("DIO") CommandAdapter commandAdapter, LineReader reader,
      DigitalOutputSet digitalOutputSet) {
    return new DioMenu(commandAdapter, reader, digitalOutputSet);
  }

  @Binds
  @IntoSet
  @Named("DIO")
  public abstract Command selectDigitalOutputCommand(SelectDigitalOutputCommand command);

  @Binds
  @IntoSet
  @Named("DIO")
  public abstract Command runDigitalOutputCommand(RunDigitalOutputCommand command);

  @Binds
  @IntoSet
  @Named("DIO")
  public abstract Command pulseDigitalOutputCommand(PulseDigitalOutputCommand command);

  @Binds
  @IntoSet
  @Named("DIO")
  public abstract Command demoPulseDigitalOutputCommand(DemoPulseDigitalOutputCommand command);

  @Binds
  @IntoSet
  @Named("DIO")
  public abstract Command pwmDigitalOutputCommand(PwmDigitalOutputCommand command);

  @Binds
  @IntoSet
  @Named("DIO")
  public abstract Command demoPwmDigitalOutputCommand(DemoPwmDigitalOutputCommand command);

  @Binds
  @IntoSet
  @Named("DIO")
  public abstract Command inspectDigitalInputsCommand(InspectDigitalInputsCommand command);

}
