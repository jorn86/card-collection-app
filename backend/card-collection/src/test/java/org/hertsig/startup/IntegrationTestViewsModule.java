package org.hertsig.startup;

import com.google.inject.AbstractModule;

public class IntegrationTestViewsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(StartupAction.class).to(Views.class);
    }
}
