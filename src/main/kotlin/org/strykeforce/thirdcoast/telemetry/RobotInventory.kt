package org.strykeforce.thirdcoast.telemetry

import org.strykeforce.thirdcoast.telemetry.item.Measurable

/** Default implementation of [Inventory] for a robot.  */
class RobotInventory(items: Collection<Measurable>) : AbstractInventory(items)
