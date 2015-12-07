package org.hertsig.startup;

public interface StartupAction {
    void run() throws StartupActionException;
}
