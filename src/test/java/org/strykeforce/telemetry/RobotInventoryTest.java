package org.strykeforce.telemetry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import okio.Buffer;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.strykeforce.telemetry.grapher.ResourceHelper;
import org.strykeforce.telemetry.item.Measurable;
import org.strykeforce.telemetry.item.Measure;

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
    when(itemOne.getMeasures())
        .thenReturn(
            Set.of(new Measure("POSITION", "POSITION"), new Measure("VALUE", "VALUE description")));
    when(itemOne.getType()).thenReturn("one");
    when(itemTwo.getMeasures()).thenReturn(Set.of(new Measure("BUS_VOLTAGE", "BUS_VOLTAGE")));
    when(itemTwo.getType()).thenReturn("two");
    RobotInventory inventory = new RobotInventory(items);
    Buffer buffer = new Buffer();
    inventory.writeInventory(buffer);

    String actual = buffer.readString(Charset.defaultCharset());
    String golden = ResourceHelper.getString("/inventory.json");

    JSONAssert.assertEquals(golden, actual, false);
  }
}
