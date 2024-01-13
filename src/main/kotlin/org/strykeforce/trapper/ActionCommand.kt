package org.strykeforce.trapper

import edu.wpi.first.wpilibj2.command.Command


abstract class ActionCommand @JvmOverloads constructor(
    var action: Action = Action(),
    val trapperSubsystem: TrapperSubsystem
) : Command() {

    constructor(name: String, trapperSubsystem: TrapperSubsystem) : this(
        Action(name),
        trapperSubsystem
    )

    init {
        addRequirements(trapperSubsystem)
    }

    val traces = mutableListOf<Trace>()

    abstract val trace: Trace

    override fun execute() = if (trapperSubsystem.enabled) traces += trace else Unit


    fun postWith(session: OkHttpSession) =
        if (trapperSubsystem.enabled) session.post(traces) else Unit
}