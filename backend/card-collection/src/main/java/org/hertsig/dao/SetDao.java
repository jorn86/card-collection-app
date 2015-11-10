package org.hertsig.dao;

import java.util.List;

import org.hertsig.dto.Set;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

public interface SetDao extends AutoCloseable {
    @SqlQuery("SELECT gatherercode, name FROM set ORDER BY releasedate")
    @MapResultAsBean
    List<Set> getAll();

    void close();
}
