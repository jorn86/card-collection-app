package org.hertsig.dao;

import org.hertsig.database.UseBetterBeanMapper;
import org.hertsig.database.UuidMapper;
import org.hertsig.dto.Deck;
import org.hertsig.dto.DeckEntry;
import org.hertsig.dto.DeckRow;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

import java.util.List;
import java.util.UUID;

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

    @SqlUpdate("UPDATE deckrow SET amount = :amount, printingid = COALESCE(:printingid, printingid) WHERE id = :id")
    int updateRow(@BindBean DeckRow row);

    @SqlUpdate("DELETE FROM deckrow WHERE id = :id")
    int deleteRow(@Bind("id") UUID rowId);

    @SqlQuery("SELECT * FROM deckrow WHERE deckid = :deckid AND cardid = :cardid")
    @UseBetterBeanMapper
    List<DeckRow> getRows(@Bind("deckid") UUID deckid, @Bind("cardid") UUID cardid);

    void close();

}
