package org.hertsig.startup;

class StartupActionException extends Exception {
    public StartupActionException(Throwable cause) {
        super(cause);
    }

    public StartupActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
