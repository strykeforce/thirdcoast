package org.strykeforce.thirdcoast.telemetry.tct.talon.config.out.di;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

/**
 * Qualifier for Talon lim lim and ramp rates config menu.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface OutputMenu {

}
