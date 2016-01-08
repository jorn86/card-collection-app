package org.hertsig.dao;

import java.util.UUID;

import org.hertsig.database.UuidMapper;
import org.hertsig.dto.DeckBoard;
import org.hertsig.dto.Printing;
import org.hertsig.dto.Set;
import org.hertsig.dto.Tag;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

public interface PreconstructedDao extends AutoCloseable {
    @SqlQuery("SELECT * FROM \"tag\" WHERE userid IS NULL AND name = 'Preconstructed'")
    @MapResultAsBean
    Tag getPreconstructedTag();

    @SqlUpdate("INSERT INTO \"tag\" (name, userid) VALUES ('Preconstructed', NULL)")
    void createPreconstructedTag();

    @SqlQuery("SELECT id FROM \"set\" WHERE gatherercode = :set")
    @Mapper(UuidMapper.class)
    UUID getSet(@Bind("set") String setcode);

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
    Printing getPrinting(@Bind("setid") UUID setId, @Bind("name") String cardName);

    @SqlUpdate("INSERT INTO deck (name, userid) VALUES (:name, NULL)")
    @GetGeneratedKeys(UuidMapper.class)
    UUID createPreconstructedDeck(@Bind("name") String name);

    @SqlUpdate("INSERT INTO decktag (tagid, deckid) VALUES (:tagid, :deckid)")
    void addTag(@Bind("deckid") UUID deckId, @Bind("tagid") UUID tagId);

    @SqlUpdate("INSERT INTO deckrow (boardid, cardid, printingid, amount) VALUES (:boardid, :cardid, :printingid, :amount)")
    void addCard(@Bind("boardid") UUID boardid, @Bind("cardid") UUID cardid, @Bind("printingid") UUID printingId, @Bind("amount") int amount);

    @SqlUpdate("INSERT INTO deckrow (boardid, cardid, amount) VALUES (:boardid, (SELECT id FROM card WHERE name = :name LIMIT 1), :amount)")
    void addCard(@Bind("boardid") UUID boardId, @Bind("name") String cardName, @Bind("amount") int amount);

    void close();

}
