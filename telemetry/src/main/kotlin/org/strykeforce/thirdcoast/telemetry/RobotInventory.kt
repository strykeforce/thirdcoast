package org.strykeforce.thirdcoast.telemetry

import org.strykeforce.thirdcoast.telemetry.graphable.Graphable

/** Default implementation of [Inventory] for a robot.  */
class RobotInventory(graphables: Collection<Graphable>) : AbstractInventory(graphables)
