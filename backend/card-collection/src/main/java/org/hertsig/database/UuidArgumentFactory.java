package org.hertsig.database;

import java.util.UUID;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

public class UuidArgumentFactory implements ArgumentFactory<UUID> {
    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx) {
        return value instanceof UUID;
    }

    @Override
    public Argument build(Class<?> expectedType, UUID value, StatementContext ctx) {
        return (position, statement, ctx1) -> statement.setObject(position, value);
    }
}
