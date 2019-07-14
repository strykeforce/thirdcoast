package org.strykeforce.thirdcoast.healthcheck

import kotlinx.html.TagConsumer
import kotlinx.html.table

interface Reportable {

    fun reportTable(tagConsumer: TagConsumer<Appendable>) {
        tagConsumer.table {
            reportHeader(tagConsumer)
            reportRows(tagConsumer)
        }
    }

    fun reportHeader(tagConsumer: TagConsumer<Appendable>)
    fun reportRows(tagConsumer: TagConsumer<Appendable>)
}

