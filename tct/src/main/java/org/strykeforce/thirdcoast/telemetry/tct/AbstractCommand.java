package org.strykeforce.thirdcoast.telemetry.tct;

import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 * Abstract implementation of a menu command.
 */
@ParametersAreNonnullByDefault
public abstract class AbstractCommand implements Command {

  protected final String name;
  protected final Terminal terminal;
  protected final LineReader reader;

  /**
   * Construct a command.
   *
   * @param name the command menu name.
   * @param reader the LineReader to use for terminal input.
   */
  public AbstractCommand(String name, LineReader reader) {
    this.name = name;
    this.reader = reader;
    this.terminal = reader.getTerminal();
  }

  /**
   * Get the command menu name.
   *
   * @return the command menu name.
   */
  @Override
  @NotNull
  public String name() {
    return name;
  }

  /**
   * Default action is to print out command information.
   */
  @Override
  public void perform() {
    terminal.writer().printf("performing %s with %s%n", name(), this.toString());
    terminal.flush();
  }

  /**
   * Default post command is empty.
   *
   * @return empty Optional
   */
  @Override
  @NotNull
  public Optional<Command> post() {
    return Optional.empty();
  }


}
