package org.strykeforce.thirdcoast.telemetry.tct.servo;

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
import org.strykeforce.thirdcoast.telemetry.tct.servo.RunServoCommand;
import org.strykeforce.thirdcoast.telemetry.tct.servo.SelectServoCommand;

@Module
public abstract class ServoMenuModule {

  @Provides
  @Named("SERVO")
  static CommandAdapter servoCommandsAdapter(@Named("SERVO") Set<Command> commands) {
    return new CommandAdapter("SERVO", commands);
  }

  @Provides
  @Named("SERVO")
  static Menu servoMenu(@Named("SERVO") CommandAdapter commandAdapter, LineReader reader) {
    return new Menu(commandAdapter, reader);
  }

  @Binds
  @IntoSet
  @Named("SERVO")
  public abstract Command selectServoCommand(SelectServoCommand command);

  @Binds
  @IntoSet
  @Named("SERVO")
  public abstract Command runServoCommand(RunServoCommand command);


}
