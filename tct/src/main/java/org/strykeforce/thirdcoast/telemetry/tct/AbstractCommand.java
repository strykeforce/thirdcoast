package org.strykeforce.thirdcoast.telemetry.tct;

import java.util.Optional;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 * Abstract implementation of a menu command.
 */
public abstract class AbstractCommand implements Command {

  protected final String name;
  protected final Terminal terminal;
  protected final LineReader reader;
  protected int weight;

  /**
   * Construct a command.
   *
   * @param name the command menu name.
   * @param weight the display weight.
   * @param reader the LineReader to use for terminal input.
   */
  public AbstractCommand(String name, int weight, LineReader reader) {
    this.name = name;
    this.weight = weight;
    this.reader = reader;
    this.terminal = reader.getTerminal();
  }

  /**
   * Construct a command with default display weight of zero.
   *
   * @param name the command menu name.
   * @param reader the LineReader to use for terminal input.
   */
  public AbstractCommand(String name, LineReader reader) {
    this(name, 0, reader);
  }

  protected static String bold(String text) {
    return new AttributedStringBuilder().style(AttributedStyle.BOLD).append(text).toAnsi();
  }

  protected static String boldYellow(String text) {
    return new AttributedStringBuilder()
        .style(AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW)).append(text).toAnsi();
  }

  /**
   * Get the command menu name.
   *
   * @return the command menu name.
   */
  @Override
  public String name() {
    return name;
  }

  /**
   * Get the display weight. The menu is sorted by weight and then sorted alphabetically.
   *
   * @return the display weight.
   */
  @Override
  public int weight() {
    return weight;
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
  public Optional<Command> post() {
    return Optional.empty();
  }


}
