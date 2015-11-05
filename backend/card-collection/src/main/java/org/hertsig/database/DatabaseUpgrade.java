package org.hertsig.database;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseUpgrade {
    @Inject
    public DatabaseUpgrade(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations(DatabaseUpgrade.class.getPackage().getName());
        flyway.migrate();
    }
}
