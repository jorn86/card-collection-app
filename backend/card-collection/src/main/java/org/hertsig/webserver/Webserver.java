package org.hertsig.webserver;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public class Webserver {
    public static void main(String... args) throws Exception {
        ContextHandlerCollection handler = new ContextHandlerCollection();
        handler.setHandlers(new Handler[]{
                new WebAppContext("src/main/webapp", "/backend"),
                new WebAppContext("../../webapp/ROOT.war", "/")
        });

        Server server = new Server(8080);
        server.setHandler(handler);
        server.start();
        server.join();
    }
}
