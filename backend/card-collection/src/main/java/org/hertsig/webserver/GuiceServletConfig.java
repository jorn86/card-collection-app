package org.hertsig.webserver;

import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuiceServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                log.error("In config");
                requestStaticInjection(ApplicationConfig.JerseyWorkaround.class);

                bind(ServletContainer.class).asEagerSingleton();
                serve("/api/*").with(ServletContainer.class, ImmutableMap.of(
                        ServletProperties.JAXRS_APPLICATION_CLASS, ApplicationConfig.class.getName()
                ));
            }
        });
    }
}
