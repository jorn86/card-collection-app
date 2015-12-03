package org.hertsig.dao;

import java.util.List;
import java.util.UUID;

import org.hertsig.database.UuidMapper;
import org.hertsig.dto.Deck;
import org.hertsig.dto.DeckEntry;
import org.hertsig.dto.DeckRow;
import org.hertsig.dto.Row;
import org.skife.jdbi.v2.sqlobject.*;
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

    @SqlUpdate("INSERT INTO deckrow (deckid, cardid, printingid, amount) VALUES (:deckid, :cardid, :printingid, :amount)")
    @GetGeneratedKeys(UuidMapper.class)
    UUID addCardToDeck(@BindBean DeckRow row);

    @SqlUpdate("UPDATE deckrow SET amount = :amount WHERE id = :id")
    int updateAmount(DeckRow row);

    void close();

}
