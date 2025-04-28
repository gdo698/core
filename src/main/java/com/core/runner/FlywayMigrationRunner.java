package com.core.runner;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlywayMigrationRunner {

    private final Flyway flyway;

    @EventListener(ApplicationReadyEvent.class)
    public void migrate() {
        flyway.baseline();
        flyway.migrate();
    }
}