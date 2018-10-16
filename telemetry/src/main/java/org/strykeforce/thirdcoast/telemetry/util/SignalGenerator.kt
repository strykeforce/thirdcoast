package org.strykeforce.thirdcoast.telemetry.util

private const val TICKS_PER_SECOND = 1e9

/** Generate a signal used for simulation.  */
abstract class SignalGenerator internal constructor(
  protected val frequency: Double,
  protected val phase: Double,
  protected val amplitude: Double,
  protected val offset: Double,
  protected val invert: Double
) {
  private var startTime = System.nanoTime()
  private var timeForLastValue: Double = 0.toDouble()

  val value: Double
    get() {
      timeForLastValue = (System.nanoTime() - startTime).toDouble() / TICKS_PER_SECOND
      return getValue(timeForLastValue)
    }

  private val isInverted: Boolean
    get() = invert == -1.0

  internal abstract fun getValue(time: Double): Double

  fun reset() {
    startTime = System.nanoTime()
  }

  override fun toString(): String {
    return String.format(
      "%.2f hz with amplitude %.2f, phase %.2f, offset %.2f%s",
      frequency, amplitude, phase, offset, if (isInverted) ", inverted" else ""
    )
  }

  /** Available signal types.  */
  enum class SignalType {
    SINE,
    SQUARE,
    TRIANGLE,
    SAWTOOTH
  }

  /** Builder for [SignalGenerator].  */
  class Builder(private val type: SignalType) {
    private var frequency = 1.0
    private var phase = 0.0
    private var amplitude = 1.0
    private var offset = 0.0
    private var invert = 1.0

    fun frequency(`val`: Double): Builder {
      frequency = `val`
      return this
    }

    fun phase(`val`: Double): Builder {
      phase = `val`
      return this
    }

    fun amplitude(`val`: Double): Builder {
      amplitude = `val`
      return this
    }

    fun offset(`val`: Double): Builder {
      offset = `val`
      return this
    }

    fun invert(`val`: Boolean): Builder {
      invert = if (`val`) -1.0 else 1.0
      return this
    }

    fun build(): SignalGenerator = when (type) {
      SignalGenerator.SignalType.SINE -> SineSignalGenerator(
        frequency,
        phase,
        amplitude,
        offset,
        invert
      )
      SignalGenerator.SignalType.SQUARE -> SquareSignalGenerator(
        frequency,
        phase,
        amplitude,
        offset,
        invert
      )
      SignalGenerator.SignalType.TRIANGLE -> TriangleSignalGenerator(
        frequency,
        phase,
        amplitude,
        offset,
        invert
      )
      SignalGenerator.SignalType.SAWTOOTH -> SawtoothSignalGenerator(
        frequency,
        phase,
        amplitude,
        offset,
        invert
      )
    }
  }

}
