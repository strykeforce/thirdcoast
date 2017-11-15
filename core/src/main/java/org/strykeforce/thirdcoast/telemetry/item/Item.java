package org.strykeforce.thirdcoast.telemetry.item;

import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;

/**
 * An item that can be graphed. These are used as {@code Set} elements and implementing classes
 * should implement an identity-based version of equals and hashCode.
 *
 * The abstract base class implementing {@code Item} implements {@code Comparable} by comparing the
 * results returned by {@code Item#id()}.
 */
public interface Item extends Comparable<Item> {

  /**
   * Returns the underlying device id, for example, CAN bus address or PWM port.
   *
   * @return the device id.
   */
  int id();

  /**
   * A {@code String}  representing the underlying device type.
   *
   * @return the type.
   */
  String type();

  /**
   * The description of this item.
   *
   * @return the description.
   */
  String description();

  /**
   * {@code Set} of {@code Measure} items applicable to this item type.
   *
   * @return Set of Measure enumeration values.
   */
  Set<Measure> measures();

  /**
   * Suppliers that implement the measures.
   *
   * @param measure the Measure to supply.
   * @return the supplier that gives the current measurement.
   */
  DoubleSupplier measurementFor(Measure measure);

  /**
   * Provides a detailed JSON representation of the underlying device.
   *
   * @param writer the writer to write to.
   * @throws IOException if an IO error occurs.
   */
  void toJson(JsonWriter writer) throws IOException;
}
