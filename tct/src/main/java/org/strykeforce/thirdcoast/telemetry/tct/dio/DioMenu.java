package org.strykeforce.thirdcoast.telemetry.tct.dio;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

/**
 * Qualifier for DIO menu.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
@interface DioMenu {

}
