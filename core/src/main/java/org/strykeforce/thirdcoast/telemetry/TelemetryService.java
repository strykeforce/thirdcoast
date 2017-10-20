package org.strykeforce.thirdcoast.telemetry;

import com.ctre.CANTalon;
import com.ctre.CANTalon.StatusFrameRate;
import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.telemetry.grapher.item.Item;
import org.strykeforce.thirdcoast.telemetry.grapher.item.TalonItem;

/**
 * The Telemetry service registers {@link Item} instances for data collection and controls the
 * starting and stopping of the service. When active, the services listens for incoming control
 * messages via a HTTP REST service and sends data over UDP.
 */
@Singleton
public class TelemetryService {

  TelemetryController telemetryController;
  Collection<Item> items = new ArrayList<>(16);

  @Inject
  public TelemetryService() {
  }

  /**
   * Start the Telemetry service and listen for client connections.
   */
  public void start() {
    TelemetryComponent component = DaggerTelemetryComponent.builder().items(items).build();
    telemetryController = component.telemetryController();
    telemetryController.start();

  }

  /**
   * Stop the Telemetry service.
   */
  public void stop() {
    telemetryController.shutdown();
  }

  /**
   * Register a Talon for telemetry sending and set CAN bus frame rates to default values.
   *
   * <ul>
   *   <li>General <b>10ms</b>: error, output duty cycle, limit switches, faults, mode</li>
   *   <li>Feedback <b>20ms</b>: selected encoder pos/vel, current, sticky faults, brake neutral state,
   *   motion control profile select</li>
   *   <li>Quad Encoder <b>100ms</b>: pos/vel, Index rising edge count, A/B/Index pin state</li>
   *   <li>Pulse Width <b>100ms</b>: assume abs encoder pos</li>
   *   <li>Analog In/Temp/Bus Voltage <b>100ms</b>: analog pos/vel, temp, bus voltage</li>
   * </ul>
   *
   * @param talon the CANTalon to register for data collection
   */
  public void register(CANTalon talon) {
    register(new TalonItem(talon));
    talon.setStatusFrameRateMs(StatusFrameRate.General, 10);
    talon.setStatusFrameRateMs(StatusFrameRate.Feedback, 20);
    talon.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 100);
    talon.setStatusFrameRateMs(StatusFrameRate.PulseWidth, 100);
    talon.setStatusFrameRateMs(StatusFrameRate.AnalogTempVbat, 100);

  }

  /**
   * Registers an Item for telemetry sending.
   *
   * @param item the Item to register for data collection
   */
  public void register(Item item) {
    items.add(item);
  }

  /**
   * Register a collection for telemetry sending.
   *
   * @param collection the collection of Items to register for data collection
   */
  public void registerAll(Collection<Item> collection) {
    items.addAll(collection);
  }

}
