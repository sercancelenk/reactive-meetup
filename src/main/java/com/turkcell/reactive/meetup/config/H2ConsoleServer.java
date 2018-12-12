package com.turkcell.reactive.meetup.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This class for h2console on webflux.
 */

@Component
@Slf4j
public class H2ConsoleServer {
    private org.h2.tools.Server webServer;

    private org.h2.tools.Server server;

    @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
    public void start() throws java.sql.SQLException {
        log.info("*********");
        this.webServer = org.h2.tools.Server.createWebServer("-webPort", "9098", "-tcpAllowOthers").start();
        this.server = org.h2.tools.Server.createTcpServer("-tcpPort", "9099", "-tcpAllowOthers").start();
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {
        this.webServer.stop();
        this.server.stop();
    }
}
