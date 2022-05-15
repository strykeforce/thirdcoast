package org.strykeforce.healthcheck

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DataTest {

    @Test
    fun `get limit for @Limits`() {
        var array = doubleArrayOf()
        var limits = array.limitsFor(0)
        assertEquals(DiagnosticLimits(), limits)

        array = doubleArrayOf(1.0, 2.0, 3.0)
        limits = array.limitsFor(0)
        assertEquals(DiagnosticLimits(), limits)

        array = doubleArrayOf(1.0, 2.0, 3.0, 4.0)
        limits = array.limitsFor(0)
        assertEquals(DiagnosticLimits(1.0, 2.0, 3.0, 4.0), limits)

        array = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0)
        limits = array.limitsFor(0)
        assertEquals(DiagnosticLimits(1.0, 2.0, 3.0, 4.0), limits)
        limits = array.limitsFor(1)
        assertEquals(DiagnosticLimits(), limits)
    }
}