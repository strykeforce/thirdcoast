package org.strykeforce.thirdcoast.telemetry.tct.di;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

/**
 * Qualifier for top-level menu.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface MainMenu { }
