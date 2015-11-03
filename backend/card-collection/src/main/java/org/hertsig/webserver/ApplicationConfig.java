package org.hertsig.webserver;

import javax.inject.Inject;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.inject.Injector;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationConfig extends ResourceConfig {
    @Inject
    public ApplicationConfig(ServiceLocator serviceLocator) {
        packages("org.hertsig");
        register(new JacksonJaxbJsonProvider(new ObjectMapper(), JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        serviceLocator.getService(GuiceIntoHK2Bridge.class).bridgeGuiceInjector(JerseyWorkaround.injector);
    }

    /**
     * Jersey does not allow us to inject directly into the {@link ResourceConfig} class, since it uses its own flavor of injection.
     * We inject the injector here so we can combine it with Jersey's injection, and inject into our restlets.
     */
    public static class JerseyWorkaround {
        @Inject
        private static Injector injector;
    }
}
