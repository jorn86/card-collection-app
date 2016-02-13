package org.hertsig.dao;

import org.hertsig.database.UuidMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.UUID;

public interface ImportDao extends AutoCloseable {
    @SqlQuery("SELECT id FROM \"set\" WHERE name = :name")
    Integer getEditionByName(@Bind("name") String editionName);

    @SqlQuery("SELECT id FROM \"set\" WHERE name LIKE :fallback OR :name LIKE '%' || name || '%'")
    Integer getEditionFallback(@Bind("fallback") String nameFallback, @Bind("name") String editionName);

    @SqlQuery("SELECT id FROM card WHERE name = :name OR normalizedname = :name")
    Integer getCardByName(@Bind("name") String name);

    @SqlQuery("SELECT id FROM printing WHERE cardid = :card AND setid = :set")
    Integer getPrinting(@Bind("card") int card, @Bind("set") int edition);

    @SqlUpdate("INSERT INTO board (name, deckid, \"order\") VALUES (:name, :deck, " +
            "(SELECT \"order\"+1 FROM board WHERE deckid = :deck ORDER BY \"order\" DESC LIMIT 1))")
    @GetGeneratedKeys(UuidMapper.class)
    UUID createBoard(@Bind("deck") UUID collectionId, @Bind("name") String name);

    void close();
}
