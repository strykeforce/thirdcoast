package org.strykeforce.thirdcoast.trapper

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

internal fun actionDefaultName() = "Action ${LocalDateTime.now()}"

@JsonClass(generateAdapter = true)
data class Action(
    val activity: Activity? = null,
    val name: String = actionDefaultName(),
    val meta: MutableMap<String, Any> = mutableMapOf(),
    val measures: List<String> = emptyList(),
    val data: MutableList<Double> = mutableListOf(),
    val traceMeasures: List<String> = emptyList(),
    val traceData: MutableList<List<Double>> = mutableListOf()
)
