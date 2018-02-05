package org.team2767.thirdcoast;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;

/**
 * LoggingConfigurator configures logback-classic by attaching a {@link ConsoleAppender} to the root
 * logger.
 */
public class LoggingConfigurator extends ContextAwareBase implements Configurator {

  @Override
  public void configure(LoggerContext lc) {
    addInfo("Setting up robot logging configuration.");

    ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<>();
    ca.setContext(lc);
    ca.setName("console");
    LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
    encoder.setContext(lc);

    PatternLayout layout = new PatternLayout();
    layout.setPattern("%-23(%d{HH:mm:ss.SSS} [%thread]) %highlight(%-5level) %logger{32} - %msg%n");

    layout.setContext(lc);
    layout.start();
    encoder.setLayout(layout);

    ca.setEncoder(encoder);
    ca.start();

    Logger rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.addAppender(ca);
    rootLogger.setLevel(Level.TRACE);
  }
}
