package org.hertsig.dao;

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

    @SqlUpdate("INSERT INTO set (gatherercode, code, name) VALUES (:gatherercode, :code, :name)")
    @GetGeneratedKeys(UuidMapper.class)
    UUID create(@BindBean Set set);

    void close();

}
