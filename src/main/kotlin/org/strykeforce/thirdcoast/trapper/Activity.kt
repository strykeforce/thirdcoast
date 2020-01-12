package org.strykeforce.thirdcoast.trapper

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime
import java.util.*

@JsonClass(generateAdapter = true)
data class Activity(
    val id: UUID = UUID.randomUUID(),
    val name: String = "Activity ${LocalDateTime.now()}",
    val meta: MutableMap<String, Any> = mutableMapOf(),
    val measures: List<String> = emptyList(),
    val data: MutableList<Double> = mutableListOf()
) {

    fun newAction(
        name: String = actionDefaultName(),
        measures: List<String> = emptyList(),
        traceMeasures: List<String> = emptyList()
    ): Action =
        Action(this, name, measures = measures, traceMeasures = traceMeasures)
}