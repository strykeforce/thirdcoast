package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * Qualifier for Talon config menu.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface TalonConfigMenu {

}
