package org.strykeforce.thirdcoast.telemetry.app.sim;

import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.ABSOLUTE_ENCODER_POSITION;
import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.BUS_VOLTAGE;
import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.CONTROL_LOOP_ERROR;
import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.ENCODER_POSITION;
import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.ENCODER_VELOCITY;
import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.FORWARD_HARD_LIMIT_CLOSED;
import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.FORWARD_SOFT_LIMIT_OK;
import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.INTEGRATOR_ACCUMULATOR;
import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.OUTPUT_CURRENT;
import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.OUTPUT_VOLTAGE;
import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.REVERSE_HARD_LIMIT_CLOSED;
import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.REVERSE_SOFT_LIMIT_OK;
import static org.strykeforce.thirdcoast.telemetry.grapher.Item.Measure.SETPOINT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.AbstractInventory;
import org.strykeforce.thirdcoast.telemetry.grapher.Item;
import org.strykeforce.thirdcoast.telemetry.util.SignalGenerator;
import org.strykeforce.thirdcoast.telemetry.util.SignalGenerator.SignalType;

public class SimulatedInventory extends AbstractInventory {

  public SimulatedInventory(Collection<Item> items) {
    super(items);
  }

  public static SimulatedInventory create() {
    List<Item> fakes = new ArrayList<>();
    for (int i = 0; i < 63; i++) {
      fakes.add(new SimulatedItem(i));
    }
    return new SimulatedInventory(fakes);
  }

  public static class SimulatedItem implements Item {

    private final int id;
    private final Map<Measure, SignalGenerator> sigs = new TreeMap<>();

    public SimulatedItem(int id) {
      this.id = id;
      boolean even = id % 2 == 0;

      SignalGenerator.Builder builder = new SignalGenerator.Builder(SignalType.SINE).frequency(id);
      sigs.put(SETPOINT, builder.invert(even).build()); // 0

      sigs.put(OUTPUT_CURRENT, builder.amplitude(id * 100).phase(0.25).build()); // 1

      sigs.put(OUTPUT_VOLTAGE, builder.amplitude(2).phase(-0.25).offset(even ? 2 : -2).build()); // 2

      builder = new SignalGenerator.Builder(SignalType.SAWTOOTH).frequency(id);
      sigs.put(ENCODER_POSITION, builder.invert(!even).build()); // 3

      sigs.put(ENCODER_VELOCITY, builder.amplitude(id * 10).build()); // 4

      sigs.put(ABSOLUTE_ENCODER_POSITION, builder.amplitude(1).offset(even ? 2 : -2).build()); // 5

      builder = new SignalGenerator.Builder(SignalType.TRIANGLE).frequency(id);
      sigs.put(CONTROL_LOOP_ERROR, builder.invert(even).build());

      sigs.put(INTEGRATOR_ACCUMULATOR, builder.amplitude(50).phase(0.25).build());

      sigs.put(BUS_VOLTAGE, builder.amplitude(id).phase(1 / 3f).offset(even ? id : -id).build());

      builder = new SignalGenerator.Builder(SignalType.SQUARE).frequency(id);
      sigs.put(FORWARD_HARD_LIMIT_CLOSED, builder.build());

      sigs.put(REVERSE_HARD_LIMIT_CLOSED, builder.invert(true).build());

      sigs.put(FORWARD_SOFT_LIMIT_OK, builder.phase(1/3f).build());

      sigs.put(REVERSE_SOFT_LIMIT_OK, builder.phase(1/3f).invert(true).build());
    }

    @Override
    public int id() {
      return id;
    }

    @Override
    public String type() {
      return "talon";
    }

    @Override
    public String description() {
      return String.format("Fake Talon %d", id);
    }

    @Override
    public DoubleSupplier measurementFor(Measure measure) {
      SignalGenerator signalGenerator = sigs.get(measure);
      System.out.println(signalGenerator);
      return signalGenerator::getValue;
    }

  }
}
