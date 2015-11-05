package org.hertsig.database;

import javax.inject.Provider;
import javax.sql.DataSource;

import org.postgresql.ds.PGPoolingDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostgresqlDatasourceProvider implements Provider<DataSource> {
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
        dataSource.setUser("cardcollection");
        dataSource.setPassword("cardcollection");
        dataSource.setDatabaseName("cardcollection");
        return dataSource;
    }
}
