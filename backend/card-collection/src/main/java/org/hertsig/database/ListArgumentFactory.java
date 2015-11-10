package org.hertsig.database;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

public class ListArgumentFactory<T> implements ArgumentFactory<List<T>> {
    private final String sqlType;
    private final Class<T> javaType;

    public ListArgumentFactory(String sqlType, Class<T> javaType) {
        this.sqlType = sqlType;
        this.javaType = javaType;
    }

    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx) {
        if (!(value instanceof List) || ((List) value).isEmpty()) {
            return false;
        }
        return javaType.isInstance(((List) value).get(0));
    }

    @Override
    public Argument build(Class<?> expectedType, List<T> value, StatementContext ctx) {
        return (position, statement, ctx1) -> {
            Array array = ctx1.getConnection().createArrayOf(sqlType, value.toArray());
            statement.setArray(position, array);
        };
    }
}
