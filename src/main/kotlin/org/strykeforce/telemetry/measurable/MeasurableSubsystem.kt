package org.strykeforce.telemetry.measurable

import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.strykeforce.telemetry.Registrable
import org.strykeforce.telemetry.TelemetryService

/**
 * A [Subsystem] that can send telemetry to the Third Coast grapher.
 *
 * To implement, override the [Measurable.measures] method, for example:
 *
 * <code><pre>
 * @Override
 * public Set<Measure> getMeasures() {
 *     return Set.of(
 *         new Measure("Gyro Rotation2d (deg)", () -> swerveDrive.getHeading().getDegrees()),
 *         new Measure("Gyro Angle (deg)", swerveDrive::getGyroAngle),
 *         new Measure("Odometry X", () -> swerveDrive.getPoseMeters().getX()),
 *         new Measure("Odometry Y", () -> swerveDrive.getPoseMeters().getY()),
 *     );
 * }</pre></code>
 *
 * The robot will call [registerWith] during initialization, default behavior is to register this
 * subsystem with the [TelemetryService]. If you wish to additionally register internal classes with
 * the telemetry service, override the [registerWith] method, for example:
 *
 * <code><pre>
 * @Override
 * public void registerWith(TelemetryService: telemetryService) {
 *     super.registerWith(telemetryService);
 *     telemetryService.register(talon);
 * }</pre></code>
 */
abstract class MeasurableSubsystem() : SubsystemBase(), Measurable, Registrable {

    override val description = name

    val subsystemNumber: Int
        get() = name.hashCode()

    override val deviceId: Int
        get() = subsystemNumber

    override fun compareTo(other: Measurable): Int {
        val result = type.compareTo(other.type)
        return if (result != 0) result else deviceId.compareTo(other.deviceId)
    }

    override fun registerWith(telemetryService: TelemetryService) = telemetryService.register(this)
}