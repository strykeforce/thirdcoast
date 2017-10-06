package org.strykeforce.thirdcoast.telemetry.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An inventory message resets the robot's telemetry service and requests list of telemetry-enabled
 * components.
 */
public class InventoryMessage extends AbstractMessage {

  public static class Item {
    public int id;
    public String type;
    public String description;

    public Item(int id, String type, String description) {
      this.id = id;
      this.type = "talon";
      this.description = description;
    }
  }

  public static class Measure {
    public int id;
    public String description;
    public String units;

    public Measure(int id, String description, String units) {
      this.id = id;
      this.description = description;
      this.units = units;
    }
  }

  List<Item> items = new ArrayList<>(TALON_MAX);
  Map<String, List<Measure>> measures = new HashMap<>();


  public InventoryMessage() {
    super("inventory");
    items.add(new Item(0, "talon", "Azimuth 0 (0)"));
    items.add(new Item(10, "talon", "Drive 0 (10)"));
//    items.add(new Item(1, "talon", "Azimuth 1 (1)"));
//    items.add(new Item(11, "talon", "Drive 1 (11)"));
//    items.add(new Item(2, "talon", "Azimuth 2 (2)"));
//    items.add(new Item(12, "talon", "Drive 2 (12)"));
//    items.add(new Item(3, "talon", "Azimuth 3 (3)"));
//    items.add(new Item(13, "talon", "Drive 3 (13)"));

    int id = 0;
    List<Measure> talonMeasures = new ArrayList<>();
    measures.put("talon", talonMeasures);
    talonMeasures.add(new Measure(id++, "Setpoint", "RPM"));
    talonMeasures.add(new Measure(id++, "Output Current", "amps"));
    talonMeasures.add(new Measure(id++, "Output Voltage", "volts"));
    talonMeasures.add(new Measure(id++, "Encoder Position", "revolutions"));
    talonMeasures.add(new Measure(id++, "Encoder Velocity", "RPM"));
    talonMeasures.add(new Measure(id++, "Absolute Encoder Position", "revolutions"));
    talonMeasures.add(new Measure(id++, "Control Loop Error", "RPM"));
    talonMeasures.add(new Measure(id++, "Integrator Accumulator", "none"));
    talonMeasures.add(new Measure(id++, "Bus Voltage", "volts"));
    talonMeasures.add(new Measure(id++, "Forward Hard Limit Closed", "boolean"));
    talonMeasures.add(new Measure(id++, "Reverse Hard Limit Closed", "boolean"));
    talonMeasures.add(new Measure(id++, "Forward Soft Limit OK", "boolean"));
    talonMeasures.add(new Measure(id++, "Reverse Soft Limit OK", "boolean"));
  }
}
