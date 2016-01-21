package org.hertsig.database;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.DataSource;

import lombok.AllArgsConstructor;
import org.postgresql.ds.PGPoolingDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
class PostgresqlDatasourceProvider implements Provider<DataSource> {
    private final String user;
    private final String password;
    private final String database;

    @Inject
    public PostgresqlDatasourceProvider() {
        this("cardcollection", "cardcollection", "cardcollection");
    }

    @Override
    public DataSource get() {
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e) {
            log.error("Couldn't load Postgres driver", e);
            throw new IllegalStateException(e);
        }

        PGPoolingDataSource dataSource = new PGPoolingDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setDatabaseName(database);
        return dataSource;
    }
}
