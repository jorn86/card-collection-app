package org.hertsig.startup;

public class StartupActionException extends Exception {
    public StartupActionException(Throwable cause) {
        super(cause);
    }

    public StartupActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
