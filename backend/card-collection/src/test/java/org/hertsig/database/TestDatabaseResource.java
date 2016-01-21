package org.hertsig.database;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;
import lombok.extern.slf4j.Slf4j;
import org.hertsig.startup.IntegrationTestViewsModule;
import org.hertsig.startup.StartupAction;
import org.intellij.lang.annotations.Language;
import org.junit.rules.ExternalResource;
import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.skife.jdbi.v2.IDBI;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertNotNull;

@Slf4j
public class TestDatabaseResource extends ExternalResource {
    private final String databaseName;
    private Injector injector;
    private PGSimpleDataSource adminDatasource;

    public TestDatabaseResource(String databaseName) {
        this.databaseName = databaseName.toLowerCase();
    }

    public IDBI getDbi() {
        assertNotNull("Resource not initialized or already closed", injector);
        return injector.getInstance(IDBI.class);
    }

    @Override
    protected void before() throws Throwable {
        this.adminDatasource = new PGSimpleDataSource();
        executeAdmin("CREATE DATABASE " + databaseName);

        this.injector = Guice.createInjector(Modules.override(new DatabaseModule()).with(new AbstractModule() {
            @Override
            protected void configure() {
                bind(DataSource.class).toProvider(new TestDatasourceProvider(databaseName)).in(Scopes.SINGLETON);
            }
        }), new IntegrationTestViewsModule());
        injector.getInstance(StartupAction.class).run(); // runs Views
    }

    @Override
    protected void after() {
        ((PGPoolingDataSource) injector.getInstance(DataSource.class)).close();
        injector = null;
        executeAdmin("DROP DATABASE " + databaseName);
    }

    private boolean executeAdmin(@Language("SQL") String statement) {
        try (Connection c = adminDatasource.getConnection("cardcollection", "cardcollection");
             Statement s = c.createStatement()) {
            return s.execute(statement);
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static class TestDatasourceProvider extends PostgresqlDatasourceProvider {
        public TestDatasourceProvider(String databaseName) {
            super("cardcollection", "cardcollection", databaseName);
        }
    }
}
