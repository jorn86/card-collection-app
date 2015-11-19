package org.hertsig.dao;

import java.util.List;
import java.util.UUID;

import org.hertsig.database.UuidMapper;
import org.hertsig.dto.Deck;
import org.hertsig.dto.DeckEntry;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

public interface DeckDao extends AutoCloseable {
    @SqlQuery("SELECT * FROM deck WHERE id = :deck")
    @MapResultAsBean
    Deck getDeck(@Bind("deck") UUID deckId);

    @SqlUpdate("INSERT INTO deck (name, userid) VALUES (:name, :user)")
    @GetGeneratedKeys(UuidMapper.class)
    UUID createDeck(@Bind("name") String name, @Bind("user") UUID userId);

    @SqlQuery("SELECT * FROM deckentryview WHERE deckid = :deck")
    @MapResultAsBean
    List<DeckEntry> getCards(@Bind("deck") UUID deckId);

    void close();
}
