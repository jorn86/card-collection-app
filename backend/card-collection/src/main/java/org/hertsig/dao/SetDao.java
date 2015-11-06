package org.hertsig.dao;

import java.util.List;
import java.util.UUID;

import org.hertsig.database.UuidMapper;
import org.hertsig.dto.Set;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

public interface SetDao extends AutoCloseable {
    @SqlQuery("SELECT * FROM set WHERE gatherercode = :code") @MapResultAsBean
    Set get(@Bind("code") String gathererCode);

    @SqlUpdate("INSERT INTO set (gatherercode, code, name, releasedate) VALUES (:gatherercode, :code, :name, :releasedate)")
    @GetGeneratedKeys(UuidMapper.class)
    UUID create(@BindBean Set set);

    @SqlQuery("SELECT gatherercode, name FROM set ORDER BY releasedate") @MapResultAsBean
    List<Set> getAll();

    void close();

}
