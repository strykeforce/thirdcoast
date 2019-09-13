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
import org.strykeforce.thirdcoast.telemetry.item.Measurable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.*;

@ExtendWith(MockitoExtension.class)
class RobotInventoryTest {

  @Mock private Measurable itemOne;
  @Mock private Measurable itemTwo;
  private Collection<Measurable> items;

  @BeforeEach
  void setUp() {
    items = List.of(itemOne, itemTwo);
  }

  @Test
  void itemForIdea() {
    RobotInventory inventory = new RobotInventory(items);
    assertThat(inventory.itemForId(0)).isSameAs(itemOne);
  }

  @Test
  void writeInventory() throws IOException, JSONException {
    when(itemOne.getMeasures()).thenReturn(Set.of(POSITION, VALUE));
    when(itemOne.getType()).thenReturn("one");
    when(itemTwo.getMeasures()).thenReturn(Set.of(BUS_VOLTAGE));
    when(itemTwo.getType()).thenReturn("two");
    RobotInventory inventory = new RobotInventory(items);
    Buffer buffer = new Buffer();
    inventory.writeInventory(buffer);

    String actual = buffer.readString(Charset.defaultCharset());

    String golden = ResourceHelper.getString("/inventory.json");

    JSONAssert.assertEquals(golden, actual, false);
  }
}
