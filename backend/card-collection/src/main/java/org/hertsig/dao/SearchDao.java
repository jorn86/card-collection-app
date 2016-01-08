package org.hertsig.dao;

import org.hertsig.database.UseBetterBeanMapper;
import org.hertsig.dto.Card;
import org.hertsig.dto.Set;
import org.hertsig.dto.SetInfo;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

import java.util.List;

public interface SearchDao extends AutoCloseable {
    @SqlQuery("SELECT gatherercode, name FROM set ORDER BY releasedate")
    @MapResultAsBean
    List<Set> getAll();

    @SqlQuery("SELECT id, name FROM card WHERE normalizedname ILIKE :name " +
            "AND splitcardparent IS NULL AND doublefacefront IS NULL ORDER BY name LIMIT 20")
    @UseBetterBeanMapper
    List<Card> searchCardsByName(@Bind("name") String name);

    @SqlQuery("SELECT s.*, COUNT(DISTINCT p.cardid) AS cards, COUNT(p.cardid) AS prints " +
            "FROM \"set\" s LEFT JOIN printing p ON s.id=p.setid GROUP BY s.id, name")
    @MapResultAsBean
    List<SetInfo> getSetStatistics();

    void close();
}
