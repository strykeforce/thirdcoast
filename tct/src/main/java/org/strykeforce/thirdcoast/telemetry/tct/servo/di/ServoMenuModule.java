package org.strykeforce.thirdcoast.telemetry.tct.servo.di;

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
import org.strykeforce.thirdcoast.telemetry.tct.servo.RunServoCommand;
import org.strykeforce.thirdcoast.telemetry.tct.servo.SelectServoCommand;

@Module
public abstract class ServoMenuModule {

  @ModeScoped
  @Provides
  @ServoMenu
  static CommandAdapter talonCommandsAdapter(@ServoMenu Set<Command> commands) {
    return new CommandAdapter("SERVO", commands);
  }

  @ModeScoped
  @Provides
  @ServoMenu
  static Menu talonMenu(@ServoMenu CommandAdapter commandAdapter, LineReader reader) {
    return new Menu(commandAdapter, reader);
  }

  @ModeScoped
  @Binds
  @IntoSet
  @ServoMenu
  public abstract Command selectServoCommand(SelectServoCommand command);

  @ModeScoped
  @Binds
  @IntoSet
  @ServoMenu
  public abstract Command runServoCommand(RunServoCommand command);


}
