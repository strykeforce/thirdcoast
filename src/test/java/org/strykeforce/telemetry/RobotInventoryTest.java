package org.strykeforce.telemetry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.strykeforce.telemetry.grapher.ResourceHelper;
import org.strykeforce.telemetry.measurable.Measurable;
import org.strykeforce.telemetry.measurable.Measure;

@ExtendWith(MockitoExtension.class)
class RobotInventoryTest {

  @Mock private Measurable itemOne;
  @Mock private Measurable itemTwo;
  private Collection<Measurable> measurables;

  @BeforeEach
  void setUp() {
    measurables = List.of(itemOne, itemTwo);
  }

  @Test
  void classNameForMeasurableType() throws IOException, JSONException {
    Measurable m0 = new TestMeasurable(0);
    Measurable m1 = new TestMeasurable(1);
    var measurables = List.of(m0, m1);
    var inventory = new RobotInventory(measurables);
    var buffer = new Buffer();
    inventory.writeInventory(buffer);
    String expected = ResourceHelper.getString("/inventory-classname-type.json");
    JSONAssert.assertEquals(expected, buffer.readUtf8(), false);
  }

  @Test
  void measurableInterfaceDefaultMethods() {
    Measurable m0 = new TestMeasurable(0);
    Measurable m1 = new TestMeasurable(1);

    assertThat(m0.getType())
        .isEqualTo("org.strykeforce.telemetry.RobotInventoryTest$TestMeasurable");

    assertThat(m0.compareTo(m0)).isEqualTo(0);
    assertThat(m0.compareTo(m1)).isLessThan(0);

    Measurable m =
        new Measurable() {
          @Override
          public int getDeviceId() {
            return 0;
          }

          @NotNull
          @Override
          public String getDescription() {
            return "Anonymous Test Measurable";
          }

          @NotNull
          @Override
          public Set<Measure> getMeasures() {
            return Collections.emptySet();
          }
        };

    assertThat(m.compareTo(m0)).isLessThan(0);
  }

  @Test
  void itemForIdea() {
    RobotInventory inventory = new RobotInventory(measurables);
    assertThat(inventory.measurableForId(0)).isSameAs(itemOne);
  }

  @Test
  void writeInventory() throws IOException, JSONException {
    when(itemOne.getMeasures())
        .thenReturn(
            Set.of(
                new Measure("POSITION", "POSITION", () -> 0),
                new Measure("VALUE", "VALUE description", () -> 0)));
    when(itemOne.getType()).thenReturn("one");
    when(itemTwo.getMeasures())
        .thenReturn(Set.of(new Measure("BUS_VOLTAGE", "BUS_VOLTAGE", () -> 0)));
    when(itemTwo.getType()).thenReturn("two");
    RobotInventory inventory = new RobotInventory(measurables);
    Buffer buffer = new Buffer();
    inventory.writeInventory(buffer);

    String actual = buffer.readString(Charset.defaultCharset());
    String golden = ResourceHelper.getString("/inventory.json");

    JSONAssert.assertEquals(golden, actual, false);
  }

  static class TestMeasurable implements Measurable {

    private final int id;

    public TestMeasurable(int id) {
      this.id = id;
    }

    @Override
    public int getDeviceId() {
      return id;
    }

    @NotNull
    @Override
    public String getDescription() {
      return "Test Measurable";
    }

    @NotNull
    @Override
    public Set<Measure> getMeasures() {
      return Set.of(new Measure("Measure 1", () -> 1.0), new Measure("Measure 2", () -> 2.0));
    }
  }
}
