package org.hertsig.dao;

import java.util.List;
import java.util.UUID;

import org.hertsig.database.UseBetterBeanMapper;
import org.hertsig.dto.Deck;
import org.hertsig.dto.Tag;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

public interface DecklistDao extends AutoCloseable {
    @SqlQuery("SELECT d.id, d.name, ARRAY(SELECT tagid FROM decktag WHERE deckid=d.id) AS tags FROM deck d WHERE userid = :user AND NOT inventory")
    @UseBetterBeanMapper
    List<Deck> getDecks(@Bind("user") UUID user);

    @SqlQuery("SELECT d.id, d.name, ARRAY(SELECT tagid FROM decktag WHERE deckid=d.id) AS tags FROM deck d WHERE userid IS NULL")
    @UseBetterBeanMapper
    List<Deck> getPreconstructedDecks();

    @SqlQuery("SELECT id, name, parentid FROM tag WHERE userid = :user ORDER BY parentid NULLS FIRST")
    @MapResultAsBean
    List<Tag> getTags(@Bind("user") UUID user);

    @SqlQuery("SELECT id, name, parentid FROM tag WHERE userid IS NULL ORDER BY parentid NULLS FIRST")
    @MapResultAsBean
    List<Tag> getPreconstructedTags();

    void close();

}
