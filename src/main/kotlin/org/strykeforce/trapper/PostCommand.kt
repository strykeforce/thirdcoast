package org.strykeforce.trapper

import edu.wpi.first.wpilibj2.command.CommandBase
import java.util.function.Consumer

class PostCommand(
    val trapperSubsystem: TrapperSubsystem,
    val onInit: Runnable,
    val onEnd: Consumer<Boolean>
) : CommandBase() {

    init {
        addRequirements(trapperSubsystem)
    }

    override fun initialize() {
        onInit.run()
    }

    override fun isFinished() = trapperSubsystem.isFinished

    override fun end(interrupted: Boolean) {
        onEnd.accept(interrupted)
    }
}