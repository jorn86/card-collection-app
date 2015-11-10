package org.hertsig.webserver;

import java.io.File;

import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.providers.WebAppProvider;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public class Webserver {
    public static void main(String... args) throws Exception {
        WebAppProvider webappProvider = new WebAppProvider();
        webappProvider.setMonitoredDirName("../../webapp/ROOT.war");
        webappProvider.setScanInterval(1); // how often to scan
        webappProvider.setExtractWars(true);
        webappProvider.setTempDir(new File("/opt/jetty/work"));

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { new WebAppContext("src/main/webapp", "/api") });

        DeploymentManager deploymentManager = new DeploymentManager();
        deploymentManager.setContexts(contexts);
        deploymentManager.setContextAttribute(
                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/servlet-api-[^/]*\\.jar$");
        deploymentManager.addAppProvider(webappProvider);

        Server server = new Server(8080);
        server.setHandler(contexts);
        server.addBean(deploymentManager);
        server.start();
        server.join();
    }
}
