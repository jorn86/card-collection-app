package org.hertsig.dao;

import java.util.List;
import java.util.UUID;

import org.hertsig.database.UseBetterBeanMapper;
import org.hertsig.database.UuidMapper;
import org.hertsig.dto.Card;
import org.hertsig.dto.Printing;
import org.hertsig.dto.Set;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.helpers.MapResultAsBean;

public interface ContentUpgradeDao extends AutoCloseable {
    @SqlQuery("SELECT * FROM set WHERE gatherercode = :code")
    @MapResultAsBean
    Set getSet(@Bind("code") String gathererCode);

    @SqlUpdate("INSERT INTO set (gatherercode, code, mcicode, name, releasedate, type, priority, onlineonly) " +
            "VALUES (:gatherercode, :code, :mcicode, :name, :releasedate, :type, :priority, :onlineonly)")
    @GetGeneratedKeys
    int createSet(@BindBean Set set);

    @SqlQuery("SELECT * FROM card WHERE name = :name")
    @UseBetterBeanMapper
    Card getCard(@Bind("name") String name);

    @SqlUpdate("INSERT INTO card (name, normalizedname, fulltype, supertypes, types, subtypes, cost, cmc, colors, text, " +
            "power, toughness, loyalty, layout, splitcardparent, doublefacefront) " +
        "VALUES (:name, unaccent(replace(:name, 'Æ', 'Ae')), :fulltype, :supertypes, :types, :subtypes, :cost, :cmc, :colors, :text, " +
            ":power, :toughness, :loyalty, :layout, :splitcardparent, :doublefacefront)")
    @GetGeneratedKeys
    int createCard(@BindBean Card card);

    @SqlQuery("SELECT * FROM printing WHERE setid = :setid AND cardid = :cardid")
    @MapResultAsBean
    List<Printing> getPrintings(@Bind("setid") int setId, @Bind("cardid") int cardId);

    @SqlQuery("SELECT * FROM printing WHERE cardid = :cardid")
    @MapResultAsBean
    List<Printing> getPrintings(@Bind("cardid") int cardId);

    @SqlUpdate("INSERT INTO printing (setid, cardid, multiverseid, number, rarity, originaltext, originaltype, flavortext) " +
            "VALUES (:setid, :cardid, :multiverseid, :number, :rarity, :originaltext, :originaltype, :flavortext)")
    @GetGeneratedKeys
    int createPrinting(@BindBean Printing printing);

    @SqlUpdate("UPDATE card SET splitcardparent = :parent WHERE id = :card")
    void setParent(@Bind("card") int childId, @Bind("parent") int parentId);

    @SqlUpdate("UPDATE card SET doublefacefront = :front WHERE id = :back")
    void setFlipFront(@Bind("front") int front, @Bind("back") int back);

    void close();

}
