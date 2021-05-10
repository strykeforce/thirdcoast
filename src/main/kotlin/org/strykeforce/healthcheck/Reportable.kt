package org.strykeforce.healthcheck

import kotlinx.html.TagConsumer
import kotlinx.html.table
import java.lang.Appendable

interface Reportable{
    fun reportTable(tagConsumer: TagConsumer<Appendable>){
        tagConsumer.table{
            reportHeader(tagConsumer)
            reportRows(tagConsumer)
        }
    }

    fun reportHeader(tagConsumer: TagConsumer<Appendable>)
    fun reportRows(tagConsumer: TagConsumer<Appendable>)
}