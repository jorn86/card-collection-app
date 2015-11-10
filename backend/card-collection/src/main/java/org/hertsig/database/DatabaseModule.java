package org.hertsig.database;

import java.util.UUID;

import javax.inject.Singleton;
import javax.sql.DataSource;

import org.hertsig.contentupgrade.ContentUpgrade;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DataSource.class).toProvider(PostgresqlDatasourceProvider.class).in(Scopes.SINGLETON);
        bind(DatabaseUpgrade.class).asEagerSingleton();
//        bind(ContentUpgrade.class).asEagerSingleton();
    }

    @Singleton @Provides
    public DBI createDBI(DataSource dataSource) {
        DBI dbi = new DBI(dataSource);
        dbi.registerArgumentFactory(new UuidArgumentFactory());
        dbi.registerArgumentFactory(new ListArgumentFactory());
        return dbi;
    }
}
