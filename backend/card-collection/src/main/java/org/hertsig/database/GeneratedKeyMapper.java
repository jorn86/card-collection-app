package org.hertsig.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class GeneratedKeyMapper implements ResultSetMapper<UUID> {
    @Override
    public UUID map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return (UUID) r.getObject("id");
    }
}
