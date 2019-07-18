package org.strykeforce.thirdcoast.telemetry;

import okio.Buffer;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.strykeforce.thirdcoast.telemetry.grapher.ResourceHelper;
import org.strykeforce.thirdcoast.telemetry.graphable.Graphable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.*;

@ExtendWith(MockitoExtension.class)
class RobotInventoryTest {

  @Mock private Graphable graphableOne;
  @Mock private Graphable graphableTwo;
  private Collection<Graphable> graphables;

  @BeforeEach
  void setUp() {
    graphables = List.of(graphableOne, graphableTwo);
  }

  @Test
  void graphableForIdea() {
    RobotInventory inventory = new RobotInventory(graphables);
    assertThat(inventory.graphableForId(0)).isSameAs(graphableOne);
  }

  @Test
  void writeInventory() throws IOException, JSONException {
    when(graphableOne.getMeasures()).thenReturn(Set.of(POSITION, VALUE));
    when(graphableOne.getType()).thenReturn("one");
    when(graphableTwo.getMeasures()).thenReturn(Set.of(BUS_VOLTAGE));
    when(graphableTwo.getType()).thenReturn("two");
    RobotInventory inventory = new RobotInventory(graphables);
    Buffer buffer = new Buffer();
    inventory.writeInventory(buffer);

    String actual = buffer.readString(Charset.defaultCharset());

    String golden = ResourceHelper.getString("/inventory.json");

    JSONAssert.assertEquals(golden, actual, false);
  }
}
