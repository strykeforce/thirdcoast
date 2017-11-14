package org.strykeforce.thirdcoast.telemetry.tct;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jline.reader.LineReader;
import org.strykeforce.thirdcoast.telemetry.tct.dio.di.DioMenuComponent;

/**
 * Command to enter mode for working with Dios.
 */
@ParametersAreNonnullByDefault
public class DioModeCommand extends AbstractCommand {

  public final static String NAME = "Work with Digital Outputs";
  private final Provider<DioMenuComponent.Builder> dioMenuComponentProvider;

  @Inject
  DioModeCommand(Provider<DioMenuComponent.Builder> dioMenuComponentProvider,
      LineReader reader) {
    super(NAME, reader);
    this.dioMenuComponentProvider = dioMenuComponentProvider;
  }

  @Override
  public void perform() {
    DioMenuComponent component = dioMenuComponentProvider.get().build();
    Menu menu = component.menu();
    menu.display();
  }
}
