package org.strykeforce.thirdcoast.telemetry.tct.talon.config.voltage;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * Qualifier for Talon voltage limit and ramp rates config menu.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface VoltageMenu {

}
