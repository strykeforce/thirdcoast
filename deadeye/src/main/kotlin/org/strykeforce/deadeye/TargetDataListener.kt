package org.strykeforce.deadeye

@FunctionalInterface
interface TargetDataListener {
  fun onTargetData(data: TargetData)
}
