package org.strykeforce.thirdcoast.telemetry.tct.talon.di;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

/**
 * Qualifier for Talon menu.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface TalonMenu {

}
