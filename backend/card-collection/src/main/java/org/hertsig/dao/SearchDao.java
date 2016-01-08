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
    @SqlQuery("SELECT id, name FROM card WHERE normalizedname ILIKE :name " +
            "AND splitcardparent IS NULL AND doublefacefront IS NULL ORDER BY name LIMIT 20")
    @UseBetterBeanMapper
    List<Card> searchCardsByName(@Bind("name") String name);

    @SqlQuery("SELECT * FROM setstatistics")
    @MapResultAsBean
    List<SetInfo> getSetStatistics();

    void close();
}
