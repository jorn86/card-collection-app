package org.hertsig.startup;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

public class StartupActionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(StartupActions.class).asEagerSingleton();
        Multibinder<StartupAction> binder = Multibinder.newSetBinder(binder(), StartupAction.class);
        binder.addBinding().to(ContentUpgrade.class).in(Scopes.SINGLETON);
        binder.addBinding().to(PreconstructedDecks.class).in(Scopes.SINGLETON);

    }
}
