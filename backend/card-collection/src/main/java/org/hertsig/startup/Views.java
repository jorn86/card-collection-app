package org.hertsig.startup;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Singleton
class Views implements StartupAction {
    private static final List<String> names = ImmutableList.of("latestprinting", "deckentryview");
    @Inject private DataSource dataSource;

    @Override
    public void run() throws StartupActionException {
        try (Connection connection = dataSource.getConnection()) {
            for (String name : names) {
                log.debug("Updating view {}", name);
                String query = CharStreams.toString(new InputStreamReader(Views.class.getResourceAsStream(name + ".sql")));
                try (Statement create = connection.createStatement()) {
                    create.execute(query);
                }
                catch (SQLException e) {
                    log.debug("Updating view {} failed, retrying", name, e);
                    try (Statement drop = connection.createStatement();
                         Statement recreate = connection.createStatement()) {
                        drop.execute("DROP VIEW IF EXISTS " + name + " CASCADE");
                        recreate.execute(query);
                    }
                }
            }
        }
        catch (SQLException | IOException e) {
            throw new StartupActionException("Exception while updating views", e);
        }
    }
}
