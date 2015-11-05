package org.hertsig.dao;

import java.util.UUID;

import org.hertsig.database.UuidMapper;
import org.hertsig.dto.User;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface AuthenticationOptionDao extends AutoCloseable {
    @SqlQuery("SELECT userid AS id FROM authenticationoption WHERE externalid = :id AND type = :type")
    @Mapper(UuidMapper.class)
    UUID getExistingUser(@BindBean User.AuthenticationOption authenticationOption);

    @SqlUpdate("INSERT INTO authenticationoption (userid, externalid, type) VALUES (:user, :id, :type)")
    @GetGeneratedKeys(UuidMapper.class)
    UUID create(@Bind("user") UUID userId, @BindBean User.AuthenticationOption authenticationOption);

    void close();
}
