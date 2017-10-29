package org.strykeforce.thirdcoast.telemetry.tct;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;

/**
 * LoggingConfigurator configures logback-classic by attaching a {@link ConsoleAppender} to the root
 * logger.
 *
 */
public class LoggingConfigurator extends ContextAwareBase implements Configurator {

  @Override
  public void configure(LoggerContext lc) {
    addInfo("Setting up robot logging configuration.");

    FileAppender<ILoggingEvent> fa = new FileAppender<>();
    fa.setFile("tct.log");
    fa.setAppend(true);
    ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<>();
    ca.setContext(lc);
    ca.setName("console");
    fa.setContext(lc);
    fa.setName("logfile");
    LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
    encoder.setContext(lc);

    PatternLayout layout = new PatternLayout();
    layout.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");

    layout.setContext(lc);
    layout.start();
    encoder.setLayout(layout);

    ca.setEncoder(encoder);
//    ca.start();
    fa.setEncoder(encoder);
    fa.start();

    Logger rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
//    rootLogger.addAppender(ca);
    rootLogger.addAppender(fa);
    rootLogger.setLevel(Level.DEBUG);
  }
}
