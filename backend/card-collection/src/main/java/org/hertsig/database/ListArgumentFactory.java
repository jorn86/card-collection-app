package org.hertsig.database;

import java.sql.Array;
import java.util.List;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListArgumentFactory implements ArgumentFactory<List<?>> {
    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx) {
        return value instanceof List;
    }

    @Override
    public Argument build(Class<?> expectedType, List<?> value, StatementContext ctx) {
        return (position, statement, ctx1) -> {
            String parameterTypeName = statement.getParameterMetaData().getParameterTypeName(position);
            // type name starts with _, cut it off here.
            Array array = ctx1.getConnection().createArrayOf(parameterTypeName.substring(1), value.toArray());
            statement.setArray(position, array);
        };
    }
}
