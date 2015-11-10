package org.hertsig.database;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class ListMapper<T> implements ResultSetMapper<List<T>> {
    @Override
    public List<T> map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Array array = r.getArray("colors");
        T[] realArray = (T[]) array.getArray();
        return Arrays.asList(realArray);
    }
}
