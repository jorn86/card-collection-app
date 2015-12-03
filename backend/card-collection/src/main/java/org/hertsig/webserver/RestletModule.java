package org.hertsig.webserver;

import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.hertsig.user.UserManager;

import com.google.common.collect.ImmutableMap;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;

class RestletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        requestStaticInjection(ApplicationConfig.JerseyWorkaround.class);

        bind(ServletContainer.class).asEagerSingleton();
        serve("/*").with(ServletContainer.class, ImmutableMap.of(
                ServletProperties.JAXRS_APPLICATION_CLASS, ApplicationConfig.class.getName()
        ));

        bind(UserManager.class).in(RequestScoped.class);
    }
}
