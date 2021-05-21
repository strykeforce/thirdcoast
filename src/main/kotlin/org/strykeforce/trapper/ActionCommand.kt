package org.strykeforce.trapper

import edu.wpi.first.wpilibj2.command.CommandBase
import org.strykeforce.trapper.Action
import org.strykeforce.trapper.Session
import org.strykeforce.trapper.Trace

abstract class ActionCommand @JvmOverloads constructor(var action: Action = Action()) :
    CommandBase() {

    constructor(name: String) : this(Action(name))

    val traces = mutableListOf<Trace>()

    abstract val trace: Trace

    override fun execute() {
        traces += trace
    }

    fun postWith(session: Session) = session.post(traces)
}