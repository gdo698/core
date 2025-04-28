package com.core.runner;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Bean
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource(datasourceUrl, datasourceUsername, datasourcePassword)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .locations("classpath:sql")
                .validateOnMigrate(true)
                .cleanDisabled(false)
                .outOfOrder(true)
                .load();
    }
}