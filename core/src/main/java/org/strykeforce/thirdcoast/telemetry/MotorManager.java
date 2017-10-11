package org.strykeforce.thirdcoast.telemetry;

import com.ctre.CANTalon;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import javax.inject.Inject;
import okio.BufferedSink;
import org.strykeforce.thirdcoast.telemetry.json.Talon;

public class MotorManager {

  private final Set<CANTalon> talons = new CopyOnWriteArraySet<>();

  @Inject
  public MotorManager() {
  }

  public void toJson(BufferedSink sink) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(List.class, Talon.class);
    JsonAdapter<List<Talon>> adapter = moshi.adapter(type);
    adapter = adapter.indent("  ");
    List<Talon> tl = talons.stream().map(t -> new Talon(t)).collect(Collectors.toList());
    adapter.toJson(sink, tl);
  }

  public void register(CANTalon talon) {
    talons.add(talon);
  }

  public void registerAll(Collection<CANTalon> collection) {
    collection.forEach(talon -> talons.add(talon));
  }

  public Collection<CANTalon> talons() {
    return Collections.unmodifiableCollection(talons);
  }

}
