package org.hertsig.startup;

import com.google.inject.Inject;

import javax.inject.Singleton;
import java.util.Set;

@Singleton
public class StartupActions {
    @Inject
    public StartupActions(Set<StartupAction> actions) {
        new Thread(() -> actions.forEach(Runnable::run), "Startup actions").start();
    }
}
