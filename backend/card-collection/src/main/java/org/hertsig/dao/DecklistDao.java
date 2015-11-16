package org.hertsig.dao;

import java.util.List;
import java.util.UUID;

import org.hertsig.dto.Deck;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

public interface DecklistDao extends AutoCloseable {
    @SqlQuery("SELECT id, name FROM deck WHERE userid = :user")
    @MapResultAsBean
    List<Deck> getDecks(@Bind("user") UUID user);

    @SqlQuery("SELECT id, name FROM deck WHERE userid IS NULL")
    @MapResultAsBean
    List<Deck> getPreconstructedDecks();

    void close();
}
