package org.strykeforce.trapper

import edu.wpi.first.wpilibj2.command.SubsystemBase

class TrapperSubsystem @JvmOverloads constructor(
    val baseUrl: String,
    @get:JvmName("isEnabled") var enabled: Boolean = true
) : SubsystemBase() {

    private val session: Session = if (enabled) OkHttpSession(baseUrl) else DummySession()
    var activity: Activity = Activity()
    var action: Action = Action()
    var isFinished = !enabled

    fun <T : Postable> post(postable: T) = if (enabled) {
        isFinished = false
        when (val posted = session.post(postable)) {
            is Activity -> activity = posted
            is Action -> action = posted
        }
        isFinished = true
    } else {
        isFinished = true
    }

    fun <T : Postable> postAsync(postable: T) = if (enabled) {
        isFinished = false
        session.postAsync(postable) {
            when (it) {
                is Activity -> activity = it
                is Action -> action = it
            }
            isFinished = true
        }
    } else {
        isFinished = true
    }

    fun post(traces: List<Trace>) = if (enabled) session.post(traces) else Unit

}