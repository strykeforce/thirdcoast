package org.strykeforce.thirdcoast.telemetry.app.sim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private final SignalGenerator sine1;
    private final SignalGenerator sine2; // phase shifted 0.25, amplitude 100.0
    private final SignalGenerator sine3; // phase shifted 0.25, offset 10.0, inverted
    private final SignalGenerator square1;
    private final SignalGenerator square2; // phase shifted 0.3333, amplitude 100.0
    private final SignalGenerator triangle1;
    private final SignalGenerator triangle2; // phase shifted 0.3333, amplitude 100.0
    private final SignalGenerator triangle3; // phase shifted 0.25, amplitude 5.0, inverted
    private final SignalGenerator sawtooth1;
    private final SignalGenerator sawtooth2; // phase shifted 0.3333, amplitude 100.0
    private final SignalGenerator sawtooth3; // phase shifted -0.5, amplitude 1000.0, inverted

    public SimulatedItem(int id) {
      this.id = id;
      sine1 = new SignalGenerator.Builder(SignalType.SINE).frequency(id).build();
      sine2 = new SignalGenerator.Builder(SignalType.SINE).frequency(id).amplitude(100.0)
          .phase(0.25)
          .build();
      sine3 = new SignalGenerator.Builder(SignalType.SINE).frequency(id).phase(0.25).offset(3.0)
          .invert(true).build();
      square1 = new SignalGenerator.Builder(SignalType.SQUARE).frequency(id).build();
      square2 = new SignalGenerator.Builder(SignalType.SQUARE).frequency(id).amplitude(0.5)
          .phase(1.0 / 3.0).offset(2).build();
      triangle1 = new SignalGenerator.Builder(SignalType.TRIANGLE).frequency(id)
          .amplitude(1.0 / 3.0).build();
      triangle2 = new SignalGenerator.Builder(SignalType.TRIANGLE).frequency(id).amplitude(10.0)
          .phase(1.0 / 3.0).build();
      triangle3 = new SignalGenerator.Builder(SignalType.TRIANGLE).frequency(id).amplitude(50.0)
          .phase(0.25).invert(true).build();
      sawtooth1 = new SignalGenerator.Builder(SignalType.SAWTOOTH).frequency(id).build();
      sawtooth2 = new SignalGenerator.Builder(SignalType.SAWTOOTH).frequency(id).amplitude(10.0)
          .phase(1.0 / 3.0).build();
      sawtooth3 = new SignalGenerator.Builder(SignalType.SAWTOOTH).frequency(id).amplitude(1000.0)
          .phase(-0.5).invert(true).build();
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
      String type = "";
      String parms = "";
      DoubleSupplier result = () -> Double.NaN;
      switch (measure) {
        case SETPOINT: // 0
          type = "sine";
          parms = "amplitude 1, phase 0, offset 0";
          result = () -> sine1.getValue();
          break;
        case OUTPUT_CURRENT: // 1
          type = "sine";
          parms = "amplitude 100, phase 0, offset 0";
          result = () -> sine2.getValue();
          break;
        case OUTPUT_VOLTAGE: // 2
          type = "sine";
          parms = "amplitude 1, phase 0.25, offset 3, inverted";
          result = () -> sine3.getValue();
          break;
        case ENCODER_POSITION: // 3
          type = "square";
          parms = "amplitude 1, phase 0, offset 0";
          result = () -> square1.getValue();
          break;
        case ENCODER_VELOCITY: // 4
          type = "triangle";
          parms = "amplitude 0.3, phase 0, offset 0";
          result = () -> triangle1.getValue();
          break;
        case ABSOLUTE_ENCODER_POSITION: // 5
          type = "triangle";
          parms = "amplitude 10, phase 0.3, offset 0";
          result = () -> triangle2.getValue();
          break;
        case CONTROL_LOOP_ERROR: // 6
          type = "sawtooth";
          parms = "amplitude 1, phase 0, offset 0";
          result = () -> sawtooth1.getValue();
          break;
        case INTEGRATOR_ACCUMULATOR: // 7
          type = "sawtooth";
          parms = "amplitude 10, phase 0.3, offset 0";
          result = () -> sawtooth2.getValue();
          break;
        case BUS_VOLTAGE: // 8
          type = "triangle";
          parms = "amplitude 50, phase 0.25, offset 0, inverted";
          result = () -> triangle3.getValue();
          break;
        case FORWARD_HARD_LIMIT_CLOSED: // 9
          type = "sawtooth";
          parms = "amplitude 1000, phase -0.5, offset 0, inverted";
          result = () -> sawtooth3.getValue();
          break;
        case REVERSE_HARD_LIMIT_CLOSED: // 10
          type = "square";
          parms = "amplitude 1, phase 0, offset 0";
          result = () -> square1.getValue();
          break;
        case FORWARD_SOFT_LIMIT_OK: // 11
          type = "square";
          parms = "amplitude 0.5, phase 0.3, offset 2";
          result = () -> square2.getValue();
          break;
        case REVERSE_SOFT_LIMIT_OK: // 12
          type = "constant";
          parms = "amplitude 9999";
          result = () -> 9999.0;
          break;
      }
      System.out.printf("  add id %d: %s at freq %d hz with %s%n", id, type, id, parms);
      return result;
    }

  }
}
