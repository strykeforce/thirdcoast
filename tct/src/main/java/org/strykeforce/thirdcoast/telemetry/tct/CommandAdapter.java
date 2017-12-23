package org.strykeforce.thirdcoast.telemetry.tct;

import static java.util.Comparator.comparing;

import com.moandjiezana.toml.Toml;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CommandAdapter {

  private final List<Command> commands;
  private List<String> menuOrder;

  public CommandAdapter(String name, Set<Command> commands) {
    loadMenuOrder(name);
    this.commands =
        commands
            .stream()
            .sorted(comparing(this::getWeight).thenComparing(Command::name))
            .collect(Collectors.toList());
  }

  public CommandAdapter(Set<Command> commands) {
    this("DEFAULT", commands);
  }

  String getMenuText(int position) {
    return commands.get(position).name();
  }

  int getCount() {
    return commands.size();
  }

  public List<Command> getCommands() {
    return Collections.unmodifiableList(commands);
  }

  public void perform(int position) {
    Command command = commands.get(position);
    command.perform();
    if (command.post().isPresent()) {
      command.post().get().perform();
    }
  }

  @SuppressWarnings("unchecked")
  void loadMenuOrder(String name) {
    Toml toml = new Toml();
    toml.read(this.getClass().getResourceAsStream("menu.toml"));
    Map<String, Object> map = toml.toMap();
    menuOrder = (List<String>) map.get(name);
  }

  int getWeight(Command command) {
    if (menuOrder == null) {
      return 0;
    }
    String className = command.getClass().getName();
    return menuOrder.indexOf(className);
  }
}
