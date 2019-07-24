package org.strykeforce.thirdcoast.healthcheck

import kotlinx.html.TagConsumer
import kotlinx.html.table

/**
 * Represents a [Test] object capable of generating an HTML report.
 */
interface Reportable {

    /**
     * Reports the table for the test
     *
     * @param tagConsumer the tagConsumer used to generate the HTLML report.
     */
    fun reportTable(tagConsumer: TagConsumer<Appendable>) {
        tagConsumer.table {
            reportHeader(tagConsumer)
            reportRows(tagConsumer)
        }
    }

    /**
     * Reports the header for the test
     *
     * @param tagConsumer the tagConsumer used to generate the HTLML report.
     */
    fun reportHeader(tagConsumer: TagConsumer<Appendable>)

    /**
     * Reports the rows for the test
     *
     * @param tagConsumer the tagConsumer used to generate the HTLML report.
     */
    fun reportRows(tagConsumer: TagConsumer<Appendable>)
}

