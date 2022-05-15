package org.strykeforce.healthcheck.old

import kotlinx.html.TagConsumer
import java.lang.Appendable

fun<T : Comparable<T>> ClosedRange<T>.statusOf(value: T): String = if(this.contains(value)) "pass" else "fail"

interface Test{
    var name: String
    fun execute()
    fun isFinished(): Boolean
    fun report(tagConsumer: TagConsumer<Appendable>)
}