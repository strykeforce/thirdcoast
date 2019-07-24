package org.strykeforce.thirdcoast.healthcheck

import kotlinx.html.TagConsumer

fun <T : Comparable<T>> ClosedRange<T>.statusOf(value: T): String = if (this.contains(value)) "pass" else "fail"

/**
 * Represents a test.
 *
 * @property name name of the test to appear in HTML report.
 */
interface Test {
    var name: String

    /**
     * Executes test and logs measured data.
     */
    fun execute()

    /**
     * Determines if the test is finished.
     *
     * @return true if the test finishes.
     */
    fun isFinished(): Boolean

    /**
     * Generates an HTML report for the [Test].
     *
     * @param tagConsumer the tagConsumer used to generate the HTLML report.
     */
    fun report(tagConsumer: TagConsumer<Appendable>)
}
