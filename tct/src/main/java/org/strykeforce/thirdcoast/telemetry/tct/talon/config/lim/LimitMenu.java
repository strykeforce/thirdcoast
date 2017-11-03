package org.strykeforce.thirdcoast.telemetry.tct.talon.config.lim;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

/**
 * Qualifier for Talon lim switch config menu.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface LimitMenu {

}
