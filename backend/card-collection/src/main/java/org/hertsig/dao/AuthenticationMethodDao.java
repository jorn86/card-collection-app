package org.hertsig.dao;

import java.util.UUID;

import org.hertsig.database.GeneratedKeyMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface AuthenticationMethodDao extends AutoCloseable {
    @SqlQuery("SELECT userid AS id FROM authenticationmethod WHERE externalid = :id AND type = :type")
    @Mapper(GeneratedKeyMapper.class)
    UUID getExistingUser(@Bind("id") String id, @Bind("type") String type);

    @SqlUpdate("INSERT INTO authenticationmethod (userid, externalid, type) VALUES (:user, :id, :type)")
    void insert(@Bind("user") UUID userId, @Bind("id") String id, @Bind("type") String type);

    void close();
}
