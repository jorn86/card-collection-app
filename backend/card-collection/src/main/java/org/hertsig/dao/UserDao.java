package org.hertsig.dao;

import java.util.UUID;

import org.hertsig.database.UuidMapper;
import org.hertsig.dto.User;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

public interface UserDao extends AutoCloseable {
    @SqlQuery("SELECT * FROM \"user\" WHERE id = :id")
    @MapResultAsBean
    User get(@Bind("id") UUID id);

    @SqlUpdate("INSERT INTO \"user\" (name, email) VALUES (:name, :email)")
    @GetGeneratedKeys(UuidMapper.class)
    UUID create(@BindBean User user);

    @SqlUpdate("UPDATE \"user\" SET inventoryid = :inventory WHERE id = :user AND inventoryid IS NULL")
    int setInventory(@Bind("user") UUID createdUserId, @Bind("inventory") UUID inventoryId);

    @SqlUpdate("INSERT INTO deck (name, userid, inventory) VALUES (:name, :user, TRUE)")
    @GetGeneratedKeys(UuidMapper.class)
    UUID createInventory(@Bind("name") String name, @Bind("user") UUID userId);

    void close();

}
