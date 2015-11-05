package org.hertsig.webserver;

import org.hertsig.database.DatabaseModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuiceServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new RestletModule(), new DatabaseModule(), new AbstractModule() {
            @Override
            protected void configure() {
                binder().disableCircularProxies();
                binder().requireExactBindingAnnotations();
                binder().requireExplicitBindings();
            }
        });
    }
}
