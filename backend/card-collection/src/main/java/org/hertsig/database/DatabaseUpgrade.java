package org.hertsig.database;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class DatabaseUpgrade {
    @Inject
    public DatabaseUpgrade(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations(DatabaseUpgrade.class.getPackage().getName());
        try {
            flyway.migrate();
        }
        catch (FlywayException e) {
            log.warn("Migration failed, attempting repair", e);
            flyway.repair();
            flyway.migrate();
        }
    }
}
