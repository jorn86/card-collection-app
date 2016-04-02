package org.hertsig.dao;

import org.hertsig.database.UseBetterBeanMapper;
import org.hertsig.database.UuidMapper;
import org.hertsig.dto.*;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

import java.util.List;
import java.util.UUID;

public interface DeckDao extends AutoCloseable {
    @SqlQuery("INSERT INTO \"tag\" (name, parentid, userid) VALUES (:name, :parentid, :userid) RETURNING *")
    @MapResultAsBean
    Tag createTag(@BindBean Tag tag);

    @SqlUpdate("INSERT INTO board (deckid, name, \"order\") VALUES (:deckid, :name, :order)")
    @GetGeneratedKeys(UuidMapper.class)
    UUID createBoard(@BindBean DeckBoard board);

    @SqlQuery("SELECT * FROM board WHERE id = :board")
    @MapResultAsBean
    DeckBoard getBoard(@Bind("board") UUID boardId);

    @SqlQuery("SELECT * FROM board WHERE deckid = :deck AND id != :board ORDER BY board.order")
    @MapResultAsBean
    List<DeckBoard> getOtherBoards(@Bind("deck") UUID deckId, @Bind("board") UUID boardId);

    @SqlQuery("SELECT * FROM board WHERE deckid = :deck ORDER BY board.order")
    @MapResultAsBean
    List<DeckBoard> getBoards(@Bind("deck") UUID deckId);

    @SqlQuery("SELECT * FROM deck WHERE id = :deck")
    @MapResultAsBean
    Deck getDeck(@Bind("deck") UUID deckId);

    @SqlQuery("SELECT * FROM deck WHERE id IN (SELECT deckid FROM board WHERE id = :board)")
    @MapResultAsBean
    Deck getDeckByBoard(@Bind("board") UUID boardId);

    @SqlQuery("INSERT INTO deck (name, userid) VALUES (:name, :user) RETURNING *")
    @UseBetterBeanMapper
    Deck createDeck(@Bind("name") String name, @Bind("user") UUID userId);

    @SqlQuery("SELECT *, (SELECT SUM(amount) FROM inventoryview WHERE :userid = userid AND cardid = deckentryview.cardid) AS inventorycount " +
            "FROM deckentryview WHERE deckid = :deck")
    @MapResultAsBean
    List<DeckEntry> getCardsForDeck(@Bind("deck") UUID deckId, @Bind("userid") UUID userId);

    @SqlQuery("SELECT *, (SELECT SUM(amount) FROM inventoryview WHERE :userid = userid AND cardid = deckentryview.cardid) AS inventorycount " +
            "FROM deckentryview WHERE boardid = :board")
    @MapResultAsBean
    List<DeckEntry> getCardsForBoard(@Bind("board") UUID boardId, @Bind("userid") UUID userId);

    @SqlQuery("INSERT INTO deckrow (boardid, cardid, printingid, amount) VALUES (:boardid, :cardid, :printingid, :amount) RETURNING *")
    @MapResultAsBean
    DeckRow addCardToDeck(@BindBean DeckRow row);

    @SqlQuery("UPDATE deckrow SET amount = :amount WHERE id = :id RETURNING *")
    @MapResultAsBean
    DeckRow updateRow(@BindBean DeckRow row);

    @SqlUpdate("DELETE FROM deckrow WHERE id = :id")
    int deleteRow(@Bind("id") UUID rowId);

    @SqlQuery("SELECT * FROM deckrow WHERE boardid = :boardid AND cardid = :cardid")
    @UseBetterBeanMapper
    List<DeckRow> getBoardRows(@Bind("boardid") UUID boardId, @Bind("cardid") int cardId);

    @SqlQuery("SELECT deckrow.* FROM deckrow LEFT JOIN board ON board.id = deckrow.boardid WHERE deckid = :deckid AND cardid = :cardid")
    @UseBetterBeanMapper
    List<DeckRow> getDeckRows(@Bind("deckid") UUID deckid, @Bind("cardid") int cardId);

    @SqlUpdate("INSERT INTO decktag (tagid, deckid) VALUES (:tagid, :deckid)")
    void addTag(@Bind("deckid") UUID deckId, @Bind("tagid") UUID tagId);

    @SqlUpdate("DELETE FROM decktag WHERE deckid = :deckid")
    void clearTags(@Bind("deckid") UUID deckId);

    @SqlUpdate("UPDATE deck SET name = :name WHERE id = :deckid")
    void updateDeckName(@Bind("deckid") UUID deckId, @Bind("name") String name);

    @SqlUpdate("UPDATE board SET name = :name WHERE id = :boardid")
    void updateBoardName(@Bind("boardid") UUID boardId, @Bind("name") String name);

    @SqlUpdate("DELETE FROM board WHERE id = :board")
    void deleteBoard(@Bind("board") UUID boardId);

    @SqlUpdate("UPDATE deckrow SET boardid = :target WHERE boardid = :source")
    void mergeBoard(@Bind("source") UUID source, @Bind("target") UUID target);

    void close();

}
