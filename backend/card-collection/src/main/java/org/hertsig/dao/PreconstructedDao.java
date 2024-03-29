package org.hertsig.dao;

import org.hertsig.database.UuidMapper;
import org.hertsig.dto.DeckBoard;
import org.hertsig.dto.Printing;
import org.hertsig.dto.Tag;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

import java.util.UUID;

public interface PreconstructedDao extends AutoCloseable {
    @SqlQuery("SELECT * FROM \"tag\" WHERE userid IS NULL AND name = 'Preconstructed'")
    @MapResultAsBean
    Tag getPreconstructedTag();

    @SqlUpdate("INSERT INTO \"tag\" (name, userid) VALUES ('Preconstructed', NULL)")
    void createPreconstructedTag();

    @SqlQuery("SELECT id FROM \"set\" WHERE gatherercode = :set")
    Integer getSet(@Bind("set") String setcode);

    @SqlQuery("SELECT id FROM \"tag\" WHERE parentid = :parent AND name = :name AND userid IS NULL")
    @Mapper(UuidMapper.class)
    UUID getTag(@Bind("parent") UUID parent, @Bind("name") String name);

    @SqlUpdate("INSERT INTO \"tag\" (parentid, name, userid) VALUES (:parentid, :name, NULL)")
    @GetGeneratedKeys(UuidMapper.class)
    UUID createTag(@BindBean Tag tag);

    @SqlUpdate("INSERT INTO board (deckid, name, \"order\") VALUES (:deckid, :name, :order)")
    @GetGeneratedKeys(UuidMapper.class)
    UUID createBoard(@BindBean DeckBoard board);

    @SqlQuery("SELECT d.id FROM deck d LEFT JOIN decktag t ON d.id=t.deckid WHERE t.tagid = :tagid AND d.name = :name AND d.userid IS NULL")
    @Mapper(UuidMapper.class)
    UUID getPreconstructedDeck(@Bind("tagid") UUID tagId, @Bind("name") String name);

    @SqlQuery("SELECT p.* FROM printing p LEFT JOIN card c ON c.id=p.cardid WHERE p.setid = :setid AND c.name = :name")
    @MapResultAsBean
    Printing getPrinting(@Bind("setid") int setId, @Bind("name") String cardName);

    @SqlUpdate("INSERT INTO deck (name, userid) VALUES (:name, NULL)")
    @GetGeneratedKeys(UuidMapper.class)
    UUID createPreconstructedDeck(@Bind("name") String name);

    @SqlUpdate("INSERT INTO decktag (tagid, deckid) VALUES (:tagid, :deckid)")
    void addTag(@Bind("deckid") UUID deckId, @Bind("tagid") UUID tagId);

    @SqlUpdate("INSERT INTO deckrow (boardid, cardid, printingid, amount) VALUES (:boardid, :cardid, :printingid, :amount)")
    void addCard(@Bind("boardid") UUID boardid, @Bind("cardid") int cardid, @Bind("printingid") int printingId, @Bind("amount") int amount);

    @SqlUpdate("INSERT INTO deckrow (boardid, cardid, amount) VALUES (:boardid, (SELECT id FROM card WHERE name = :name LIMIT 1), :amount)")
    void addCard(@Bind("boardid") UUID boardId, @Bind("name") String cardName, @Bind("amount") int amount);

    void close();

    @SqlQuery("SELECT p.* FROM printing p LEFT JOIN \"set\" s ON p.setid = s.id LEFT JOIN card c ON c.id = p.cardid " +
            "WHERE c.name = :name AND s.releasedate <= (SELECT releasedate FROM \"set\" WHERE id = :set) AND s.priority = 1" +
            "ORDER BY s.releasedate DESC LIMIT 1")
    @MapResultAsBean
    Printing getFallbackPrinting(@Bind("name") String cardName, @Bind("set") int defaultSet);
}
