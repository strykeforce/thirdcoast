package org.strykeforce.thirdcoast.telemetry.tct.talon.config.enc.di;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

/**
 * Qualifier for Talon encoder config menu.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface EncoderMenu {

}
