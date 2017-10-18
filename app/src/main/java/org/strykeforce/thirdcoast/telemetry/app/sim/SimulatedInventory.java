package org.strykeforce.thirdcoast.telemetry.app.sim;

import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.ABSOLUTE_ENCODER_POSITION;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.ANGLE;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.BUS_VOLTAGE;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.CONTROL_LOOP_ERROR;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.ENCODER_POSITION;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.ENCODER_VELOCITY;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.FORWARD_HARD_LIMIT_CLOSED;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.FORWARD_SOFT_LIMIT_OK;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.INTEGRATOR_ACCUMULATOR;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.OUTPUT_CURRENT;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.OUTPUT_VOLTAGE;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.POSITION;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.REVERSE_HARD_LIMIT_CLOSED;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.REVERSE_SOFT_LIMIT_OK;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.SETPOINT;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.VALUE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.AbstractInventory;
import org.strykeforce.thirdcoast.telemetry.grapher.Item;
import org.strykeforce.thirdcoast.telemetry.grapher.Item.Type;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;
import org.strykeforce.thirdcoast.telemetry.util.SignalGenerator;
import org.strykeforce.thirdcoast.telemetry.util.SignalGenerator.SignalType;

public class SimulatedInventory extends AbstractInventory {

  public SimulatedInventory(Collection<Item> items) {
    super(items);
  }

  public static SimulatedInventory create() {
    System.out.println("\nCreating simulated inventory...");
    List<Item> fakes = new ArrayList<>();
    final int[] ints = new Random().ints(0, 64).distinct().limit(8).toArray();
    for (int i : ints) {
      fakes.add(new SimulatedItem(i, Type.TALON));
    }
    fakes.add(new SimulatedItem(0, Type.SERVO));
    fakes.add(new SimulatedItem(0, Type.DIGITAL_INPUT));
    for (int i = 0; i < fakes.size(); i++) {
      System.out.printf("%2d: %s%n", i, fakes.get(i).description());
    }
    System.out.println("\nMeasure signals are...");

    System.out.printf("%-25s %s%n", SETPOINT.getDescription(), ": SINE inv even ids");
    System.out.printf("%-25s %s%n", OUTPUT_CURRENT.getDescription(), ": SINE, ampl 1000, ph 0.25");
    System.out.printf("%-25s %s%n", OUTPUT_VOLTAGE.getDescription(),
        ": SINE, ampl 2, ph -0.25, off 2/-2 even/odd id");
    System.out.printf("%-25s %s%n", ENCODER_POSITION.getDescription(), ": SAWTOOTH, inv odd ids");
    System.out.printf("%-25s %s%n", ENCODER_VELOCITY.getDescription(), ": SAWTOOTH, ampl 0.1");
    System.out.printf("%-25s %s%n", ABSOLUTE_ENCODER_POSITION.getDescription(),
        ": SAWTOOTH, off 2/-2 even/odd");
    System.out
        .printf("%-25s %s%n", CONTROL_LOOP_ERROR.getDescription(), ": TRIANGLE, inv even ids");
    System.out.printf("%-25s %s%n", INTEGRATOR_ACCUMULATOR.getDescription(),
        ": TRIANGLE, ampl 50 ph 0.25");
    System.out.printf("%-25s %s%n", BUS_VOLTAGE.getDescription(),
        ": TRIANGLE, ampl id ph 0.33 off id/-id even/odd");
    System.out.printf("%-25s %s%n", FORWARD_HARD_LIMIT_CLOSED.getDescription(), ": SQUARE");
    System.out.printf("%-25s %s%n", REVERSE_HARD_LIMIT_CLOSED.getDescription(), ": SQUARE, inv");
    System.out.printf("%-25s %s%n", FORWARD_SOFT_LIMIT_OK.getDescription(), ": SQUARE, ph 0.33");
    System.out
        .printf("%-25s %s%n", REVERSE_SOFT_LIMIT_OK.getDescription(), ": SQUARE, ph 0.33 inv");
    System.out.printf("%-25s %s%n", VALUE.getDescription(), ": SINE");
    System.out.printf("%-25s %s%n", POSITION.getDescription(), ": SAWTOOTH");
    System.out.printf("%-25s %s%n", ANGLE.getDescription(), ": TRIANGLE");
    return new SimulatedInventory(fakes);
  }

  public static class SimulatedItem implements Item {

    private final int id;
    private final Item.Type type;
    private final Map<Measure, SignalGenerator> sigs = new TreeMap<>();

    public SimulatedItem(int id, Item.Type type) {
      this.id = id;
      this.type = type;

      boolean even = id % 2 == 0;
      double freq = id % 5 + 1;

      SignalGenerator.Builder builder = new SignalGenerator.Builder(SignalType.SINE)
          .frequency(freq);
      sigs.put(VALUE, builder.build());
      sigs.put(SETPOINT, builder.invert(even).build()); // 0

      sigs.put(OUTPUT_CURRENT, builder.amplitude(1000).phase(0.25).build()); // 1

      sigs.put(OUTPUT_VOLTAGE,
          builder.amplitude(2).phase(-0.25).offset(even ? 2 : -2).build()); // 2

      builder = new SignalGenerator.Builder(SignalType.SAWTOOTH).frequency(freq);
      sigs.put(POSITION, builder.build());
      sigs.put(ENCODER_POSITION, builder.invert(!even).build()); // 3

      sigs.put(ENCODER_VELOCITY, builder.amplitude(0.1).build()); // 4

      sigs.put(ABSOLUTE_ENCODER_POSITION, builder.amplitude(1).offset(even ? 2 : -2).build()); // 5

      builder = new SignalGenerator.Builder(SignalType.TRIANGLE).frequency(freq);
      sigs.put(ANGLE, builder.build());
      sigs.put(CONTROL_LOOP_ERROR, builder.invert(even).build());

      sigs.put(INTEGRATOR_ACCUMULATOR, builder.amplitude(50).phase(0.25).build());

      sigs.put(BUS_VOLTAGE, builder.amplitude(id).phase(1 / 3f).offset(even ? id : -id).build());

      builder = new SignalGenerator.Builder(SignalType.SQUARE).frequency(freq);
      sigs.put(FORWARD_HARD_LIMIT_CLOSED, builder.build());

      sigs.put(REVERSE_HARD_LIMIT_CLOSED, builder.invert(true).build());

      sigs.put(FORWARD_SOFT_LIMIT_OK, builder.phase(1 / 3f).build());

      sigs.put(REVERSE_SOFT_LIMIT_OK, builder.phase(1 / 3f).invert(true).build());
    }

    @Override
    public int id() {
      return id;
    }

    @Override
    public Item.Type type() {
      return type;
    }

    @Override
    public String description() {
      String desc;
      switch (type) {
        case DIGITAL_INPUT:
          desc = "Digital Input " + id;
          break;
        case SERVO:
          desc = "Servo " + id;
          break;
        case TALON:
          desc = "Talon " + id;
          break;
        default:
          desc = "Unknown " + id;
      }
      return String.format("Fake %s at %d Hz", desc, id % 5 + 1);
    }

    @Override
    public DoubleSupplier measurementFor(Measure measure) {
      SignalGenerator signalGenerator = sigs.get(measure);
      System.out.println(signalGenerator);
      return signalGenerator::getValue;
    }

  }
}
