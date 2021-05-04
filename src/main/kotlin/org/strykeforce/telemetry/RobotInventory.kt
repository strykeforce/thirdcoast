package org.strykeforce.telemetry

import org.strykeforce.telemetry.item.Measurable

/** Default implementation of [Inventory] for a robot.  */
class RobotInventory(items: Collection<Measurable>) : AbstractInventory(items)
