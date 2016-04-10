package org.hertsig.startup;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import lombok.Getter;
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
public class Views implements StartupAction {
    private static final List<String> NAMES = ImmutableList.of("latestprinting", "setstatistics", "searchview",
            "deckboardview", "inventoryview", "deckentryview");
    @Inject private DataSource dataSource;
    @Getter private boolean running = false;

    @Override
    public void run() throws StartupActionException {
        running = true;
        try (Connection connection = dataSource.getConnection()) {
            for (String name : NAMES) {
                log.debug("Updating view {}", name);
                String query = CharStreams.toString(new InputStreamReader(Views.class.getResourceAsStream(name + ".sql")));
                boolean materialized = query.startsWith("CREATE MATERIALIZED VIEW");
                try (Statement update = connection.createStatement()) {
                    update.execute(query);
                }
                catch (SQLException e) {
                    if (!materialized) {
                        log.debug("Updating view {} failed, retrying", name, e);
                    }
                    try (Statement drop = connection.createStatement();
                         Statement recreate = connection.createStatement()) {
                        drop.execute("DROP " + (materialized ? "MATERIALIZED " : "") + "VIEW IF EXISTS " + name + " CASCADE");
                        recreate.execute(query);
                    }
                }
            }
        }
        catch (SQLException | IOException e) {
            running = false;
            throw new StartupActionException("Exception while updating views", e);
        }
        running = false;
    }
}
