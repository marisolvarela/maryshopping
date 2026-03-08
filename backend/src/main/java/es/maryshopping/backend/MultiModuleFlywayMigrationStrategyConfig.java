package es.maryshopping.backend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Multi-module Flyway migration strategy configuration with Railway-Oriented Programming patterns.
 * 
 * This configuration provides database migration strategy using functional
 * dependency injection and structured logging. All dependencies are guaranteed non-null.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MultiModuleFlywayMigrationStrategyConfig {
    private static final String FLYWAY_HISTORY_TABLE_NAME = "flyway_schema_history";
    private static final String SCHEMAS = "schemas";
    private static final String LOCATIONS = "locations";

    private final FlywayConfig flywayConfig;

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            log.debug("Starting multi-module Flyway migration strategy");
            DataSource dataSource = flyway.getConfiguration().getDataSource();

            log.debug("Migrating customers module");
            migrateModule(dataSource, flywayConfig.getCustomers());

            log.debug("Migrating orders module");
            migrateModule(dataSource, flywayConfig.getOrders());

            log.debug("Migrating catalog module");
            migrateModule(dataSource, flywayConfig.getCatalog());

            log.debug("Migrating reports module");
            migrateModule(dataSource, flywayConfig.getReports());

            log.debug("Completed multi-module Flyway migration strategy");
        };
    }

    private void migrateModule(DataSource dataSource, FlywayConfig.ModuleConfig moduleConfig) {
        java.util.Map<String, String[]> configMap = moduleConfig.toMap();
        String[] schemas = configMap.get(SCHEMAS);
        String[] locations = configMap.get(LOCATIONS);

        if (schemas == null || locations == null || schemas.length == 0 || locations.length == 0) {
            log.warn("Skipping migration: schemas or locations not configured");
            return;
        }

        Flyway module = Flyway.configure()
                .table(FLYWAY_HISTORY_TABLE_NAME)
                .createSchemas(true)
                .baselineOnMigrate(true)
                .schemas(schemas)
                .locations(locations)
                .dataSource(dataSource)
                .load();
        module.migrate();
    }
}
