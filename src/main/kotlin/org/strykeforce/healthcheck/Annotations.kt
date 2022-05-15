package org.strykeforce.healthcheck

@Target(AnnotationTarget.FIELD)
annotation class HealthCheck

@Target(AnnotationTarget.FIELD)
annotation class Position(val percentOutput: DoubleArray, val encoderChange: Int)

@Target(AnnotationTarget.FIELD)
annotation class Timed(val percentOutput: DoubleArray, val duration: Double)

@Target(AnnotationTarget.FIELD)
annotation class Follow

@Target(AnnotationTarget.FIELD)
annotation class Limits(val value: DoubleArray)

@Target(AnnotationTarget.FIELD)
annotation class LimitsSource(val value: String)

@Target(AnnotationTarget.FUNCTION)
annotation class BeforeHealthCheck

@Target(AnnotationTarget.FUNCTION)
annotation class AfterHealthCheck

@Target(AnnotationTarget.FUNCTION)
annotation class BeforeEachHealthCheck

@Target(AnnotationTarget.FUNCTION)
annotation class AfterEachHealthCheck

