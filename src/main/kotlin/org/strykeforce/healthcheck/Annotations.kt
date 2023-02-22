package org.strykeforce.healthcheck

import com.ctre.phoenix.motorcontrol.can.BaseTalon

@Target(AnnotationTarget.FIELD)
annotation class HealthCheck(val order: Int = 0)

@Target(AnnotationTarget.FIELD)
annotation class Position(val percentOutput: DoubleArray, val encoderChange: Int)

@Target(AnnotationTarget.FIELD)
annotation class Timed(val percentOutput: DoubleArray, val duration: Double = 5.0)

@Target(AnnotationTarget.FIELD)
annotation class Follow(val leader: Int)

@Target(AnnotationTarget.FIELD)
annotation class Limits(val value: DoubleArray)

@Target(AnnotationTarget.FIELD)
annotation class LimitsSource(val value: String)

@Target(AnnotationTarget.FUNCTION)
annotation class BeforeHealthCheck(val order: Int = 0)

@Target(AnnotationTarget.FUNCTION)
annotation class AfterHealthCheck(val order: Int = 0)

/*
// will implement these later if needed

@Target(AnnotationTarget.FUNCTION)
annotation class BeforeEachHealthCheck

@Target(AnnotationTarget.FUNCTION)
annotation class AfterEachHealthCheck
*/
