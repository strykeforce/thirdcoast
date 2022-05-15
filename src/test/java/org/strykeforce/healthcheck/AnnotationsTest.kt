package org.strykeforce.healthcheck

import org.junit.jupiter.api.Test

class AnnotationsTest {
    @HealthCheck
    val fieldOne = "One"

    @HealthCheck
    @Timed(percentOutput = doubleArrayOf(0.5), duration = 1.0)
    val fieldTwo = "Two"

    @HealthCheck
    @Position(percentOutput = doubleArrayOf(0.5), encoderChange = 1000)
    val fieldThree = "Three"

    @HealthCheck
    @Follow
    val fieldFour = "Four"

    @HealthCheck
    @Timed(percentOutput = doubleArrayOf(0.5), duration = 1.0)
    @Position(percentOutput = doubleArrayOf(0.5), encoderChange = 1000)
    val fieldFive = "Five"


}
