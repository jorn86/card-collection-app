package org.hertsig.logic;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class LogicModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DeckManager.class).in(Scopes.SINGLETON);
        bind(DeckboxImport.class).in(Scopes.SINGLETON);
    }
}
