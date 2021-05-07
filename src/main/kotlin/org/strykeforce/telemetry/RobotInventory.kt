package org.strykeforce.telemetry

import org.strykeforce.telemetry.measurable.Measurable

/** Default implementation of [Inventory] for a robot.  */
class RobotInventory(measurableSet: Collection<Measurable>) : AbstractInventory(measurableSet)
