package org.hertsig.startup;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.Set;

@Slf4j
@Singleton
public class StartupActions implements Runnable {
    private final Set<StartupAction> actions;

    @Inject
    public StartupActions(Set<StartupAction> actions) {
        this.actions = actions;
        new Thread(this, "Startup actions").start();
    }

    @Override
    public void run() {
        try {
            for (StartupAction action : actions) {
                log.info("Running startup action {}", action.getClass().getSimpleName());
                action.run();
            }
        }
        catch (StartupActionException e) {
            log.error("Exception running startup action", e);
        }
    }
}
