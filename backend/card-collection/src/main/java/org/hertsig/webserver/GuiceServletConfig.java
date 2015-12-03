package org.hertsig.webserver;

import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;

import org.hertsig.database.DatabaseModule;
import org.hertsig.startup.StartupActionModule;
import org.postgresql.ds.PGPoolingDataSource;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuiceServletConfig extends GuiceServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // TODO test
        Injector injector = (Injector) servletContextEvent.getServletContext().getAttribute(Injector.class.getName());
        ((PGPoolingDataSource) injector.getInstance(DataSource.class)).close();
        log.info("Closed data source");
        super.contextDestroyed(servletContextEvent);
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new RestletModule(), new DatabaseModule(), new StartupActionModule(), new AbstractModule() {
            @Override
            protected void configure() {
                binder().disableCircularProxies();
                binder().requireExactBindingAnnotations();
                binder().requireExplicitBindings();
            }
        });
    }
}
