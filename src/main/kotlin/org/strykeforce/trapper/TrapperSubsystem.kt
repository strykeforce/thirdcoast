package org.strykeforce.trapper

import edu.wpi.first.wpilibj2.command.SubsystemBase

class TrapperSubsystem(val baseUrl: String) : SubsystemBase() {

    private val session = Session(baseUrl)
    var activity: Activity = Activity()
    var action: Action = Action()
    var isFinished = false

    fun <T : Postable> post(postable: T) {
        isFinished = false
        when (val posted = session.post(postable)) {
            is Activity -> activity = posted
            is Action -> action = posted
        }
        isFinished = true
    }

    fun <T : Postable> postAsync(postable: T) {
        isFinished = false
        session.postAsync(postable) {
            when (it) {
                is Activity -> activity = it
                is Action -> action = it
            }
            isFinished = true
        }
    }

    fun post(traces: List<Trace>) = session.post(traces)

}