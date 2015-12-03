package org.hertsig.dao;

import java.util.List;

import org.hertsig.database.UseBetterBeanMapper;
import org.hertsig.dto.Card;
import org.hertsig.dto.Set;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

public interface SearchDao extends AutoCloseable {
    @SqlQuery("SELECT gatherercode, name FROM set ORDER BY releasedate")
    @MapResultAsBean
    List<Set> getAll();

    @SqlQuery("SELECT id, name FROM card WHERE name ILIKE :name AND splitcardparent IS NULL ORDER BY name LIMIT 20")
    @UseBetterBeanMapper
    List<Card> searchCardsByName(@Bind("name") String name);

    void close();
}
