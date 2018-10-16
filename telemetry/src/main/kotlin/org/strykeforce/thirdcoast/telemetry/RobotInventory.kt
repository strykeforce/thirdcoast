package org.strykeforce.thirdcoast.telemetry

import org.strykeforce.thirdcoast.telemetry.item.Item

/** Default implementation of [Inventory] for a robot.  */
class RobotInventory(items: Collection<Item>) : AbstractInventory(items)
